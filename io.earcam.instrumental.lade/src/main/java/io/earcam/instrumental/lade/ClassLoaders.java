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

import static io.earcam.unexceptional.Exceptional.swallow;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * ClassLoaders class.
 * </p>
 *
 */
public final class ClassLoaders {

	/**
	 * In case this {@link InMemoryClassLoader} is used as the system classloader (by setting
	 * the system property "<tt>java.system.class.loader</tt>"), we need to hold onto the
	 * original system {@link ClassLoader} use to load this {@link InMemoryClassLoader}
	 */
	private static final ClassLoader JVM_SYSTEM_CLASSLOADER = InMemoryClassLoader.class.getClassLoader();

	private static final class SelfFirstUrlClassLoader extends URLClassLoader {

		private final Map<String, Class<?>> loaded = new HashMap<>();


		public SelfFirstUrlClassLoader(URL[] urls, ClassLoader parent)
		{
			super(urls, parent);
		}


		@Override
		public void close() throws IOException
		{
			loaded.clear();
			super.close();
		}


		@Override
		protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
		{
			try {
				Class<?> wasLoaded = loaded.get(name);
				if(wasLoaded != null) {
					return wasLoaded;
				}
				Class<?> found = findClass(name);
				loaded.put(name, found);
				return found;
			} catch(ClassNotFoundException e) {
				swallow(e);
				return super.loadClass(name, resolve);
			}
		}
	}


	private ClassLoaders()
	{}


	/**
	 * Shortcut for loading a single class - curt but expensive.
	 *
	 * @param bytes bytecode
	 * @return the class defined by the bytecode
	 * @see InMemoryClassLoader#define(byte[])
	 */
	public static Class<?> load(byte[] bytes)
	{
		return load(null, bytes, 0, bytes.length);
	}


	public static Class<?> load(String name, byte[] bytes, int offset, int length)
	{
		try(InMemoryClassLoader classLoader = inMemoryClassLoader()) {
			return classLoader.define(name, bytes, offset, length, null);
		}
	}


	/**
	 * Runs the {@code runnable} with the {@code loader} set as the current threads'
	 * context {@link ClassLoader}, and finally resets the context
	 * 
	 * @param loader the ClassLoader to use in context
	 * @param runnable the executable to run in context
	 * 
	 * @see Thread#setContextClassLoader(ClassLoader)
	 */
	public static void runInContext(ClassLoader loader, Runnable runnable)
	{
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(loader);
			runnable.run();
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}


	/**
	 * <p>
	 * selfFirstClassLoader.
	 * </p>
	 *
	 * @param jars a {@link java.nio.file.Path} object.
	 * @return a {@link java.net.URLClassLoader} object.
	 */
	public static URLClassLoader selfFirstClassLoader(Path... jars)
	{
		return selfFirstClassLoader(ClassLoader.getSystemClassLoader(), jars);
	}


	/**
	 * <p>
	 * selfFirstClassLoader.
	 * </p>
	 *
	 * @param parent a {@link java.lang.ClassLoader} object.
	 * @param jars a {@link java.nio.file.Path} object.
	 * @return a {@link java.net.URLClassLoader} object.
	 */
	public static URLClassLoader selfFirstClassLoader(ClassLoader parent, Path... jars)
	{
		return selfFirstClassLoader(parent, Arrays.stream(jars).map(Path::toUri));
	}


	private static URLClassLoader selfFirstClassLoader(ClassLoader parent, Stream<URI> uris)
	{
		URL[] jars = uris.map(Exceptional.uncheckFunction(URI::toURL)).toArray(s -> new URL[s]);
		return new SelfFirstUrlClassLoader(jars, parent);
	}


	/**
	 * Creates a new in-memory {@link java.lang.ClassLoader} with the system class loader as parent (parent-last order).
	 * To create with no parent, use the single parameter constructor and pass null argument.
	 *
	 * @see ClassLoader#getSystemClassLoader()
	 * @return a {@link io.earcam.instrumental.lade.InMemoryClassLoader} object.
	 */
	public static InMemoryClassLoader inMemoryClassLoader()
	{
		return ClassLoaders.inMemoryClassLoader(JVM_SYSTEM_CLASSLOADER);
	}


	/**
	 * Creates a new in-memory {@link java.lang.ClassLoader} with provided class loader as parent (parent-last order).
	 *
	 * @param parent the parent classloader to delegate to (may be null)
	 * @return a {@link io.earcam.instrumental.lade.InMemoryClassLoader} object.
	 */
	public static InMemoryClassLoader inMemoryClassLoader(ClassLoader parent)
	{
		return new InMemoryClassLoader(parent);
	}


	/**
	 * Creates a new in-memory, isolated {@link java.lang.ClassLoader} <b>without</b> parent
	 * {@link java.lang.ClassLoader}
	 *
	 * @return a {@link io.earcam.instrumental.lade.InMemoryClassLoader} object.
	 */
	public static InMemoryClassLoader orphanedInMemoryClassLoader()
	{
		return new InMemoryClassLoader(null);
	}
}
