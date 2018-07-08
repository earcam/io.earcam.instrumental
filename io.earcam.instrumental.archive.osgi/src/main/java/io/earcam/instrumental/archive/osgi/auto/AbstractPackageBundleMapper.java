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
package io.earcam.instrumental.archive.osgi.auto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.earcam.instrumental.archive.osgi.PackageBundleMapper;
import io.earcam.instrumental.module.osgi.BundleInfo;
import io.earcam.instrumental.module.osgi.Clause;
import io.earcam.instrumental.module.osgi.ClauseParameters.ClauseParameter;

/**
 * <p>
 * Abstract AbstractPackageBundleMapper class.
 * </p>
 *
 */
public abstract class AbstractPackageBundleMapper implements PackageBundleMapper {

	/**
	 * <p>
	 * bundles.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	protected abstract List<BundleInfo> bundles();


	/**
	 * <p>
	 * allAvailableExports.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	protected List<Clause> allAvailableExports()
	{
		return bundles().stream()
				.map(BundleInfo::exportPackage)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}


	/** {@inheritDoc} */
	@Override
	public List<Clause> importsFor(Iterator<String> requiredPackages)
	{
		List<Clause> availableExports = allAvailableExports();
		List<Clause> matchedExports = new ArrayList<>();

		while(requiredPackages.hasNext()) {
			String requiredPackage = requiredPackages.next();

			if(requiredPackage.startsWith("java.")) {   // OSGi spec 7.0.0 § 2.4.1 "Implied Permissions"
				requiredPackages.remove();
				continue;
			}

			for(Clause export : availableExports) {

				if(export.uniqueNames().contains(requiredPackage)) {
					requiredPackages.remove();
					// TODO version-range... optionally
					String version = export.parameters().attributeOrDefault("version", "0.0.0");
					matchedExports.add(new Clause(requiredPackage, ClauseParameter.attribute("version", version)));
				}
			}
		}
		return matchedExports;
	}
}
