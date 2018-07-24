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

import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.ArchiveConstruction.contentFrom;
import static io.earcam.instrumental.archive.jpms.AsJpmsModule.asJpmsModule;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.module.jpms.Export;
import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.instrumental.reflect.Resources;
import io.earcam.unexceptional.Exceptional;

public class AsJpmsModuleIntegrationTest {

	@Test
	public void produceModuleInfoForProjectUnexceptional() throws Exception
	{
		// EARCAM_SNIPPET_BEGIN: add-to-jar
		Path jar = Paths.get(Resources.sourceOfResource(Exceptional.class));

		// removes any existing module-info.class
		Predicate<String> filter = n -> !"module-info.class".equals(n);

		Archive jpmsed = archive()
				.configured(
						asJpmsModule()
								.autoRequiring()
								.exporting(p -> true)
								.named(Exceptional.class.getPackage().getName()))
				.sourcing(contentFrom(jar, filter))
				.toObjectModel();

		ArchiveResource moduleInfoBinary = jpmsed.content("module-info.class").orElseThrow(NullPointerException::new);

		ModuleInfo moduleInfoSource = ModuleInfo.read(moduleInfoBinary.bytes());

		// EARCAM_SNIPPET_END: add-to-jar
		assertThat(moduleInfoSource.name(), is(equalTo("io.earcam.unexceptional")));
		assertThat(moduleInfoSource.exports(), contains(new Export("io.earcam.unexceptional")));

		assertThat(moduleInfoSource.requires(), hasSize(1));
		assertThat(moduleInfoSource.requires().iterator().next().module(), is(equalTo("java.base")));
	}
}
