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

import static java.lang.Thread.currentThread;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.reflect.Resources;

public class ClassLoadersTest {

	// EARCAM_SNIPPET_BEGIN: selffirst
	@Test
	public void selfFirstClassLoader() throws IOException, ClassNotFoundException
	{
		Path junitApiJar = Paths.get(Resources.sourceOfResource(Test.class));

		try(URLClassLoader selfFirst = ClassLoaders.selfFirstClassLoader(junitApiJar)) {

			Class<?> loaded = selfFirst.loadClass(Test.class.getCanonicalName());
			assertThat(loaded, is(not(equalTo(Test.class))));
		}
	}
	// EARCAM_SNIPPET_END: selffirst


	@Test
	public void selfFirstClassLoaderTwiceLoaded() throws IOException, ClassNotFoundException
	{
		Path junitApiJar = Paths.get(Resources.sourceOfResource(Test.class));

		try(URLClassLoader selfFirst = ClassLoaders.selfFirstClassLoader(junitApiJar)) {

			Class<?> a = selfFirst.loadClass(Test.class.getCanonicalName());
			Class<?> b = selfFirst.loadClass(Test.class.getCanonicalName());

			assertThat(a, is(equalTo(b)));
		}
	}


	@Test
	public void selfFirstClassLoaderNotFoundInSelf() throws IOException, ClassNotFoundException
	{
		Path junitApiJar = Paths.get(Resources.sourceOfResource(Test.class));

		try(URLClassLoader selfFirst = ClassLoaders.selfFirstClassLoader(junitApiJar)) {

			Class<?> loaded = selfFirst.loadClass(String.class.getCanonicalName());

			assertThat(loaded, is(equalTo(String.class)));
		}
	}


	@Test
	public void runInContext()
	{
		InMemoryClassLoader a = ClassLoaders.inMemoryClassLoader();
		InMemoryClassLoader b = ClassLoaders.inMemoryClassLoader();

		currentThread().setContextClassLoader(a);

		ClassLoaders.runInContext(b, () -> assertThat(currentThread().getContextClassLoader(), is(sameInstance(b))));

		assertThat(currentThread().getContextClassLoader(), is(sameInstance(a)));
	}


	@Test
	public void canDefineAClass()
	{
		byte[] bytes = Resources.classAsBytes(ClassLoadersTest.class);

		Class<?> loaded = ClassLoaders.load(bytes);

		assertThat(loaded.getCanonicalName(), is(equalTo(ClassLoadersTest.class.getCanonicalName())));
		assertThat(loaded, is(not(equalTo(ClassLoadersTest.class))));
	}


	@Test
	public void canDefineAClassFromSubsectionOfArray()
	{
		byte[] section = Resources.classAsBytes(ClassLoadersTest.class);

		byte[] bytes = new byte[10 + section.length + 20];

		System.arraycopy(section, 0, bytes, 10, section.length);

		Class<?> loaded = ClassLoaders.load(ClassLoadersTest.class.getCanonicalName(), bytes, 10, section.length);

		assertThat(loaded.getCanonicalName(), is(equalTo(ClassLoadersTest.class.getCanonicalName())));
		assertThat(loaded, is(not(equalTo(ClassLoadersTest.class))));
	}
}
