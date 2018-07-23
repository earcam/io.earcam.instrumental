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

import static io.earcam.instrumental.archive.jpms.auto.ClasspathModules.HEADER_AUTOMATIC_MODULE_NAME;
import static io.earcam.instrumental.module.auto.Classpaths.PROPERTY_CLASS_PATH;
import static io.earcam.instrumental.module.jpms.ModuleInfo.moduleInfo;
import static io.earcam.instrumental.module.jpms.ModuleModifier.SYNTHETIC;
import static java.io.File.pathSeparator;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.utilitarian.io.IoStreams;

public class ClasspathModulesTest {

	// Automatic-Module-Name requires generating module-info.class
	@Test
	void generatesSyntheticForAutomaticModule() throws IOException
	{
		Path baseDir = Paths.get(".", "target", getClass().getCanonicalName(), "generatesSyntheticForAutomaticModule", UUID.randomUUID().toString());
		baseDir.toFile().mkdirs();
		Path eJar = writeAutoModule(baseDir, "mod.e", "e.jar");

		ModuleInfo expectedSynthetic = moduleInfo()
				.withAccess(SYNTHETIC.access())
				.named("mod.e")
				.exporting(ClasspathModulesTest.class.getPackage().getName())
				.construct();

		ModuleInfo extractedSynthetic = new ClasspathModules().moduleInfoFrom(eJar);

		assertThat(extractedSynthetic, is(equalTo(expectedSynthetic)));

	}


	@Test
	void findsModulesOnClasspath() throws IOException
	{
		Path baseDir = Paths.get(".", "target", getClass().getCanonicalName(), "findsModulesOnClasspath", UUID.randomUUID().toString());
		Path otherDir = baseDir.resolve("other");
		otherDir.toFile().mkdirs();

		Path aJar = writeModule(baseDir, "mod.a", "a.jar");
		Path bJar = writeNonModule(baseDir, "b.jar");
		Path cJar = writeModule(otherDir, "mod.c", "c.jar");
		Path dJar = writeExplodedModule(baseDir, "mod.d");
		Path eJar = writeAutoModule(baseDir, "mod.e", "e.jar");

		String oldClasspath = System.getProperty(PROPERTY_CLASS_PATH);
		try {
			System.setProperty(PROPERTY_CLASS_PATH, constructPath(aJar, bJar, cJar, dJar, eJar));

			ClasspathModules mapper = new ClasspathModules();

			List<String> classpathModuleNames = mapper.modules().stream()
					.map(ModuleInfo::name)
					.collect(toList());

			assertThat(classpathModuleNames, containsInAnyOrder("mod.a", "mod.c", "mod.d", "mod.e"));
		} finally {
			System.setProperty(PROPERTY_CLASS_PATH, oldClasspath);
		}
	}


	private static String constructPath(Path... jars)
	{
		return Arrays.stream(jars)
				.map(Path::toAbsolutePath)
				.map(Path::toString)
				.collect(joining(pathSeparator));
	}


	private static Path writeModule(Path baseDir, String moduleName, String jarName) throws FileNotFoundException
	{
		ModuleInfo module = moduleInfo().named(moduleName).construct();
		InputStream built = bundle()
				.add("module-info.class", new ByteArrayInputStream(module.toBytecode()))
				.build();

		return writeJar(baseDir, jarName, built);
	}


	private static Path writeJar(Path baseDir, String jarName, InputStream built) throws FileNotFoundException
	{
		Path jar = baseDir.resolve(jarName);
		IoStreams.transfer(built, new FileOutputStream(jar.toFile()));
		return jar;
	}


	private static Path writeNonModule(Path baseDir, String jarName) throws FileNotFoundException
	{
		InputStream built = bundle()
				.add(ClasspathModulesTest.class)
				.build();

		return writeJar(baseDir, jarName, built);
	}


	private static Path writeExplodedModule(Path baseDir, String moduleName) throws IOException
	{

		Path path = baseDir.resolve("exploded-" + moduleName);
		path.toFile().mkdirs();

		ModuleInfo module = moduleInfo().named(moduleName).construct();
		Files.write(path.resolve("module-info.class"), module.toBytecode());

		return path;
	}


	private static Path writeAutoModule(Path baseDir, String moduleName, String jarName) throws FileNotFoundException
	{
		InputStream built = bundle()
				.set(HEADER_AUTOMATIC_MODULE_NAME.toString(), moduleName)
				.add(ClasspathModulesTest.class)
				.build();

		return writeJar(baseDir, jarName, built);
	}
}
