/*-
 * #%L
 * io.earcam.instrumental.lade
 * %%
 * Copyright (C) 2018 earcam
 * %%
 * SPDX-License-Identifier: (BSD-3-Clause OR EPL-1.0 OR Apache-2.0 OR MIT)
 * 
 * You <b>must</b> choose to accept, in full - any individual or combination of 
 * the following licenses:
 * <ul>
 * 	<li><a href="https://opensource.org/licenses/BSD-3-Clause">BSD-3-Clause</a></li>
 * 	<li><a href="https://www.eclipse.org/legal/epl-v10.html">EPL-1.0</a></li>
 * 	<li><a href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a></li>
 * 	<li><a href="https://opensource.org/licenses/MIT">MIT</a></li>
 * </ul>
 * #L%
 */
package io.earcam.instrumental.lade;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class Handler extends URLStreamHandler {

	static final String JAVA_PROTOCOL_HANDLER_PKGS = "java.protocol.handler.pkgs";


	public static void addProtocolHandlerSystemProperty()
	{
		String property = System.getProperty(JAVA_PROTOCOL_HANDLER_PKGS, "");
		if(property.length() > 0) {
			property += '|';
		}
		System.setProperty(JAVA_PROTOCOL_HANDLER_PKGS, property + parentPackage());
	}


	public static void removeProtocolHandlerSystemProperty()
	{
		String property = System.getProperty(JAVA_PROTOCOL_HANDLER_PKGS, "");

		String parentPackage = parentPackage();

		property = Arrays.stream(property.split("\\|"))
				.filter(p -> !parentPackage.equals(p))
				.collect(Collectors.joining("|"));

		System.setProperty(JAVA_PROTOCOL_HANDLER_PKGS, property);
	}


	static String parentPackage()
	{
		String paquet = Handler.class.getPackage().getName();
		int index = paquet.lastIndexOf('.');
		return paquet.substring(0, index);
	}


	@Override
	protected URLConnection openConnection(URL url)
	{
		return new URLConnection(url) {

			@Override
			public void connect()
			{ /* NOOP */ }


			@Override
			public InputStream getInputStream() throws IOException
			{
				connect();
				return InMemoryClassLoader.getResourceAsStream(url);
			}
		};
	}

}
