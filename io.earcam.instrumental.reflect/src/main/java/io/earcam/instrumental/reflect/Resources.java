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

import java.io.InputStream;
import java.net.URI;

import io.earcam.unexceptional.Exceptional;
import io.earcam.utilitarian.io.IoStreams;

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
	 * Load the bytecode for a given class
	 * </p>
	 *
	 * @param type the subject class.
	 * @return the bytecode as an array.
	 * 
	 * @see #classAsStream(Class)
	 */
	public static byte[] classAsBytes(Class<?> type)
	{
		return IoStreams.readAllBytes(classAsStream(type));
	}


	/**
	 * <p>
	 * Load a class' bytecode as an InputStream.
	 * </p>
	 *
	 * @param type the subject class.
	 * @return the bytecode as an InputStream.
	 * 
	 * @see #classAsBytes(Class)
	 */
	public static InputStream classAsStream(Class<?> type)
	{
		return type.getClassLoader().getResourceAsStream(typeToResourceName(type));
	}


	/**
	 * <p>
	 * Find the origin of a given class (typically a path to a JAR or class file).
	 * </p>
	 * <p>
	 * <p>
	 * Uses the {@code type} arguments ClassLoader.
	 * <p>
	 *
	 * @param type the subject class.
	 * @return the location source of the resource class.
	 * 
	 * @see #sourceOfResource(Class, ClassLoader)
	 */
	public static String sourceOfResource(Class<?> type)
	{
		ClassLoader classLoader = type.getClassLoader();
		return sourceOfResource(type, classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader);
	}


	/**
	 * <p>
	 * Find the origin of a given class (typically a path to a JAR or class file).
	 * </p>
	 *
	 * @param type the subject class.
	 * @param classLoader the ClassLoader to load from.
	 * @return the location source of the resource class.
	 * 
	 * @see #sourceOfResource(Class)
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
	 * Remove JAR URL decoration. Specifically the "jar" and "file" protocols, as well as the <i>separator</i>
	 * ({@code !/}).
	 * </p>
	 *
	 * @param jar a {@link java.net.URI} object.
	 * @return a {@link java.lang.String} object.
	 * 
	 * @see java.net.JarURLConnection
	 */
	public static String removeJarUrlDecoration(URI jar)
	{
		return jar.toString()
				.replaceFirst("^jar:file:", "")
				.replaceFirst("^file:(//)?", "")
				.replaceAll("!/$", "");
	}
}
