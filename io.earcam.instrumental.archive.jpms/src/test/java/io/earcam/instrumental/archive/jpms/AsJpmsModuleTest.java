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
package io.earcam.instrumental.archive.jpms;

import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.jpms.AsJpmsModule.asJpmsModule;
import static io.earcam.instrumental.module.jpms.ExportModifier.MANDATED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Comparator;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.acme.DummyIntComparator;
import com.acme.DummyMain;
import com.acme.ext.Ext;
import com.acme.internal.DummyInternal;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.module.jpms.Export;
import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.instrumental.module.jpms.Require;
import io.earcam.instrumental.module.jpms.RequireModifier;

public class AsJpmsModuleTest {

	@Test
	void emptyModule()
	{
		Archive archive = archive()
				.configured(asJpmsModule()
						.named("foo")
						.versioned("0.42.1"))
				.toObjectModel();

		ModuleInfo moduleInfo = moduleInfoFrom(archive);

		assertThat(moduleInfo.name(), is("foo"));
		assertThat(moduleInfo.version(), is("0.42.1"));

		assertThat(moduleInfo.mainClass(), is(nullValue()));
		assertThat(moduleInfo.packages(), is(empty()));
		assertThat(moduleInfo.exports(), is(empty()));
		assertThat(moduleInfo.opens(), is(empty()));
		assertThat(moduleInfo.uses(), is(empty()));
		assertThat(moduleInfo.requires(), is(empty()));
		assertThat(moduleInfo.provides(), is(anEmptyMap()));
	}


	@Test
	void mainClass()
	{
		Archive archive = archive()
				.configured(asJpmsModule()
						.named("foo")
						.launching(DummyMain.class))
				.toObjectModel();

		ModuleInfo moduleInfo = moduleInfoFrom(archive);

		assertThat(moduleInfo.mainClass(), is(equalTo(DummyMain.class.getCanonicalName())));
	}


	private ModuleInfo moduleInfoFrom(Archive archive)
	{
		ModuleInfo moduleInfo = archive
				.content("module-info.class")
				.map(ArchiveResource::bytes)
				.map(ModuleInfo::read)
				.orElseThrow(AssertionError::new);
		return moduleInfo;
	}


	@Test
	void provides()
	{
		Archive archive = archive()
				.configured(asJpmsModule()
						.named("foo")
						.providing(Comparator.class, DummyIntComparator.class))
				.toObjectModel();

		ModuleInfo moduleInfo = moduleInfoFrom(archive);

		assertThat(moduleInfo.provides(), hasEntry(
				equalTo(cn(Comparator.class)),
				arrayContaining(cn(DummyIntComparator.class))));
	}


	private static String cn(Class<?> type)
	{
		return type.getCanonicalName();
	}


	@Test
	void export()
	{
		// EARCAM_SNIPPET_BEGIN: export-predicate
		Predicate<String> predicate = e -> !e.contains("internal");

		Archive archive = archive()
				.configured(asJpmsModule()
						.named("foo")
						.exporting(predicate))
				.with(DummyIntComparator.class)
				.with(DummyInternal.class)
				.toObjectModel();
		// EARCAM_SNIPPET_END: export-predicate

		ModuleInfo moduleInfo = moduleInfoFrom(archive);

		Export expectedExport = new Export(pkg(DummyIntComparator.class), MANDATED.access());

		assertThat(moduleInfo.exports(), contains(expectedExport));
		assertThat(moduleInfo.opens(), is(empty()));
	}


	private static String pkg(Class<?> type)
	{
		return type.getPackage().getName();
	}


	@Test
	void exportsTo()
	{
		String packageA = pkg(DummyIntComparator.class);
		String packageB = pkg(Ext.class);
		Predicate<String> predicateA = Predicate.isEqual(packageA);
		Predicate<String> predicateB = Predicate.isEqual(packageB);

		Archive archive = archive()
				.configured(asJpmsModule()
						.named("foo")
						.exporting(predicateA, "a")
						.exporting(predicateB, "b"))
				.with(DummyIntComparator.class)
				.with(DummyInternal.class)
				.with(Ext.class)
				.toObjectModel();

		ModuleInfo moduleInfo = moduleInfoFrom(archive);

		Export expectedExportA = new Export(packageA, MANDATED.access(), "a");
		Export expectedExportB = new Export(packageB, MANDATED.access(), "b");

		assertThat(moduleInfo.exports(), contains(expectedExportA, expectedExportB));
		assertThat(moduleInfo.opens(), is(empty()));
	}


	@Test
	void opens()
	{
		String packageA = pkg(DummyIntComparator.class);
		String packageB = pkg(Ext.class);
		Predicate<String> predicateA = Predicate.isEqual(packageA);
		Predicate<String> predicateB = Predicate.isEqual(packageB);

		Archive archive = archive()
				.configured(asJpmsModule()
						.named("foo")
						.opening(predicateA, "a")
						.opening(predicateB))
				.with(DummyIntComparator.class)
				.with(DummyInternal.class)
				.with(Ext.class)
				.toObjectModel();

		ModuleInfo moduleInfo = moduleInfoFrom(archive);

		Export expectedExportA = new Export(packageA, MANDATED.access(), "a");
		Export expectedExportB = new Export(packageB, MANDATED.access());

		assertThat(moduleInfo.opens(), contains(expectedExportA, expectedExportB));
		assertThat(moduleInfo.exports(), is(empty()));
	}


	@Test
	void requires()
	{
		Archive archive = archive()
				.configured(asJpmsModule()
						.named("foo")
						.requiring("bar")
						.requiring("hum.bug", "1.42.0"))
				.toObjectModel();

		ModuleInfo moduleInfo = moduleInfoFrom(archive);

		Require expectedRequire1 = new Require("bar", RequireModifier.MANDATED.access(), null);
		Require expectedRequire2 = new Require("hum.bug", RequireModifier.MANDATED.access(), "1.42.0");

		assertThat(moduleInfo.requires(), containsInAnyOrder(expectedRequire1, expectedRequire2));
	}


	@Test
	void uses()
	{
		Archive archive = archive()
				.configured(asJpmsModule()
						.named("foo")
						.using(Comparator.class)
						.using(Predicate.class))
				.toObjectModel();

		ModuleInfo moduleInfo = moduleInfoFrom(archive);

		assertThat(moduleInfo.uses(), containsInAnyOrder(cn(Comparator.class), cn(Predicate.class)));
	}


	@Test
	void packages()
	{
		Archive archive = archive()
				.configured(asJpmsModule()
						.named("foo")
						.listingPackages())
				.with(DummyIntComparator.class)
				.with(Ext.class)
				.toObjectModel();

		ModuleInfo moduleInfo = moduleInfoFrom(archive);

		assertThat(moduleInfo.packages(), containsInAnyOrder(pkg(DummyIntComparator.class), pkg(Ext.class)));
	}


	@Test
	void providingFromMetaInfServices()
	{
		Archive archive = archive()
				.configured(asJpmsModule()
						.named("foo")
						.providingFromMetaInfServices())
				.with(DummyIntComparator.class)
				.with("META-INF/services/java.util.Comparator", cn(DummyIntComparator.class).getBytes(UTF_8))
				.with("META-INF/servicehistory", "one careful owner".getBytes(UTF_8))
				.toObjectModel();

		ModuleInfo moduleInfo = moduleInfoFrom(archive);

		assertThat(moduleInfo.provides(), allOf(
				aMapWithSize(1),
				hasEntry(equalTo("java.util.Comparator"), arrayContaining(cn(DummyIntComparator.class)))));
	}


	@Test
	void notProvidingFromMetaInfServices()
	{
		Archive archive = archive()
				.configured(asJpmsModule()
						.named("foo"))
				.with(DummyIntComparator.class)
				.with("META-INF/services/java.util.Comparator", cn(DummyIntComparator.class).getBytes(UTF_8))
				.with("META-INF/servicehistory", "one careful owner".getBytes(UTF_8))
				.toObjectModel();

		ModuleInfo moduleInfo = moduleInfoFrom(archive);

		assertThat(moduleInfo.provides(), is(anEmptyMap()));
	}
}
