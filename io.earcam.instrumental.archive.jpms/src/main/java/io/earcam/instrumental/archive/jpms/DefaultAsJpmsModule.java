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

import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.PRE_MANIFEST;
import static io.earcam.instrumental.module.auto.Reader.reader;
import static io.earcam.instrumental.module.jpms.RequireModifier.MANDATED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.earcam.instrumental.archive.AbstractAsJarBuilder;
import io.earcam.instrumental.archive.ArchiveRegistrar;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.archive.ArchiveResourceListener;
import io.earcam.instrumental.archive.jpms.auto.ClasspathModules;
import io.earcam.instrumental.archive.jpms.auto.JdkModules;
import io.earcam.instrumental.module.auto.Reader;
import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.instrumental.module.jpms.ModuleInfoBuilder;

/**
 * <p>
 * AsJpmsModule class.
 * </p>
 *
 */
class DefaultAsJpmsModule extends AbstractAsJarBuilder<AsJpmsModule> implements AsJpmsModule, ArchiveResourceListener {

	private final ModuleInfoBuilder builder = ModuleInfo.moduleInfo();

	class ExportMatcher {
		final Predicate<String> matcher;
		final String[] to;
		final BiConsumer<String, String[]> method;


		ExportMatcher(BiConsumer<String, String[]> method, Predicate<String> matcher, String... to)
		{
			this.method = method;
			this.matcher = matcher;
			this.to = to;
		}


		public void test(ArchiveResource resource)
		{
			String pkg = resource.pkg();
			if(matcher.test(pkg)) {
				method.accept(pkg, to);
			}
		}
	}

	class AutoRequiring {
		final Map<String, Set<String>> imports = new HashMap<>();
		final Reader reader = reader()
				.addImportListener(imports::put)
				.ignoreAnnotations()
				.setImportedTypeReducer(Reader::typeToPackageReducer)
				.setImportingTypeReducer(Reader::typeToPackageReducer);


		void process(ArchiveResource resource)
		{
			reader.processClass(resource.bytes());
		}


		Set<String> imported()
		{
			Predicate<? super String> ownPackages = imports.keySet()::contains;

			Stream<Set<String>> importsRequired = Stream.concat(
					imports.values().stream(),
					Stream.of(singleton("java.lang")));

			return importsRequired
					.flatMap(Set::stream)
					.filter(ownPackages.negate())
					.collect(toSet());
		}
	}

	private final List<ExportMatcher> exportMatchers = new ArrayList<>();
	private boolean listingPackages;

	private AutoRequiring autoRequiring;
	private List<PackageModuleMapper> packageModuleMappers = new ArrayList<>();
	private boolean providingFromMetaInfServices;


	DefaultAsJpmsModule()
	{}


	@Override
	protected AsJpmsModule self()
	{
		return this;
	}


	@Override
	public void added(ArchiveResource resource)
	{
		super.added(resource);
		if(listingPackages) {
			builder.packaging(resource.pkg());
		}
		if(resource.isQualifiedClass()) {
			if(autoRequiring != null) {
				autoRequiring.process(resource);
			}
			for(ExportMatcher matcher : exportMatchers) {
				matcher.test(resource);
			}
		} else if(providingFromMetaInfServices && resource.name().startsWith(SPI_ROOT_PATH)) { // config optional
			String service = resource.name().substring(SPI_ROOT_PATH.length());
			String[] implementations = new String(resource.bytes(), UTF_8).split("\r?\n");
			builder.providing(service, implementations);
		}
	}


	@Override
	public Stream<ArchiveResource> drain(ResourceSourceLifecycle stage)
	{
		Stream<ArchiveResource> drained = super.drain(stage);
		if(stage == PRE_MANIFEST) {

			resolveAutoImports();
			ArchiveResource moduleInfo = new ArchiveResource("module-info.class", builder.construct().toBytecode());
			drained = Stream.concat(drained, Stream.of(moduleInfo));
		}
		return drained;
	}


	private void resolveAutoImports()
	{
		if(autoRequiring == null) {
			return;
		}
		Set<String> imports = autoRequiring.imported();
		String name = ((ModuleInfo) builder).name();

		for(PackageModuleMapper mapper : packageModuleMappers) {
			mapper.moduleRequiredFor(name, imports.iterator())
					.forEach(builder::requiring);
		}

		if(validate() && !imports.isEmpty()) {
			throw new IllegalStateException("For module '" + name + "' unresolved imports remain: " + imports);
		}
	}


	@Override
	public void attach(ArchiveRegistrar core)
	{
		super.attach(core);
		core.registerResourceListener(this);
	}


	@Override
	public DefaultAsJpmsModule launching(Class<?> mainClass)
	{
		super.launching(mainClass);
		builder.launching(cn(mainClass));
		return this;
	}


	@Override
	public AsJpmsModule launching(String mainClass)
	{
		super.launching(mainClass);
		return this;
	}


	private static String cn(Class<?> type)
	{
		return type.getCanonicalName();
	}


	@Override
	public AsJpmsModule providing(Class<?> service, List<Class<?>> implementations)
	{
		super.providing(service, implementations);
		Set<String> imps = implementations.stream()
				.map(DefaultAsJpmsModule::cn)
				.collect(toSet());
		return providing(cn(service), imps);
	}


	@Override
	public AsJpmsModule providing(String contract, Set<String> concretes)
	{
		super.providing(contract, concretes);
		builder.providing(contract, concretes);
		return this;
	}


	/**
	 * <p>
	 * named.
	 * </p>
	 *
	 * @param moduleName a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.DefaultAsJpmsModule} object.
	 */
	@Override
	public AsJpmsModule named(String moduleName)
	{
		builder.named(moduleName);
		return this;
	}


	@Override
	public AsJpmsModule versioned(String moduleVersion)
	{
		builder.versioned(moduleVersion);
		return this;
	}


	/**
	 * <p>
	 * exporting... Composition of predicates is more powerful than regex or globs.
	 * Exports classes and resources
	 * </p>
	 *
	 * @param predicate a {@link java.util.function.Predicate} object.
	 * @param onlyToModules a {@link java.lang.String} object.
	 * @see java.util.regex.Pattern#asPredicate()
	 * @see Predicate#and(Predicate)
	 * @see Predicate#or(Predicate)
	 * @see Predicate#negate()
	 * @see Predicate#and(Predicate)
	 * @see Predicate#or(Predicate)
	 * @see Predicate#negate()
	 * @see Predicate#and(Predicate)
	 * @see Predicate#or(Predicate)
	 * @see Predicate#negate()
	 * @return a {@link io.earcam.instrumental.archive.jpms.DefaultAsJpmsModule} object.
	 */
	@Override
	public AsJpmsModule exporting(Predicate<String> predicate, String... onlyToModules)
	{
		exportMatchers.add(new ExportMatcher(builder::exporting, predicate, onlyToModules));
		return this;
	}


	/**
	 * <p>
	 * opening.
	 * </p>
	 *
	 * @param predicate a {@link java.util.function.Predicate} object.
	 * @param onlyToModules a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.DefaultAsJpmsModule} object.
	 */
	@Override
	public AsJpmsModule opening(Predicate<String> predicate, String... onlyToModules)
	{
		exportMatchers.add(new ExportMatcher(builder::opening, predicate, onlyToModules));
		return this;
	}


	/**
	 * <p>
	 * requiring.
	 * </p>
	 *
	 * @param moduleName a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.DefaultAsJpmsModule} object.
	 */
	@Override
	public AsJpmsModule requiring(String moduleName)
	{
		return requiring(moduleName, null);
	}


	/**
	 * <p>
	 * requiring.
	 * </p>
	 *
	 * @param moduleName a {@link java.lang.String} object.
	 * @param version a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.DefaultAsJpmsModule} object.
	 */
	@Override
	public AsJpmsModule requiring(String moduleName, String version)
	{
		builder.requiring(moduleName, EnumSet.of(MANDATED), version);
		return this;
	}


	/**
	 * <p>
	 * using.
	 * </p>
	 *
	 * @param service a {@link java.lang.Class} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.DefaultAsJpmsModule} object.
	 */
	@Override
	public AsJpmsModule using(Class<?> service)
	{
		return using(service.getCanonicalName());
	}


	/**
	 * <p>
	 * using.
	 * </p>
	 *
	 * @param service a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.DefaultAsJpmsModule} object.
	 */
	@Override
	public AsJpmsModule using(String service)
	{
		builder.using(service);
		return this;
	}


	/**
	 * <p>
	 * listingPackages.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.jpms.DefaultAsJpmsModule} object.
	 */
	@Override
	public AsJpmsModule listingPackages()
	{
		listingPackages = true;
		return this;
	}


	@Override
	public AsJpmsModule autoRequiringClasspath()
	{
		return autoRequiring(new ClasspathModules());
	}


	@Override
	public AsJpmsModule autoRequiringJdkModules(int jdkVersion)
	{
		return autoRequiring(new JdkModules(jdkVersion));
	}


	/**
	 * <p>
	 * Note: if you also want the default {@link PackageModuleMapper} ({@link JdkModules} and {@link ClasspathModules})
	 * then you must also invoke {@link #autoRequiring()} or add them manually here
	 * </p>
	 *
	 * @param mappers a {@link io.earcam.instrumental.archive.jpms.PackageModuleMapper} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.DefaultAsJpmsModule} object.
	 * 
	 * @see #autoRequiring()
	 */
	@Override
	public AsJpmsModule autoRequiring(PackageModuleMapper... mappers)
	{
		return autoRequiring(Arrays.asList(mappers));
	}


	/**
	 * <p>
	 * Note: if you also want the default {@link PackageModuleMapper} ({@link JdkModules} and {@link ClasspathModules})
	 * then you must also invoke {@link #autoRequiring()} or add them manually here
	 * </p>
	 *
	 * @param mappers a {@link java.lang.Iterable} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.DefaultAsJpmsModule} object.
	 * 
	 * @see #autoRequiring()
	 */
	@Override
	public AsJpmsModule autoRequiring(Iterable<PackageModuleMapper> mappers)
	{
		autoRequiring = new AutoRequiring();
		mappers.forEach(packageModuleMappers::add);
		return this;
	}


	@Override
	public AsJpmsModule providingFromMetaInfServices(boolean enable)
	{
		this.providingFromMetaInfServices = enable;
		return this;
	}
}
