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

import static io.earcam.instrumental.module.auto.Reader.reader;
import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.Manifest;

import org.osgi.framework.BundleActivator;

import io.earcam.instrumental.archive.AbstractAsJarBuilder;
import io.earcam.instrumental.archive.ArchiveRegistrar;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.archive.ArchiveResourceListener;
import io.earcam.instrumental.archive.osgi.auto.ClasspathBundles;
import io.earcam.instrumental.module.auto.Reader;
import io.earcam.instrumental.module.osgi.BundleInfoBuilder;
import io.earcam.instrumental.module.osgi.ClauseParameters;
import io.earcam.instrumental.reflect.Types;

/**
 * <p>
 * AsOsgiBundle class.
 * </p>
 *
 */
class DefaultAsOsgiBundle extends AbstractAsJarBuilder<AsOsgiBundle> implements AsOsgiBundle, ArchiveResourceListener {

	private final BundleInfoBuilder builder = BundleInfoBuilder.bundle();

	class ExportMatcher {
		final Predicate<String> matcher;
		final ClauseParameters parameters;


		ExportMatcher(Predicate<String> matcher, ClauseParameters parameters)
		{
			this.matcher = matcher;
			this.parameters = parameters;
		}


		public boolean test(ArchiveResource resource)
		{
			String pkg = resource.pkg();
			if(matcher.test(pkg)) {
				builder.exportPackages(pkg, parameters);
				return true;
			}
			return false;
		}
	}

	class AutoImporting {
		final Map<String, Set<String>> imports = new HashMap<>();
		final Reader reader = reader()
				.addImportListener(imports::put)
				.setImportedTypeReducer(Reader::typeToPackageReducer)
				.setImportingTypeReducer(Reader::typeToPackageReducer);


		void process(ArchiveResource resource)
		{
			reader.processClass(resource.bytes());
		}


		Set<String> imported()
		{
			Predicate<? super String> ownPackages = imports.keySet()::contains;

			return imports.values().stream()
					.flatMap(Set::stream)
					.filter(ownPackages.negate())
					.collect(toCollection(HashSet::new));
		}

	}

	private final List<ExportMatcher> exportMatchers = new ArrayList<>();

	private final List<PackageBundleMapper> packageBundleMappers = new ArrayList<>();

	private AutoImporting autoImporting;


	DefaultAsOsgiBundle()
	{}


	@Override
	protected AsOsgiBundle self()
	{
		return this;
	}


	@Override
	public void attach(ArchiveRegistrar core)
	{
		super.attach(core);
		core.registerResourceListener(this);
	}


	@Override
	public void added(ArchiveResource resource)
	{
		super.added(resource);

		if(autoImporting != null && resource.isClass()) {
			autoImporting.process(resource);
		}

		for(ExportMatcher matcher : exportMatchers) {
			if(resource.isQualifiedClass()) {
				matcher.test(resource);
				if(matcher.test(resource)) {
					break;
				}
			}
		}
	}


	@Override
	public void process(Manifest manifest)
	{
		super.process(manifest);
		resolveAutoImports();
		builder.to(manifest);
	}


	private void resolveAutoImports()
	{
		if(autoImporting == null) {
			return;
		}
		Set<String> imports = autoImporting.imported();

		for(PackageBundleMapper mapper : packageBundleMappers) {
			mapper.importsFor(imports.iterator())
					.forEach(builder::importPackages);
		}

		if(validate() && !imports.isEmpty()) {
			throw new IllegalStateException("' unresolved imports remain: " + imports);
		}
	}


	@Override
	public AsOsgiBundle named(String symbolicName, ClauseParameters parameters)
	{
		builder.symbolicName(symbolicName, parameters);
		return this;
	}


	@Override
	public AsOsgiBundle withActivator(Class<?> activator)
	{
		if(validate()) {
			requireActivator(activator);
		}
		source.with(activator);
		return withActivator(activator.getCanonicalName());
	}


	@Override
	public AsOsgiBundle withActivator(String canonicalName)
	{
		builder.activator(canonicalName);
		return this;
	}


	private static final void requireActivator(Class<?> activator)
	{
		if(!Types.implementsAll(activator, BundleActivator.class)) {
			throw new IllegalArgumentException(activator + " does not implement " + BundleActivator.class);
		}
	}


	@Override
	public AsOsgiBundle exporting(Predicate<String> exportMatcher, ClauseParameters parameters)
	{
		exportMatchers.add(new ExportMatcher(exportMatcher, parameters));
		return this;
	}


	/**
	 * <p>
	 * exporting.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.archive.osgi.DefaultAsOsgiBundle} object.
	 */
	@Override
	public AsOsgiBundle exporting(Class<?> type, ClauseParameters parameters)
	{
		source.with(type);
		return exporting(type.getPackage(), parameters);
	}


	/**
	 * <p>
	 * exporting.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.archive.osgi.DefaultAsOsgiBundle} object.
	 */
	@Override
	public AsOsgiBundle exporting(String paquet, ClauseParameters parameters)
	{
		builder.exportPackages(paquet, parameters);
		return this;
	}


	/**
	 * <p>
	 * importing.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.archive.osgi.DefaultAsOsgiBundle} object.
	 */
	@Override
	public AsOsgiBundle importing(String paquet, ClauseParameters parameters)
	{
		builder.importPackages(paquet, parameters);
		return this;
	}


	/**
	 * <p>
	 * autoImporting.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.osgi.DefaultAsOsgiBundle} object.
	 */
	@Override
	public AsOsgiBundle autoImporting()
	{
		return autoImporting(new ClasspathBundles());
	}


	@Override
	public AsOsgiBundle autoImporting(List<PackageBundleMapper> mappers)
	{
		this.autoImporting = new AutoImporting();
		mappers.forEach(packageBundleMappers::add);
		return this;
	}
}
