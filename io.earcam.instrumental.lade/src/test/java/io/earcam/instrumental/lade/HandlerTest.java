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

import static io.earcam.instrumental.lade.Handler.JAVA_PROTOCOL_HANDLER_PKGS;
import static io.earcam.instrumental.lade.Handler.*;
import static io.earcam.instrumental.lade.Handler.parentPackage;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class HandlerTest {

	@Test
	public void doesNotAppendPipeWhenAddingOnlyProtocolHandler()
	{
		setProperty(JAVA_PROTOCOL_HANDLER_PKGS, "");
		addProtocolHandlerSystemProperty();

		assertThat(getProperty(JAVA_PROTOCOL_HANDLER_PKGS), is(equalTo(parentPackage())));
	}


	@Test
	public void appendsPipeWhenProtocolHandlerPackagesExist()
	{
		setProperty(JAVA_PROTOCOL_HANDLER_PKGS, "com.acme");
		addProtocolHandlerSystemProperty();

		assertThat(getProperty(JAVA_PROTOCOL_HANDLER_PKGS), is(equalTo("com.acme|" + parentPackage())));
	}


	@Test
	public void removesSelfWhenOnlyProtocolHandler()
	{
		setProperty(JAVA_PROTOCOL_HANDLER_PKGS, "");
		addProtocolHandlerSystemProperty();

		removeProtocolHandlerSystemProperty();

		assertThat(getProperty(JAVA_PROTOCOL_HANDLER_PKGS), is(emptyString()));
	}


	@Test
	public void removesSelfWhenMultipleProtocolHandlersPresent()
	{
		setProperty(JAVA_PROTOCOL_HANDLER_PKGS, "com.acme.a");
		addProtocolHandlerSystemProperty();
		setProperty(JAVA_PROTOCOL_HANDLER_PKGS, getProperty(JAVA_PROTOCOL_HANDLER_PKGS) + "|com.acme.b|com.acme.c");

		removeProtocolHandlerSystemProperty();

		assertThat(getProperty(JAVA_PROTOCOL_HANDLER_PKGS), is(equalTo("com.acme.a|com.acme.b|com.acme.c")));
	}
}
