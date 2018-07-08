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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static java.util.Locale.ROOT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Stream;

import io.earcam.unexceptional.Exceptional;

/**
 * A {@link java.lang.ClassLoader} that doesn't require a file system.
 *
 * Class/resource loading strategy is parent last.
 *
 * TODO does not provide for `CodeSource` and `ProtectionDomain`
 *
 */
public class InMemoryClassLoader extends ClassLoader implements AutoCloseable {

	static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";

	/**
	 * The URL protocol used by resources from this {@link ClassLoader}.
	 * Note: this is only valid for URLs returned by this instance as they
	 * include a handler which maintain a reference to this instance.
	 * More specifically; no URLStreamHandler is globally registered for
	 * this fake "protocol".
	 */
	public static final String URL_PROTOCOL = "mental";

	private class MentalHandler extends URLStreamHandler {

		@Override
		protected URLConnection openConnection(URL url)
		{
			return new URLConnection(url) {

				@Override
				public void connect()
				{ /* NOOP */ }


				@Override
				public InputStream getInputStream()
				{
					return getResourceAsStream(url.getPath());
				}
			};
		}
	}

	// Map of resource-name to map of resource-owner-name to resource
	private final Map<String, Map<String, byte[]>> resources = new HashMap<>();

	private final MentalHandler handler = new MentalHandler();


	/**
	 * Exposed for use as "<tt>java.system.class.loader</tt>"
	 *
	 * @param parent a {@link java.lang.ClassLoader} object.
	 * @see ClassLoader#getSystemClassLoader()
	 */
	public InMemoryClassLoader(ClassLoader parent)
	{
		super(parent);
	}


	/**
	 * <p>
	 * jars.
	 * </p>
	 *
	 * @param jars an array of {@link byte} objects.
	 * @return a {@link io.earcam.instrumental.lade.InMemoryClassLoader} object.
	 */
	public InMemoryClassLoader jars(byte[]... jars)
	{
		return jars(asList(jars));
	}


	/**
	 * <p>
	 * jars.
	 * </p>
	 *
	 * @param jars a {@link java.util.Collection} object.
	 * @return a {@link io.earcam.instrumental.lade.InMemoryClassLoader} object.
	 */
	public InMemoryClassLoader jars(Collection<byte[]> jars)
	{
		return jars(jars.stream());
	}


	/**
	 * <p>
	 * jars.
	 * </p>
	 *
	 * @param jars a {@link java.util.stream.Stream} object.
	 * @return a {@link io.earcam.instrumental.lade.InMemoryClassLoader} object.
	 */
	public InMemoryClassLoader jars(Stream<byte[]> jars)
	{
		jars.forEach(j -> jar(j, identityHashCodeHex(j)));
		return this;
	}


	private static String identityHashCodeHex(Object object)
	{
		return Integer.toString(System.identityHashCode(object), 16).toUpperCase(ROOT);
	}


	/**
	 * <p>
	 * jar.
	 * </p>
	 *
	 * @param jar an array of {@link byte} objects.
	 * @return a {@link io.earcam.instrumental.lade.InMemoryClassLoader} object.
	 */
	public InMemoryClassLoader jar(byte[] jar)
	{
		return jars(singleton(jar));
	}


	/**
	 * <p>
	 * jar.
	 * </p>
	 *
	 * @param jar adds the byte array representing a jar's contents
	 * @param name the "name" to use for this jar (for URL resource references etc)
	 * @return a {@link io.earcam.instrumental.lade.InMemoryClassLoader} object.
	 */
	public InMemoryClassLoader jar(byte[] jar, String name)
	{
		jar(new ByteArrayInputStream(jar), name);
		return this;
	}


	/**
	 * <p>
	 * jar.
	 * </p>
	 *
	 * @param jar adds the {@link java.io.InputStream} representing a jar's contents
	 * @param name the "name" to use for this jar (for URL resource references etc)
	 * @return a {@link io.earcam.instrumental.lade.InMemoryClassLoader} object.
	 */
	public InMemoryClassLoader jar(InputStream jar, String name)
	{
		Exceptional.run(() -> jar(new JarInputStream(jar), name));
		return this;
	}


	private InMemoryClassLoader jar(JarInputStream jar, String name) throws IOException
	{
		addManifest(jar, name);
		addJarEntries(jar, name);
		return this;
	}


	void addManifest(JarInputStream jar, String name) throws IOException
	{
		if(jar.getManifest() != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			jar.getManifest().write(os);
			resources.computeIfAbsent(MANIFEST_PATH, k -> new HashMap<>()).put(name, os.toByteArray());
		}
	}


	void addJarEntries(JarInputStream jar, String name) throws IOException
	{
		JarEntry jarEntry;
		while((jarEntry = jar.getNextJarEntry()) != null) {
			resources.computeIfAbsent(jarEntry.getName(), k -> new HashMap<>()).put(name, inputStreamToBytes(jar));
		}
	}


	static byte[] inputStreamToBytes(InputStream input) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int read = input.read();
		while(read != -1) {
			baos.write(read);
			read = input.read();
		}
		return baos.toByteArray();
	}


	/** {@inheritDoc} */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		String className = classToResourceName(name);
		Iterator<byte[]> iterator = resourcesForName(className).values().iterator();

		if(!iterator.hasNext()) {
			if(getParent() == null) {
				throw new ClassNotFoundException(name);
			}
			return getParent().loadClass(name);
		}
		byte[] resource = iterator.next();
		return defineClass(name, resource, 0, resource.length);
	}


	static String classToResourceName(String name)
	{
		return name.replaceAll("\\.", "/") + ".class";
	}


	private Map<String, byte[]> resourcesForName(String resource)
	{
		return resources.getOrDefault(resource, emptyMap());
	}


	/** {@inheritDoc} */
	@Override
	public void close()
	{
		resources.clear();
	}


	/** {@inheritDoc} */
	@Override
	public InputStream getResourceAsStream(String name)
	{
		String resource = (name.charAt(0) == '/') ? name.substring(1) : name;
		Iterator<byte[]> iterator = resourcesForName(resource).values().iterator();
		if(!iterator.hasNext()) {
			return super.getResourceAsStream(resource);
		}
		return new ByteArrayInputStream(iterator.next());
	}


	/** {@inheritDoc} */
	@Override
	public URL getResource(String name)
	{
		Enumeration<URL> earls;
		earls = getResources(name);
		if(earls.hasMoreElements()) {
			return earls.nextElement();
		}
		return super.getResource(name);
	}


	/** {@inheritDoc} */
	@Override
	public Enumeration<URL> getResources(String name)
	{
		Iterator<Entry<String, byte[]>> iterator = resourcesForName(name).entrySet().iterator();

		return new Enumeration<URL>() {

			@Override
			public boolean hasMoreElements()
			{
				return iterator.hasNext();
			}


			@Override
			public URL nextElement()
			{
				String resourceOwner = iterator.next().getKey();
				return Exceptional.url(URL_PROTOCOL, resourceOwner, 0, (name.charAt(0) != '/') ? "/" + name : name, handler);
			}
		};
	}


	/**
	 * Exposes functionality of {@link #defineClass(String, java.nio.ByteBuffer, java.security.ProtectionDomain)}
	 *
	 * @param bytes bytecode
	 * @return the class defined by the bytecode
	 */
	public Class<?> define(byte[] bytes)
	{
		return defineClass(null, bytes, 0, bytes.length, null);
	}
}
