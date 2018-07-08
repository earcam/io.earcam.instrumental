/*-
 * #%L
 * io.earcam.instrumental.reflect
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
 */package io.earcam.instrumental.reflect;

import static io.earcam.instrumental.reflect.Names.typeToResourceName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;

import javax.annotation.WillClose;

import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * Resources class.
 * </p>
 *
 */
public final class Resources {

	private Resources()
	{}


	/**
	 * <p>
	 * classAsBytes.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @return an array of {@link byte} objects.
	 */
	public static byte[] classAsBytes(Class<?> type)
	{
		return readAllBytes(classAsStream(type));
	}


	static byte[] readAllBytes(@WillClose InputStream input)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int read;
		try(InputStream in = input) {
			while((read = in.read()) != -1) {
				baos.write(read);
			}
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
		return baos.toByteArray();
	}


	/**
	 * <p>
	 * classAsStream.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @return a {@link java.io.InputStream} object.
	 */
	public static InputStream classAsStream(Class<?> type)
	{
		return type.getClassLoader().getResourceAsStream(typeToResourceName(type));
	}


	/**
	 * <p>
	 * sourceOfResource.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String sourceOfResource(Class<?> type)
	{
		ClassLoader classLoader = type.getClassLoader();
		return sourceOfResource(type, classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader);
	}


	/**
	 * <p>
	 * sourceOfResource.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @param classLoader a {@link java.lang.ClassLoader} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String sourceOfResource(Class<?> type, ClassLoader classLoader)
	{
		String className = Names.typeToResourceName(type);
		String resource = removeJarUrlDecoration(Exceptional.uri(classLoader.getResource(className)));
		int shebang = resource.indexOf('!');
		return shebang > -1 ? resource.substring(0, shebang)
				: resource.substring(0, resource.length() - className.length());
	}


	/**
	 * <p>
	 * removeJarUrlDecoration.
	 * </p>
	 *
	 * @param jar a {@link java.net.URI} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String removeJarUrlDecoration(URI jar)
	{
		return jar.toString()
				.replaceFirst("^jar:file:", "")
				.replaceFirst("^file:(//)?", "")
				.replaceAll("!/$", "");
	}
}
