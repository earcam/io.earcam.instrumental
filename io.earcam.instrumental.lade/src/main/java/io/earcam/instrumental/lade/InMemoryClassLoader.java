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
import static java.util.Collections.emptyEnumeration;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static java.util.Locale.ROOT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.SecureClassLoader;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import javax.annotation.concurrent.ThreadSafe;

import io.earcam.unexceptional.Exceptional;
import io.earcam.utilitarian.io.IoStreams;
import io.earcam.utilitarian.security.Signatures;

/**
 * <p>
 * A {@link java.lang.ClassLoader} that doesn't require a file system.
 * </p>
 * 
 * <p>
 * Class/resource loading strategy is self-first. With multiple JARs added,
 * the search is linear in-order; so "hiding" is possible as per classpath.
 * </p>
 * 
 * <p>
 * Current partial support for signed JARs; the certificate will be loaded
 * and {@link CodeSigner}/{@link CodeSource} associated with loaded classes,
 * however no validation is performed. RSA only.
 * </p>
 */
@ThreadSafe
public final class InMemoryClassLoader extends SecureClassLoader implements AutoCloseable {

	static {
		InMemoryClassLoader.registerAsParallelCapable();
	}

	static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";

	static final ConcurrentMap<String, WeakReference<InMemoryClassLoader>> loaders = new ConcurrentHashMap<>();

	/**
	 * The URL protocol used by resources from this {@link ClassLoader}.
	 * Note: this is only valid for URLs returned by this instance as they
	 * include a handler which maintain a reference to this instance.
	 * 
	 * You may register the protocol handler to load URLs within the same JVM.
	 * 
	 * @see Handler#addProtocolHandlerSystemProperty()
	 */
	public static final String URL_PROTOCOL = "lade";

	// Map of resource-name to map of resource-owner-name to resource
	private final ConcurrentMap<String, Map<String, byte[]>> resources = new ConcurrentHashMap<>();

	private final ConcurrentMap<String, CodeSource> codeSources = new ConcurrentHashMap<>();

	private final ConcurrentMap<String, Class<?>> loaded = new ConcurrentHashMap<>();

	private final Handler handler = new Handler();

	private boolean addCodeSources = true;


	/**
	 * Exposed for use as the "<tt>java.system.class.loader</tt>"
	 *
	 * @param parent a {@link java.lang.ClassLoader} instance.
	 * @see ClassLoader#getSystemClassLoader()
	 */
	public InMemoryClassLoader(ClassLoader parent)
	{
		super(parent);
		loaders.put(identityHashCodeHex(this), new WeakReference<InMemoryClassLoader>(this));
	}


	/**
	 * <p>
	 * By default the {@link InMemoryClassLoader} adds the {@link CodeSigner}/{@link CodeSource}
	 * details where found.
	 * </p>
	 * <p>
	 * Invoking this method disables this behaviour, but only for JARs added subsequently.
	 * </p>
	 * 
	 * @return
	 */
	public InMemoryClassLoader doNotAddSubsequentSignatures()
	{
		this.addCodeSources = false;
		return this;
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
		if(addCodeSources) {
			addCodeSource(name);
		}
		return this;
	}


	void addManifest(JarInputStream jar, String name) throws IOException
	{
		if(jar.getManifest() != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			jar.getManifest().write(os);
			resources.computeIfAbsent(MANIFEST_PATH, k -> new ConcurrentHashMap<>()).put(name, os.toByteArray());
		}
	}


	void addJarEntries(JarInputStream jar, String name) throws IOException
	{
		JarEntry jarEntry;
		try(JarInputStream wrap = jar) {
			while((jarEntry = wrap.getNextJarEntry()) != null) {
				resources.computeIfAbsent(jarEntry.getName(), k -> new ConcurrentHashMap<>()).put(name, IoStreams.readAllBytes(wrap));
			}
		}
	}


	private void addCodeSource(String name)
	{
		byte[] bytes = resources.keySet().stream()
				.filter(r -> r.startsWith("META-INF/") && r.endsWith(".SF"))
				.map(r -> r.substring(0, r.length() - 2) + "RSA")
				.map(this::resourcesForName)
				.map(m -> m.get(name))
				.filter(Objects::nonNull)
				.findFirst().orElse(new byte[0]);

		if(bytes.length != 0) {
			X509Certificate[] certificates = Signatures.certificatesFromSignature(bytes).stream().toArray(s -> new X509Certificate[s]);
			codeSources.put(name, new CodeSource(createResourceUrl(name, ""), certificates));
		}
	}


	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		Class<?> wasLoaded = loaded.get(name);
		if(wasLoaded != null) {
			return wasLoaded;
		}
		String className = classToResourceName(name);
		Iterator<Entry<String, byte[]>> iterator = resourcesForName(className).entrySet().iterator();
		if(!iterator.hasNext()) {
			if(getParent() == null) {
				throw new ClassNotFoundException(name);
			}
			return getParent().loadClass(name);
		}
		Entry<String, byte[]> resource = iterator.next();
		byte[] bytes = resource.getValue();
		Class<?> defined = defineClass(name, bytes, 0, bytes.length, codeSources.get(resource.getKey()));
		loaded.put(name, defined);
		return defined;
	}


	private static String classToResourceName(String name)
	{
		return name.replace('.', '/') + ".class";
	}


	private Map<String, byte[]> resourcesForName(String resource)
	{
		return resources.getOrDefault(stripLeadingSlash(resource), emptyMap());
	}


	@Override
	public void close()
	{
		resources.clear();
		loaded.clear();
		loaders.remove(identityHashCodeHex(this));
	}


	@Override
	public InputStream getResourceAsStream(String name)
	{
		Iterator<byte[]> iterator = resourcesForName(name).values().iterator();
		if(!iterator.hasNext()) {
			return super.getResourceAsStream(name);
		}
		return new ByteArrayInputStream(iterator.next());
	}


	private static String stripLeadingSlash(String name)
	{
		return (name.length() > 0 && name.charAt(0) == '/') ? name.substring(1) : name;
	}


	static InputStream getResourceAsStream(URL earl) throws IOException
	{
		String host = earl.getHost();
		int index = host.indexOf('.');
		String loaderId = host.substring(0, index);
		String archiveId = host.substring(index + 1);

		InMemoryClassLoader loader = InMemoryClassLoader.loaders.get(loaderId).get();

		String resource = stripLeadingSlash(earl.getPath());

		if(resource.isEmpty()) {
			// We must now serialize the entire JAR for the given archiveId ... as something
			// is trying to look up the JAR having trimmed off the resource path

			return loader.serializeJar(archiveId);
		}
		byte[] bytes = loader.resourcesForName(resource).get(archiveId);

		return (bytes == null) ? null : new ByteArrayInputStream(bytes);
	}


	private InputStream serializeJar(String archiveId) throws IOException
	{
		byte[] manifestBytes = resourcesForName(MANIFEST_PATH).get(archiveId);
		Manifest manifest = new Manifest(new ByteArrayInputStream(manifestBytes));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(JarOutputStream output = new JarOutputStream(baos, manifest)) {

			for(Entry<String, Map<String, byte[]>> entrySet : resources.entrySet()) {

				if(!MANIFEST_PATH.equals(entrySet.getKey()) && entrySet.getValue().containsKey(archiveId)) {
					JarEntry jarEntry = new JarEntry(entrySet.getKey());
					byte[] bytes = entrySet.getValue().get(archiveId);
					jarEntry.setSize(bytes.length);
					output.putNextEntry(jarEntry);
					output.write(bytes);
				}
			}
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}


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


	@Override
	public Enumeration<URL> getResources(String name)
	{
		return getResources(name, true);
	}


	public Enumeration<URL> getResources(String name, boolean includeParents)
	{
		Iterator<Entry<String, byte[]>> iterator = resourcesForName(name).entrySet().iterator();

		Enumeration<URL> superResources = includeParents ? Exceptional.apply(super::getResources, name) : emptyEnumeration();

		return new Enumeration<URL>() {

			@Override
			public boolean hasMoreElements()
			{
				return iterator.hasNext() || superResources.hasMoreElements();
			}


			@Override
			public URL nextElement()
			{
				if(iterator.hasNext()) {
					String archiveId = iterator.next().getKey();
					return createResourceUrl(archiveId, name);
				}
				return superResources.nextElement();
			}
		};
	}


	public Stream<URL> resources()
	{
		return resources.entrySet()
				.stream()
				.flatMap(e -> e.getValue().entrySet().stream().map(r -> createResourceUrl(r.getKey(), e.getKey())));
	}


	private URL createResourceUrl(String archiveId, String path)
	{
		String host = identityHashCodeHex(this) + '.' + archiveId;
		return Exceptional.url(URL_PROTOCOL, host, 0, "/" + path, handler);
	}


	public Class<?> define(String name, byte[] bytes, CodeSource codeSource)
	{
		return define(name, bytes, 0, bytes.length, codeSource);
	}


	public Class<?> define(String name, byte[] bytes, int offset, int length, CodeSource codeSource)
	{
		return defineClass(name, bytes, offset, length, codeSource);
	}
}
