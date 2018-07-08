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

import static io.earcam.instrumental.module.osgi.BundleManifestHeaders.*;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;

import javax.annotation.Nullable;

import io.earcam.instrumental.module.manifest.ManifestInfo;

/**
 * <p>
 * BundleInfo interface.
 * </p>
 *
 */
public interface BundleInfo extends ManifestInfo {

	/**
	 * <p>
	 * allHeaderClauses.
	 * </p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public abstract Map<Name, List<Clause>> allHeaderClauses();


	/**
	 * <p>
	 * deconstruct.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public abstract BundleInfoBuilder deconstruct();


	/**
	 * <p>
	 * exportPackage.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public default List<Clause> exportPackage()
	{
		return allHeaderClauses().getOrDefault(EXPORT_PACKAGE.header(), emptyList());
	}


	/**
	 * <p>
	 * importPackage.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public default List<Clause> importPackage()
	{
		return allHeaderClauses().getOrDefault(IMPORT_PACKAGE.header(), emptyList());
	}


	/**
	 * <p>
	 * dynamicImportPackage.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public default List<Clause> dynamicImportPackage()
	{
		return allHeaderClauses().getOrDefault(DYNAMICIMPORT_PACKAGE.header(), emptyList());
	}


	/**
	 * <p>
	 * symbolicName.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.osgi.Clause} object.
	 */
	public default Clause symbolicName()
	{
		return allHeaderClauses().get(BUNDLE_SYMBOLICNAME.header()).get(0);
	}


	/**
	 * <p>
	 * symbolicName.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.osgi.Clause} object.
	 */
	public default Clause fragmentHost()
	{
		return allHeaderClauses().get(FRAGMENT_HOST.header()).get(0);
	}


	/**
	 * <p>
	 * activator.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public default @Nullable String activator()
	{
		List<Clause> header = allHeaderClauses().get(BUNDLE_ACTIVATOR.header());
		return (header == null) ? null : header.get(0).uniqueNames().first();
	}
}
