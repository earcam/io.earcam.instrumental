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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.module.osgi.BundleInfo;
import io.earcam.instrumental.module.osgi.BundleInfoBuilder;

public final class ArchivePackageBundleMapper extends AbstractPackageBundleMapper {

	private final List<BundleInfo> bundles;


	private ArchivePackageBundleMapper(List<BundleInfo> bundles)
	{
		this.bundles = bundles;

	}


	public static ArchivePackageBundleMapper byMappingBundleArchives(Archive... archives)
	{
		return byMappingBundleArchives(Arrays.asList(archives));
	}


	public static ArchivePackageBundleMapper byMappingBundleArchives(List<Archive> archives)
	{
		List<BundleInfo> bundles = archives.stream()
				.map(Archive::manifest)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(BundleInfoBuilder::bundleFrom)
				.map(BundleInfoBuilder::construct)
				.collect(Collectors.toList());

		return new ArchivePackageBundleMapper(bundles);
	}


	@Override
	protected List<BundleInfo> bundles()
	{
		return bundles;
	}

}
