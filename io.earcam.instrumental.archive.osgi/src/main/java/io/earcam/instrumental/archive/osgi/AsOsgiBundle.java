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

import static io.earcam.instrumental.module.osgi.ClauseParameters.EMPTY_PARAMETERS;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import io.earcam.instrumental.archive.AsJarBuilder;
import io.earcam.instrumental.fluent.Fluent;
import io.earcam.instrumental.module.osgi.ClauseParameters;

/**
 * <p>
 * AsOsgiBundle class.
 * </p>
 *
 */
public interface AsOsgiBundle extends AsJarBuilder<AsOsgiBundle> {

	/**
	 * <p>
	 * asOsgiBundle.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.osgi.AsOsgiBundle} object.
	 */
	@Fluent
	public static AsOsgiBundle asOsgiBundle()
	{
		return new DefaultAsOsgiBundle();
	}


	public default AsOsgiBundle named(String symbolicName)
	{
		return named(symbolicName, EMPTY_PARAMETERS);
	}


	public abstract AsOsgiBundle named(String symbolicName, ClauseParameters parameters);


	public abstract AsOsgiBundle withActivator(Class<?> activator);


	public abstract AsOsgiBundle withActivator(String canonicalName);


	/**
	 * <p>
	 * <code>Export-Package</code> using a predicate string matching.
	 * </p>
	 * <p>
	 * The matchers are applied in order - after the first successful match,
	 * no other matchers are tested.
	 * </p>
	 *
	 * @param exportMatcher a {@link java.util.function.Predicate} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.archive.osgi.DefaultAsOsgiBundle} object.
	 */
	public abstract AsOsgiBundle exporting(Predicate<String> exportMatcher, ClauseParameters parameters);


	/**
	 * <p>
	 * <code>Export-Package</code> without attributes or directives
	 * </p>
	 * 
	 * @param type
	 * @return
	 */
	public default AsOsgiBundle exporting(Class<?> type)
	{
		return exporting(type, EMPTY_PARAMETERS);
	}


	public abstract AsOsgiBundle exporting(Class<?> type, ClauseParameters parameters);


	public default AsOsgiBundle exporting(Package paquet, ClauseParameters parameters)
	{
		return exporting(paquet.getName(), parameters);
	}


	public abstract AsOsgiBundle exporting(String paquet, ClauseParameters parameters);


	public default AsOsgiBundle importing(String paquet)
	{
		return importing(paquet, EMPTY_PARAMETERS);
	}


	public abstract AsOsgiBundle importing(String paquet, ClauseParameters parameters);


	public abstract AsOsgiBundle autoImporting();


	public default AsOsgiBundle autoImporting(PackageBundleMapper... mappers)
	{
		return autoImporting(Arrays.asList(mappers));
	}


	public abstract AsOsgiBundle autoImporting(List<PackageBundleMapper> mappers);
}
