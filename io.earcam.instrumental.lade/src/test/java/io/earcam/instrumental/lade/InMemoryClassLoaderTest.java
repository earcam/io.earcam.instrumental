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

import static io.earcam.instrumental.lade.InMemoryClassLoader.MANIFEST_PATH;
import static io.earcam.instrumental.lade.InMemoryClassLoader.URL_PROTOCOL;
import static io.earcam.instrumental.lade.InMemoryClassLoader.classToResourceName;
import static io.earcam.instrumental.lade.InMemoryClassLoader.inputStreamToBytes;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.junit.Test;

import io.earcam.unexceptional.Exceptional;

public class InMemoryClassLoaderTest {

	@Test
	public void identicallyNamedClassesLoadedAsResourcesFromMulitpleJars() throws IOException
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader()) {
			classLoader.jar(createJar("id", "1"), "one");
			classLoader.jar(createJar("id", "2"), "two");

			String name = classToResourceName(InMemoryClassLoaderTest.class.getCanonicalName());
			Enumeration<URL> resources = classLoader.getResources(name);

			List<URL> earls = enumerationToList(resources);

			assertThat(earls, hasSize(2));
		}
	}


	private <T> List<T> enumerationToList(Enumeration<T> resources)
	{
		ArrayList<T> list = new ArrayList<>();
		while(resources.hasMoreElements()) {
			list.add(resources.nextElement());
		}
		return list;
	}


	private byte[] createJar(String manifestKey, String manifestValue)
	{
		return Exceptional.get(() -> inputStreamToBytes(bundle()
				.add(InMemoryClassLoaderTest.class)
				.set(manifestKey, manifestValue)
				.build()));
	}


	@Test
	public void classFromJarsCanBeLoaded() throws ClassNotFoundException
	{
		canLoadClass(InMemoryClassLoaderTest.class.getCanonicalName());
	}


	public void canLoadClass(String canonicalName) throws ClassNotFoundException
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jar(createJar("SomeKey", "SomeValue"))) {
			Class<?> loaded = classLoader.loadClass(canonicalName);
			assertThat(loaded.getCanonicalName(), is(equalTo(canonicalName)));
		}
	}


	@Test
	public void classFromSystemCanBeLoaded() throws ClassNotFoundException
	{
		canLoadClass(String.class.getCanonicalName());
	}


	@Test
	public void resourceUrlCanBeStreamed() throws IOException
	{
		String key = "SomeKey";
		String value = "SomeValue";
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jars(createJar(key, value))) {

			URL resource = classLoader.getResource(MANIFEST_PATH);

			assertThat(resource.getProtocol(), is(equalTo(URL_PROTOCOL)));

			String contents = new String(inputStreamToBytes(resource.openStream()), UTF_8);

			assertThat(contents, containsString(key + ": " + value));
		}
	}


	@Test
	public void unknownResourceReturnsNull()
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jars(createJar("SomeKey", "SomeValue"), createJar("OtherKey", "OtherValue"))) {

			URL resource = classLoader.getResource(" this ::: is utter !?@£$%^ nonsense");
			assertThat(resource, is(nullValue()));
		}
	}


	@Test
	public void unknownResourceAsStreamReturnsNull()
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jar(createJar("SomeKey", "SomeValue"))) {

			InputStream stream = classLoader.getResourceAsStream(" this ::: is utter !?@£$%^ nonsense");
			assertThat(stream, is(nullValue()));
		}
	}


	@Test
	public void orphanedInMemoryClassLoaderCannotLoadSystemClass() throws Exception
	{
		try {
			ClassLoaders.orphanedInMemoryClassLoader().loadClass(String.class.getCanonicalName());
			fail();
		} catch(ClassNotFoundException cnfe) {
			assertThat(cnfe.getMessage(), containsString(String.class.getCanonicalName()));
		}
	}


	@Test
	public void loadFailsOnNull()
	{
		try {
			ClassLoaders.load(null);
			fail();
		} catch(NullPointerException npe) {}
	}


	@Test
	public void canDefineAClass() throws IOException
	{
		String canonicalName = InMemoryClassLoaderTest.class.getCanonicalName();
		String resourceName = canonicalName.replace('.', '/') + ".class";
		InputStream resource = InMemoryClassLoaderTest.class.getClassLoader().getResourceAsStream(resourceName);

		byte[] bytes = InMemoryClassLoader.inputStreamToBytes(resource);
		Class<?> loaded = ClassLoaders.load(bytes);

		assertThat(loaded.getCanonicalName(), is(equalTo(canonicalName)));
		assertThat(loaded, is(not(equalTo(InMemoryClassLoaderTest.class))));
	}


	@Test
	public void asSystemClassLoaderCanLoadSystemClass()
	{
		asSystemClassLoaderCanLoad(String.class.getCanonicalName());
	}


	private void asSystemClassLoaderCanLoad(String canonicalName)
	{
		String property = System.getProperty("java.system.class.loader");
		assumeTrue(InMemoryClassLoader.class.getCanonicalName().equals(property));

		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		assertThat(systemClassLoader, is(instanceOf(InMemoryClassLoader.class)));

		Class<?> loaded = Exceptional.apply(systemClassLoader::loadClass, canonicalName);

		assertThat(loaded.getCanonicalName(), is(equalTo(canonicalName)));
	}


	@Test
	public void asSystemClassLoaderCanLoadApplicationClass()
	{
		asSystemClassLoaderCanLoad(InMemoryClassLoaderTest.class.getCanonicalName());
	}
}
