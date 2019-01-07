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

import java.util.function.Predicate;

import javax.lang.model.SourceVersion;

import io.earcam.instrumental.archive.ArchiveConstruction;
import io.earcam.instrumental.archive.AsJarBuilder;
import io.earcam.instrumental.archive.jpms.auto.ClasspathModules;
import io.earcam.instrumental.archive.jpms.auto.JdkModules;
import io.earcam.instrumental.fluent.Fluent;

public interface AsJpmsModule extends AsJarBuilder<AsJpmsModule> {

	/**
	 * <p>
	 * asJpmsModule.
	 * </p>
	 *
	 * @return this builder
	 */
	@Fluent
	public static AsJpmsModule asJpmsModule()
	{
		return new DefaultAsJpmsModule();
	}


	public abstract AsJpmsModule named(String moduleName);


	public abstract AsJpmsModule versioned(String moduleVersion);


	/**
	 * <p>
	 * exporting... Composition of predicates is more powerful than regex or globs
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
	 * @return this builder
	 */
	public abstract AsJpmsModule exporting(Predicate<String> predicate, String... onlyToModules);


	/**
	 * <p>
	 * opening.
	 * </p>
	 *
	 * @param predicate a {@link java.util.function.Predicate} object.
	 * @param onlyToModules a {@link java.lang.String} object.
	 * @return this builder
	 */
	public abstract AsJpmsModule opening(Predicate<String> predicate, String... onlyToModules);


	/**
	 * <p>
	 * requiring.
	 * </p>
	 *
	 * @param moduleName a {@link java.lang.String} object.
	 * @return this builder
	 */
	public abstract AsJpmsModule requiring(String moduleName);


	/**
	 * <p>
	 * requiring.
	 * </p>
	 *
	 * @param moduleName a {@link java.lang.String} object.
	 * @param version a {@link java.lang.String} object.
	 * @return this builder
	 */
	public abstract AsJpmsModule requiring(String moduleName, String version);


	/**
	 * <p>
	 * using.
	 * </p>
	 *
	 * @param service a {@link java.lang.Class} object.
	 * @return this builder
	 */
	public abstract AsJpmsModule using(Class<?> service);


	/**
	 * <p>
	 * using.
	 * </p>
	 *
	 * @param service a {@link java.lang.String} object.
	 * @return this builder
	 */
	public abstract AsJpmsModule using(String service);


	/**
	 * <p>
	 * listingPackages.
	 * </p>
	 *
	 * @return this builder
	 */
	public abstract AsJpmsModule listingPackages();


	public default AsJpmsModule autoRequiring(SourceVersion jdkVersion)
	{
		return autoRequiring(jdkVersion.ordinal());
	}


	/**
	 * <p>
	 * Auto-require the JDK modules and classpaths.
	 * </p>
	 *
	 * @return this builder
	 * 
	 * @see #autoRequiringClasspath()
	 * @see #autoRequiringJdkModules(SourceVersion)
	 * @see #autoRequiringJdkModules(int)
	 */
	public default AsJpmsModule autoRequiring(int jdkVersion)
	{
		return autoRequiringJdkModules(jdkVersion)
				.autoRequiringClasspath();
	}


	/**
	 * <p>
	 * Auto-require from all available class-paths (inc. module-paths).
	 * </p>
	 *
	 * @return this builder
	 */
	public default AsJpmsModule autoRequiringClasspath()
	{
		return autoRequiring(new ClasspathModules());
	}


	/**
	 * <p>
	 * Auto-Require from the JDK's modules
	 * </p>
	 *
	 * @return this builder
	 */
	public default AsJpmsModule autoRequiringJdkModules(SourceVersion jdkVersion)
	{
		return autoRequiringJdkModules(jdkVersion.ordinal());
	}


	/**
	 * <p>
	 * Auto-Require from the JDK's modules
	 * </p>
	 *
	 * @param version dictate which version of modules JDK to use.
	 * @return this builder
	 */
	public abstract AsJpmsModule autoRequiringJdkModules(int version);


	/**
	 * <p>
	 * Note: if you also want the default {@link PackageModuleMapper} ({@link JdkModules} and {@link ClasspathModules})
	 * then you must also invoke {@link #autoRequiring(int)} or add them manually here
	 * </p>
	 *
	 * @param mappers an array of {@link io.earcam.instrumental.archive.jpms.PackageModuleMapper}s.
	 * @return this builder
	 * 
	 * @see #autoRequiring(int)
	 * @see #autoRequiring(SourceVersion)
	 * @see #autoRequiringJdkModules(SourceVersion)
	 * @see #autoRequiringClasspath()
	 */
	public abstract AsJpmsModule autoRequiring(PackageModuleMapper... mappers);


	/**
	 * <p>
	 * Note: if you also want the default {@link PackageModuleMapper} ({@link JdkModules} and {@link ClasspathModules})
	 * then you must also invoke {@link #autoRequiring()} or add them manually here
	 * </p>
	 *
	 * @param mappers a {@link java.lang.Iterable} object.
	 * @return this builder
	 * 
	 * @see #autoRequiring()
	 */
	public abstract AsJpmsModule autoRequiring(Iterable<PackageModuleMapper> mappers);


	/**
	 * Shorthand for {@code providingFromMetaInfServices(true)}
	 * 
	 * @return this builder
	 * 
	 * @see #providingFromMetaInfServices(boolean)
	 */
	public default AsJpmsModule providingFromMetaInfServices()
	{
		return providingFromMetaInfServices(true);
	}


	/**
	 * <p>
	 * This option enables support for scanning existing {@code META-INF/service/*} and
	 * adding any service implementations to the {@code module-info}'s {@code provides}
	 * entries.
	 * </p>
	 * <p>
	 * Use in conjunction with
	 * {@link ArchiveConstruction#sourcing(io.earcam.instrumental.archive.ArchiveResourceSource)}
	 * and e.g. {@link ArchiveConstruction#contentFrom(java.io.File)}
	 * </p>
	 * 
	 * @param enable true to enable, false to disable
	 * @return this builder
	 */
	public abstract AsJpmsModule providingFromMetaInfServices(boolean enable);
}
