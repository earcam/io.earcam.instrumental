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

import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.AsJar.asJar;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.module.osgi.BundleInfoBuilder;

public class ArchivePackageBundleMapperTest {

	@Test
	void maps()
	{
		BundleInfoBuilder bundleInfoBuilder = BundleInfoBuilder.bundle().symbolicName("blah.blah").exportPackages("some.paquet");

		Archive bundle = archive()
				.configured(asJar()
						.mergingManifest(bundleInfoBuilder.toManifest()))
				.toObjectModel();

		ArchivePackageBundleMapper mapper = ArchivePackageBundleMapper.byMappingBundleArchives(bundle);

		assertThat(mapper.bundles(), contains(bundleInfoBuilder.construct()));
	}

}
