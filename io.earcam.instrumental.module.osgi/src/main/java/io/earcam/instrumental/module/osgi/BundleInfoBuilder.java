/*-
 * #%L
 * io.earcam.instrumental.module.osgi
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
package io.earcam.instrumental.module.osgi;

import static io.earcam.instrumental.module.manifest.ManifestInfoBuilder.attribute;
import static io.earcam.instrumental.module.osgi.BundleManifestHeaders.BUNDLE_MANIFESTVERSION;
import static io.earcam.instrumental.module.osgi.ClauseParameters.EMPTY_PARAMETERS;
import static io.earcam.instrumental.module.osgi.OsgiManifestHeaderConstants.BUNDLE_ACTIVATOR;
import static io.earcam.instrumental.module.osgi.OsgiManifestHeaderConstants.BUNDLE_SYMBOLICNAME;
import static io.earcam.instrumental.module.osgi.OsgiManifestHeaderConstants.DYNAMICIMPORT_PACKAGE;
import static io.earcam.instrumental.module.osgi.OsgiManifestHeaderConstants.EXPORT_PACKAGE;
import static io.earcam.instrumental.module.osgi.OsgiManifestHeaderConstants.FRAGMENT_HOST;
import static io.earcam.instrumental.module.osgi.OsgiManifestHeaderConstants.IMPORT_PACKAGE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.SortedSet;
import java.util.function.Predicate;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import io.earcam.instrumental.fluent.Fluent;
import io.earcam.instrumental.module.manifest.ManifestInfoBuilder;
import io.earcam.instrumental.module.osgi.parser.BundleInfoParser;
import io.earcam.instrumental.reflect.Types;
import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * BundleInfoBuilder interface.
 * </p>
 *
 */
public interface BundleInfoBuilder extends ManifestInfoBuilder<BundleInfoBuilder> {

	/**
	 * <p>
	 * bundle.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	@Fluent
	public static BundleInfoBuilder bundle()
	{
		return new DefaultBundleInfo();
	}


	/**
	 * <p>
	 * bundleFrom.
	 * </p>
	 *
	 * @param input a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	@Fluent
	public static BundleInfoBuilder bundleFrom(String input)
	{
		return BundleInfoParser.parse(input);
	}


	/**
	 * <p>
	 * bundleFrom.
	 * </p>
	 *
	 * @param input a {@link java.io.InputStream} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	@Fluent
	public static BundleInfoBuilder bundleFrom(InputStream input)
	{
		return BundleInfoParser.parse(input);
	}


	/**
	 * <p>
	 * bundleManifestVersion.
	 * </p>
	 *
	 * @param version a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder bundleManifestVersion(String version)
	{
		return manifestMain(attribute(BUNDLE_MANIFESTVERSION.header(), version));
	}


	/**
	 * <p>
	 * symbolicName.
	 * </p>
	 *
	 * @param name a {@link java.lang.CharSequence} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder symbolicName(CharSequence name)
	{
		return symbolicName(name, EMPTY_PARAMETERS);
	}


	/**
	 * <p>
	 * symbolicName.
	 * </p>
	 *
	 * @param name a {@link java.lang.CharSequence} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder symbolicName(CharSequence name, ClauseParameters parameters)
	{
		return headerClause(BUNDLE_SYMBOLICNAME.value, new Clause(Clause.sortedSet(name.toString()), parameters));
	}


	/**
	 * <p>
	 * symbolicName.
	 * </p>
	 *
	 * @param name a {@link java.lang.CharSequence} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder fragmentHost(CharSequence name, ClauseParameters parameters)
	{
		return headerClause(FRAGMENT_HOST.value, new Clause(Clause.sortedSet(name.toString()), parameters));
	}


	/**
	 * <p>
	 * exportPackages.
	 * </p>
	 *
	 * @param packages a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder exportPackages(String... packages)
	{
		return exportPackages(packages, EMPTY_PARAMETERS);
	}


	/**
	 * <p>
	 * exportPackages.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder exportPackages(String paquet, ClauseParameters parameters)
	{
		return exportPackages(new String[] { paquet }, parameters);
	}


	/**
	 * <p>
	 * exportPackages.
	 * </p>
	 *
	 * @param packages an array of {@link java.lang.String} objects.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder exportPackages(String[] packages, ClauseParameters parameters)
	{
		return exportPackages(Clause.sortedSet(packages), parameters);
	}


	/**
	 * <p>
	 * exportPackages.
	 * </p>
	 *
	 * @param packages a {@link java.util.SortedSet} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder exportPackages(SortedSet<String> packages)
	{
		return exportPackages(packages, EMPTY_PARAMETERS);
	}


	/**
	 * <p>
	 * exportPackages.
	 * </p>
	 *
	 * @param packages a {@link java.util.SortedSet} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder exportPackages(SortedSet<String> packages, ClauseParameters parameters)
	{
		return headerClause(EXPORT_PACKAGE.value, new Clause(packages, parameters));
	}


	/**
	 * <p>
	 * importPackages.
	 * </p>
	 *
	 * @param packages a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder importPackages(String... packages)
	{
		return importPackages(packages, EMPTY_PARAMETERS);
	}


	/**
	 * <p>
	 * importPackages.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder importPackages(String paquet, ClauseParameters parameters)
	{
		return importPackages(Clause.sortedSet(paquet), parameters);
	}


	/**
	 * <p>
	 * importPackages.
	 * </p>
	 *
	 * @param packages an array of {@link java.lang.String} objects.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder importPackages(String[] packages, ClauseParameters parameters)
	{
		return importPackages(Clause.sortedSet(packages), parameters);
	}


	/**
	 * <p>
	 * importPackages.
	 * </p>
	 *
	 * @param packages a {@link java.util.SortedSet} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder importPackages(SortedSet<String> packages)
	{
		return importPackages(packages, EMPTY_PARAMETERS);
	}


	/**
	 * <p>
	 * importPackages.
	 * </p>
	 *
	 * @param packages a {@link java.util.SortedSet} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder importPackages(SortedSet<String> packages, ClauseParameters parameters)
	{
		return importPackages(new Clause(packages, parameters));
	}


	/**
	 * <p>
	 * importPackages.
	 * </p>
	 *
	 * @param clause a {@link io.earcam.instrumental.module.osgi.Clause} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder importPackages(Clause clause)
	{
		return headerClause(IMPORT_PACKAGE.value, clause);
	}


	/**
	 * <p>
	 * dynamic importPackages.
	 * </p>
	 *
	 * @param packages a {@link java.util.SortedSet} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder dynamicImportPackages(SortedSet<String> packages, ClauseParameters parameters)
	{
		return dynamicImportPackages(new Clause(packages, parameters));
	}


	/**
	 * <p>
	 * dynamic importPackages.
	 * </p>
	 *
	 * @param clause a {@link io.earcam.instrumental.module.osgi.Clause} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder dynamicImportPackages(Clause clause)
	{
		return headerClause(DYNAMICIMPORT_PACKAGE.value, clause);
	}


	/**
	 * <p>
	 * headerClause.
	 * </p>
	 *
	 * @param header a {@link java.lang.String} object.
	 * @param clause a {@link io.earcam.instrumental.module.osgi.Clause} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder headerClause(String header, Clause clause)
	{
		return headerClause(new Name(header), clause);
	}


	/**
	 * <p>
	 * headerClause.
	 * </p>
	 *
	 * @param header a {@link java.util.jar.Attributes.Name} object.
	 * @param clause a {@link io.earcam.instrumental.module.osgi.Clause} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public abstract BundleInfoBuilder headerClause(Name header, Clause clause);


	/**
	 * <p>
	 * activator.
	 * </p>
	 *
	 * @param className a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder activator(Class<?> activator)
	{
		final String iface = "org.osgi.framework.BundleActivator";
		boolean implementsActivator = Types.allInterfacesOf(activator)
				.map(Class::getCanonicalName)
				.anyMatch(Predicate.isEqual(iface));
		if(!implementsActivator) {
			throw new IllegalArgumentException(activator.getCanonicalName() + " does not implement " + iface);
		}
		return activator(activator.getCanonicalName());
	}


	/**
	 * <p>
	 * activator.
	 * </p>
	 *
	 * @param className a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public default BundleInfoBuilder activator(String className)
	{
		return headerClause(BUNDLE_ACTIVATOR.value, new Clause(className, EMPTY_PARAMETERS));
	}


	/**
	 * <p>
	 * construct.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfo} object.
	 */
	public abstract BundleInfo construct();


	@Fluent
	public static BundleInfoBuilder bundleFrom(Manifest manifest)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Exceptional.accept(manifest::write, baos);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		return BundleInfoBuilder.bundleFrom(bais);
	}
}
