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
package io.earcam.instrumental.archive.jpms.auto;

import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.AsJar.asJar;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.module.jpms.ModuleInfo;

public class ArchivePackageModuleMapperTest {

	@Test
	void maps()
	{
		ModuleInfo moduleInfo = ModuleInfo.moduleInfo().named("blah.blah").exporting("some.paquet").construct();

		Archive module = archive()
				.configured(asJar())
				.with("module-info.class", moduleInfo.toBytecode())
				.toObjectModel();

		ArchivePackageModuleMapper mapper = ArchivePackageModuleMapper.byMappingModuleArchives(module);

		assertThat(mapper.modules(), contains(moduleInfo));
	}

}
