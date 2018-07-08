/*-
 * #%L
 * io.earcam.instrumental.archive.osgi
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
package io.earcam.instrumental.archive.osgi.auto;

import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.AsJar.asJar;
import static io.earcam.instrumental.archive.osgi.AsOsgiBundle.asOsgiBundle;
import static io.earcam.instrumental.module.auto.Classpaths.PROPERTY_CLASS_PATH;
import static java.io.File.pathSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.module.osgi.BundleInfo;
import io.earcam.instrumental.module.osgi.Clause;

public class ClasspathBundlesTest {

	@Test
	void findsBundlesOnClasspath()
	{
		Path baseDir = Paths.get(".", "target", getClass().getCanonicalName(), "findsBundlesOnClasspath", UUID.randomUUID().toString());
		Path otherDir = baseDir.resolve("other");
		otherDir.toFile().mkdirs();

		Path aJar = writeBundle(baseDir, "bundle.a");
		Path bJar = writeJar(baseDir, "vanilla-b");
		Path cJar = writeExplodedBundle(baseDir, "bundle.c");
		Path dJar = writeZip(baseDir, "de_nadda");

		String oldClasspath = System.getProperty(PROPERTY_CLASS_PATH);
		try {
			System.setProperty(PROPERTY_CLASS_PATH, constructPath(aJar, bJar, cJar, dJar));

			ClasspathBundles mapper = new ClasspathBundles();

			List<String> classpathModuleNames = mapper.bundles().stream()
					.map(BundleInfo::symbolicName)
					.map(Clause::uniqueNames)
					.flatMap(Set::stream)
					.collect(toList());

			assertThat(classpathModuleNames, containsInAnyOrder("bundle.a", "bundle.c"));
		} finally {
			System.setProperty(PROPERTY_CLASS_PATH, oldClasspath);
		}
	}


	private Path writeBundle(Path baseDir, String symbolicName)
	{

		return archive()
				.configured(asOsgiBundle()
						.named(symbolicName))
				.to(baseDir.resolve(symbolicName + ".jar"));
	}


	private Path writeJar(Path baseDir, String filename)
	{

		return archive()
				.configured(asJar()).to(baseDir.resolve(filename + ".jar"));
	}


	private Path writeExplodedBundle(Path baseDir, String symbolicName)
	{

		return archive()
				.configured(asOsgiBundle()
						.named(symbolicName))
				.explodeTo(baseDir.resolve(symbolicName + "_exploded"));
	}


	private Path writeZip(Path baseDir, String name)
	{

		return archive()
				.with("some.txt", "blah blah".getBytes(UTF_8))
				.explodeTo(baseDir.resolve(name + ".zip"));
	}


	private static String constructPath(Path... jars)
	{
		return Arrays.stream(jars)
				.map(Path::toAbsolutePath)
				.map(Path::toString)
				.collect(joining(pathSeparator));
	}
}
