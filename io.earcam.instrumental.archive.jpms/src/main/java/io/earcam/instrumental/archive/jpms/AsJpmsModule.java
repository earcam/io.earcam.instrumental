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
	 * @return a {@link io.earcam.instrumental.archive.jpms.AsJpmsModule} object.
	 */
	@Fluent
	public static AsJpmsModule asJpmsModule()
	{
		return new DefaultAsJpmsModule();
	}


	public abstract AsJpmsModule named(String moduleName);


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
	 * @return a {@link io.earcam.instrumental.archive.jpms.AsJpmsModule} object.
	 */
	public abstract AsJpmsModule exporting(Predicate<String> predicate, String... onlyToModules);


	/**
	 * <p>
	 * opening.
	 * </p>
	 *
	 * @param predicate a {@link java.util.function.Predicate} object.
	 * @param onlyToModules a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.AsJpmsModule} object.
	 */
	public abstract AsJpmsModule opening(Predicate<String> predicate, String... onlyToModules);


	/**
	 * <p>
	 * requiring.
	 * </p>
	 *
	 * @param moduleName a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.AsJpmsModule} object.
	 */
	public abstract AsJpmsModule requiring(String moduleName);


	/**
	 * <p>
	 * requiring.
	 * </p>
	 *
	 * @param moduleName a {@link java.lang.String} object.
	 * @param version a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.AsJpmsModule} object.
	 */
	public abstract AsJpmsModule requiring(String moduleName, String version);


	/**
	 * <p>
	 * using.
	 * </p>
	 *
	 * @param service a {@link java.lang.Class} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.AsJpmsModule} object.
	 */
	public abstract AsJpmsModule using(Class<?> service);


	/**
	 * <p>
	 * using.
	 * </p>
	 *
	 * @param service a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.AsJpmsModule} object.
	 */
	public abstract AsJpmsModule using(String service);


	/**
	 * <p>
	 * listingPackages.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.jpms.AsJpmsModule} object.
	 */
	public abstract AsJpmsModule listingPackages();


	/**
	 * <p>
	 * autoRequiring.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.jpms.AsJpmsModule} object.
	 */
	public abstract AsJpmsModule autoRequiring();


	/**
	 * <p>
	 * Note: if you also want the default {@link PackageModuleMapper} ({@link JdkModules} and {@link ClasspathModules})
	 * then you must also invoke {@link #autoRequiring()} or add them manually here
	 * </p>
	 *
	 * @param mappers a {@link io.earcam.instrumental.archive.jpms.PackageModuleMapper} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.AsJpmsModule} object.
	 * 
	 * @see #autoRequiring()
	 */
	public abstract AsJpmsModule autoRequiring(PackageModuleMapper... mappers);


	/**
	 * <p>
	 * Note: if you also want the default {@link PackageModuleMapper} ({@link JdkModules} and {@link ClasspathModules})
	 * then you must also invoke {@link #autoRequiring()} or add them manually here
	 * </p>
	 *
	 * @param mappers a {@link java.lang.Iterable} object.
	 * @return a {@link io.earcam.instrumental.archive.jpms.AsJpmsModule} object.
	 * 
	 * @see #autoRequiring()
	 */
	public abstract AsJpmsModule autoRequiring(Iterable<PackageModuleMapper> mappers);
}
