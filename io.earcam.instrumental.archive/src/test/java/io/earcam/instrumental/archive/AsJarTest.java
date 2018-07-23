/*-
 * #%L
 * io.earcam.instrumental.archive
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
package io.earcam.instrumental.archive;

import static io.earcam.instrumental.archive.AsJar.asJar;
import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.Hamcrest.present;
import static io.earcam.instrumental.reflect.Names.typeToResourceName;
import static java.lang.Boolean.TRUE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.jar.Attributes.Name.MAIN_CLASS;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import io.earcam.acme.CaseInsensitiveToStringComparator;
import io.earcam.acme.DummyMain;
import io.earcam.acme.WhitespaceIgnorantToStringComparator;
import io.earcam.acme.other.Other;
import io.earcam.instrumental.lade.ClassLoaders;
import io.earcam.instrumental.lade.InMemoryClassLoader;

public class AsJarTest {

	@Test
	void emptyArchiveHasManifest()
	{
		Archive archive = archive()
				.configured(asJar())
				.toObjectModel();

		assertThat(archive.contents(), is(empty()));

		assertValidJarManifest(archive);
	}


	private void assertValidJarManifest(Archive archive)
	{
		assertThat(archive.manifest(), is(present()));

		String manifestVersion = archive.manifest().get().getMainAttributes().getValue(Name.MANIFEST_VERSION);
		assertThat(manifestVersion, is(equalTo(AbstractAsJarBuilder.V1_0)));
	}


	@Test
	void singleContent()
	{
		String name = "/path/to/some.file";
		byte[] contents = "Some Content".getBytes(UTF_8);

		Archive archive = archive()
				.configured(asJar())
				.with(name, contents)
				.toObjectModel();

		assertThat(archive.contents(), hasSize(1));
		assertThat(archive.contents(), contains(new ArchiveResource(name, contents)));

		assertValidJarManifest(archive);
	}


	@Test
	void mainClass()
	{
		// @formatter:off
		// EARCAM_SNIPPET_BEGIN: jar-main
		Archive archive = archive()
			.configured(asJar()
				.launching(DummyMain.class))
			.toObjectModel();
		// EARCAM_SNIPPET_END: jar-main
		// @formatter:on

		assertValidJarManifest(archive);

		Attributes attributes = archive.manifest().get().getMainAttributes();
		assertThat(attributes, hasEntry(MAIN_CLASS, cn(DummyMain.class)));

		assertThat(archive.contents(), contains(new ArchiveResource(typeToResourceName(DummyMain.class), new byte[0])));
	}


	@Test
	void manifestMainAttributesAreMerged()
	{

		Manifest manifest = new Manifest();
		manifest.getMainAttributes().putValue("Existing-Header", "Existing Value");

		Archive archive = archive()
				.configured(asJar()
						.mergingManifest(manifest)
						.launching(DummyMain.class))
				.toObjectModel();

		assertValidJarManifest(archive);

		Attributes attributes = archive.manifest().get().getMainAttributes();
		assertThat(attributes, allOf(
				hasEntry(MAIN_CLASS, cn(DummyMain.class)),
				hasEntry(new Name("Existing-Header"), "Existing Value")));
	}


	@Test
	void manifestNamedEntriesAreMerged()
	{

		Manifest manifestA = new Manifest();
		Attributes entryA = new Attributes();
		entryA.putValue("Merged", "By A");
		manifestA.getEntries().put("named", entryA);

		Manifest manifestB = new Manifest();
		Attributes entryB = new Attributes();
		entryB.putValue("Merged-From", "B");
		manifestB.getEntries().put("named", entryB);

		Archive archive = archive()
				.configured(asJar()
						.mergingManifest(manifestA)
						.mergingManifest(manifestB)
						.withManifestHeader("Main", "Attribute"))
				.toObjectModel();

		assertValidJarManifest(archive);

		Attributes attributes = archive.manifest().get().getAttributes("named");

		Attributes expected = new Attributes();
		expected.putValue("Merged", "By A");
		expected.putValue("Merged-From", "B");
		assertThat(attributes, is(equalTo(expected)));

		assertThat(archive.manifest().get().getMainAttributes(), hasEntry(new Name("Main"), "Attribute"));
	}


	@Test
	void mainClassFailsFastIfMainMethodNotFound()
	{
		try {
			asJar().launching(AsJarTest.class);
			fail();
		} catch(Exception e) {}
	}


	@Test
	void singleSpiSingleImplementation()
	{
		byte[] archive = archive()
				.configured(asJar()
						.providing(Comparator.class, CaseInsensitiveToStringComparator.class))
				.toByteArray();

		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jar(archive)) {

			List<String> implementations = spiServices(Comparator.class, classLoader);

			assertThat(implementations, contains(cn(CaseInsensitiveToStringComparator.class)));
		}
	}


	private static String cn(Class<?> type)
	{
		return type.getCanonicalName();
	}


	@Test
	void singleSpiMultipleImplementations()
	{
		// @formatter: off
		// EARCAM_SNIPPET_BEGIN: jar-spi-classload
		byte[] archive = archive().configured(asJar()
				.providing(Comparator.class,
						CaseInsensitiveToStringComparator.class,
						WhitespaceIgnorantToStringComparator.class))
				.toByteArray();

		ClassLoader loader = ClassLoaders.inMemoryClassLoader().jar(archive);

		List<String> imps = spiServices(Comparator.class, loader);

		assertThat(imps, containsInAnyOrder(
				cn(CaseInsensitiveToStringComparator.class),
				cn(WhitespaceIgnorantToStringComparator.class)));
		// EARCAM_SNIPPET_END: jar-spi-classload
		// @formatter: on
	}


	private List<String> spiServices(Class<?> service, ClassLoader classLoader)
	{
		return streamSpiServices(service, classLoader)
				.map(i -> cn(i.getClass()))
				.collect(toList());
	}


	private <T> Stream<T> streamSpiServices(Class<T> service, ClassLoader classLoader)
	{
		return StreamSupport.stream(ServiceLoader.load(service, classLoader).spliterator(), false);
	}


	@Test
	void sealing()
	{
		Predicate<String> packageMatcher = Predicate.isEqual(pkg(WhitespaceIgnorantToStringComparator.class));

		Archive jar = archive()
				.configured(asJar()
						.sealing(packageMatcher))
				.with(WhitespaceIgnorantToStringComparator.class, Other.class)
				.toObjectModel();

		Manifest manifest = jar.manifest().get();

		assertThat(manifest.getAttributes(pkg(Other.class)), is(nullValue()));
		assertThat(manifest.getAttributes(pkg(WhitespaceIgnorantToStringComparator.class)), hasEntry(Name.SEALED, TRUE.toString()));
	}


	private String pkg(Class<?> type)
	{
		return type.getPackage().getName();
	}
}
