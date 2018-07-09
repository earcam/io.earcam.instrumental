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
package io.earcam.instrumental.archive.osgi;

import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.osgi.AsOsgiBundle.asOsgiBundle;
import static io.earcam.instrumental.module.osgi.BundleInfoBuilder.bundle;
import static io.earcam.instrumental.module.osgi.BundleManifestHeaders.IMPORT_PACKAGE;
import static io.earcam.instrumental.module.osgi.Clause.clause;
import static io.earcam.instrumental.module.osgi.ClauseParameters.EMPTY_PARAMETERS;
import static io.earcam.instrumental.module.osgi.ClauseParameters.ClauseParameter.attribute;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.earcam.acme.DummyInterface;
import io.earcam.acme.TakesExt;
import io.earcam.acme.ext.Ext;
import io.earcam.acme.internal.DummyActivator;
import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.archive.osgi.auto.AbstractPackageBundleMapper;
import io.earcam.instrumental.module.osgi.BundleInfo;
import io.earcam.instrumental.module.osgi.BundleInfoBuilder;
import io.earcam.instrumental.module.osgi.ClauseParameters;
import io.earcam.instrumental.module.osgi.ClauseParameters.ClauseParameter;
import io.earcam.instrumental.reflect.Names;
import io.earcam.unexceptional.Exceptional;

public class AsOsgiBundleTest {

	@Test
	void emptyBundle()
	{
		Archive archive = archive()
				.configured(asOsgiBundle()
						.named("foo"))
				.toObjectModel();

		BundleInfo bundleInfo = bundleInfoFrom(archive);

		assertThat(bundleInfo.symbolicName(), is(equalTo(clause("foo"))));
	}


	private static BundleInfo bundleInfoFrom(Archive archive)
	{
		Manifest manifest = archive.manifest().orElseThrow(AssertionError::new);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Exceptional.accept(manifest::write, baos);
		BundleInfoBuilder builder = BundleInfoBuilder.bundleFrom(new ByteArrayInputStream(baos.toByteArray()));
		return builder.construct();
	}


	@Test
	void parameterizedSymbolicName()
	{
		ClauseParameters parameters = attribute("attri", "bute").directive("direct", "ive");

		Archive archive = archive()
				.configured(asOsgiBundle()
						.named("sym.nom", parameters))
				.toObjectModel();

		BundleInfo bundleInfo = bundleInfoFrom(archive);

		assertThat(bundleInfo.symbolicName(), is(equalTo(clause("sym.nom", parameters))));
	}


	@Test
	void singleExportedClassWithNoParameters()
	{
		Archive archive = archive()
				.configured(asOsgiBundle()
						.named("mandatory.value")
						.exporting(DummyInterface.class))
				.toObjectModel();

		BundleInfo bundleInfo = bundleInfoFrom(archive);

		assertThat(bundleInfo.exportPackage(), contains(clause(pkg(DummyInterface.class))));
		assertThat(archive.content(Names.typeToResourceName(DummyInterface.class)), is(not(nullValue())));
	}


	private String pkg(Class<?> type)
	{
		return type.getPackage().getName();
	}


	@Test
	void exportPatternWithParameters()
	{
		Predicate<String> matcher = Predicate.isEqual(pkg(DummyInterface.class));
		ClauseParameters parameters = ClauseParameter.attribute("attr", "ibute");

		Archive archive = archive()
				.configured(asOsgiBundle()
						.named("mandatory.value")
						.exporting(matcher, parameters))
				.with(DummyInterface.class)
				.with(DummyActivator.class)
				.toObjectModel();

		BundleInfo bundleInfo = bundleInfoFrom(archive);

		assertThat(bundleInfo.exportPackage(), contains(clause(pkg(DummyInterface.class), parameters)));
	}


	@Test
	void validActivator()
	{
		Archive archive = archive()
				.configured(asOsgiBundle()
						.named("mandatory.value")
						.withActivator(DummyActivator.class))
				.toObjectModel();

		BundleInfo bundleInfo = bundleInfoFrom(archive);

		assertThat(bundleInfo.activator(), is(equalTo(cn(DummyActivator.class))));
	}


	private String cn(Class<?> type)
	{
		return type.getCanonicalName();
	}


	@Test
	void invalidActivator()
	{
		try {
			asOsgiBundle()
					.named("mandatory.value")
					.withActivator(DummyInterface.class);
			fail();
		} catch(IllegalArgumentException e) {

		}
	}


	@Test
	void importWithParameters()
	{
		String paquet = "com.acme.core.of.cores.root.of.all.apis";
		Archive archive = archive()
				.configured(asOsgiBundle()
						.named("mandatory.value")
						.importing(paquet))
				.with(DummyInterface.class)
				.toObjectModel();

		BundleInfo bundleInfo = bundleInfoFrom(archive);

		assertThat(bundleInfo.importPackage(), contains(clause(paquet)));
	}


	@Test
	void exportPatternDoesNotExportResourcePaths() throws ReflectiveOperationException
	{

		Archive archive = archive()
				.configured(asOsgiBundle()
						.named("syn.nom")
						.exporting(s -> true, EMPTY_PARAMETERS))
				.with(DummyInterface.class)
				.with("some/resource/path/config.properties", bytes("key1=valueA\nkey2=valueB"))
				.with("/some/other/path/legal.md", bytes("IANAL Sorry"))
				.toObjectModel();

		BundleInfo bundleInfo = bundleInfoFrom(archive);

		assertThat(bundleInfo.exportPackage(), contains(clause(pkg(DummyInterface.class))));
	}


	private static byte[] bytes(String text)
	{
		return text.getBytes(UTF_8);
	}


	@Test
	void exportPatternDoesNotExportTheDefaultPackage() throws ReflectiveOperationException
	{

		Class<?> noPackage = getClass().getClassLoader().loadClass("NoPackage");
		assertThat(noPackage, is(not(nullValue())));

		Archive archive = archive()
				.configured(asOsgiBundle()
						.named("syn.nom")
						.exporting(s -> true, EMPTY_PARAMETERS))
				.with(DummyInterface.class)
				.with(noPackage)
				.toObjectModel();

		BundleInfo bundleInfo = bundleInfoFrom(archive);

		assertThat(bundleInfo.exportPackage(), contains(clause(pkg(DummyInterface.class))));
	}

	@Nested
	class AutoImport {

		@Disabled // FIXME
		@Test
		void autoImportsViaDefaultClasspathBundleMapper()
		{
			Archive built = archive()
					.configured(asOsgiBundle()
							.named("dependee")
							.autoImporting())
					.with(Assertions.class)
					.toObjectModel();

			String value = built.manifest().get().getMainAttributes().getValue(IMPORT_PACKAGE.header());

			assertThat(value, containsString("apiguardian"));
		}


		@Test
		void autoImportsViaSuppliedPackageBundleMapper()
		{
			ClauseParameters version = attribute("version", "0.0.1-alpha");
			String exported = pkg(Ext.class);

			BundleInfo dependency = bundle()
					.symbolicName("dependency")
					.exportPackages(exported, version)
					.construct();

			PackageBundleMapper mapper = new AbstractPackageBundleMapper() {
				@Override
				protected List<BundleInfo> bundles()
				{
					return Collections.singletonList(dependency);
				}
			};

			Archive built = archive()
					.configured(asOsgiBundle()
							.named("dependee")
							.autoImporting(mapper))
					.with(TakesExt.class)
					.toObjectModel();

			String value = built.manifest().get().getMainAttributes().getValue(IMPORT_PACKAGE.header());

			assertThat(value, is(equalTo(clause(exported, version).toString())));
		}


		@Test
		void autoImportsFailsWhenImportsUnmapped()
		{

			PackageBundleMapper mapper = new AbstractPackageBundleMapper() {
				@Override
				protected List<BundleInfo> bundles()
				{
					return Collections.emptyList();
				}
			};

			try {
				archive()
						.configured(asOsgiBundle()
								.named("dependee")
								.autoImporting(mapper))
						.with(TakesExt.class)
						.toObjectModel();
				fail();
			} catch(IllegalStateException e) {

			}
		}

	}
}