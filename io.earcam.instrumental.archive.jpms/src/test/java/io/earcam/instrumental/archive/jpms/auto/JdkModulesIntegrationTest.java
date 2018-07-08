/*-
 * #%L
 * io.earcam.instrumental.archive.jpms
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
package io.earcam.instrumental.archive.jpms.auto;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.io.FileMatchers.anExistingFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.jpms.PackageModuleMapper;
import io.earcam.instrumental.archive.jpms.auto.JdkModules;
import io.earcam.instrumental.module.jpms.ModuleInfo;

/**
 * Cache must be built - either by running main method of {@link JdkModules} directly
 * running this test, or via the maven build (which ensures cache populated regardless of
 * running the integration-test phase).
 */
public class JdkModulesIntegrationTest {

	@BeforeAll
	public static void cacheIsPresent() throws IOException, InterruptedException
	{
		synchronized(JdkModules.class) {

			Path cache = JdkModules.DEFAULT_DIRECTORY.resolve(JdkModules.CACHE_FILENAME);
			Files.deleteIfExists(cache);

			JdkModules.main(new String[0]);
			assertThat(cache.toFile(), is(anExistingFile()));
		}
	}


	@Test
	public void mapperIsAvailableViaSpi()
	{
		assertThat(mapperSpi().next(), instanceOf(JdkModules.class));
	}


	private Iterator<PackageModuleMapper> mapperSpi()
	{
		return spi(PackageModuleMapper.class);
	}


	private <T> Iterator<T> spi(Class<T> contract)
	{
		Iterator<T> iterator = ServiceLoader.load(contract).iterator();

		assertThat(iterator.hasNext(), is(true));
		return iterator;
	}


	@Test
	public void unqualifiedRequiredForModule() throws Exception
	{
		PackageModuleMapper mapper = spi(PackageModuleMapper.class).next();
		String nonJdkModule = "com.acme.not.in.jdk";
		Set<String> packages = new HashSet<>();
		packages.add("com.sun.nio.sctp");
		packages.add(nonJdkModule);

		Set<String> modules = mapper.moduleRequiredFor("com.acme.irrelevant", packages.iterator())
				.stream()
				.map(ModuleInfo::name)
				.collect(toSet());

		assertThat(modules, contains("jdk.sctp"));
		assertThat(packages, contains(nonJdkModule));
	}


	@Test
	public void qualifiedRequiredForQualifiedModule() throws Exception
	{
		PackageModuleMapper mapper = spi(PackageModuleMapper.class).next();
		String nonJdkModule = "com.acme.not.in.jdk";
		Set<String> packages = new HashSet<>();
		packages.add("jdk.internal.org.objectweb.asm.signature");
		packages.add(nonJdkModule);

		Set<String> modules = mapper.moduleRequiredFor("jdk.scripting.nashorn", packages.iterator())
				.stream()
				.map(ModuleInfo::name)
				.collect(toSet());

		assertThat(modules, contains("java.base"));
		assertThat(packages, contains(nonJdkModule));
	}


	@Test
	public void qualifiedRequiredForUnqualifiedModule() throws Exception
	{
		PackageModuleMapper mapper = spi(PackageModuleMapper.class).next();
		String paquet = "jdk.internal.org.objectweb.asm.signature";
		Set<String> packages = new HashSet<>();
		packages.add(paquet);

		Set<ModuleInfo> modules = mapper.moduleRequiredFor("com.acme.aint.gonna.werk", packages.iterator());

		assertThat(modules, is(empty()));
		assertThat(packages, contains(paquet));
	}


	@Test
	public void unqualifiedOpenForModule() throws Exception
	{
		PackageModuleMapper mapper = spi(PackageModuleMapper.class).next();
		String nonJdkModule = "com.acme.not.in.jdk";
		Set<String> packages = new HashSet<>();
		packages.add(nonJdkModule);
		packages.add("sun.misc");

		Set<String> modules = mapper.moduleOpenedFor("com.acme.irrelevant", packages.iterator())
				.stream()
				.map(ModuleInfo::name)
				.collect(toSet());

		assertThat(modules, contains("jdk.unsupported"));
		assertThat(packages, contains(nonJdkModule));
	}


	@Test
	public void qualifiedOpensForModule() throws Exception
	{
		PackageModuleMapper mapper = spi(PackageModuleMapper.class).next();
		String nonJdkModule = "com.acme.not.in.jdk";
		Set<String> packages = new HashSet<>();
		packages.add(nonJdkModule);
		packages.add("com.sun.java.swing.plaf.windows");
		packages.add("javax.swing.plaf.basic");

		Set<String> modules = mapper.moduleOpenedFor("jdk.jconsole", packages.iterator())
				.stream()
				.map(ModuleInfo::name)
				.collect(toSet());

		assertThat(modules, contains("java.desktop"));
		assertThat(packages, contains(nonJdkModule));
	}


	@Test
	public void qualifiedOpensForUnqalifiedModule() throws Exception
	{
		PackageModuleMapper mapper = spi(PackageModuleMapper.class).next();
		String nonJdkModule = "com.acme.not.in.jdk";
		Set<String> packages = new HashSet<>();
		packages.add(nonJdkModule);
		packages.add("com.sun.java.swing.plaf.windows");
		packages.add("javax.swing.plaf.basic");

		Set<String> modules = mapper.moduleOpenedFor("com.acme.unqalified", packages.iterator())
				.stream()
				.map(ModuleInfo::name)
				.collect(toSet());

		assertThat(modules, is(empty()));
		assertThat(packages, hasSize(3));
	}
}
