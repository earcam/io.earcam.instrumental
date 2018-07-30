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
import static io.earcam.instrumental.archive.jpms.AsJpmsModule.asJpmsModule;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.acme.require.ImportsDummyIntComparator;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.archive.jpms.PackageModuleMapper;
import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.instrumental.reflect.Names;

public class FailureToResolveAutoImportTest {

	final PackageModuleMapper noopMapper = new AbstractPackageModuleMapper() {

		@Override
		protected List<ModuleInfo> modules()
		{
			return Collections.emptyList();
		}
	};


	@Test
	public void whenImportsUmappedThenThrows()
	{

		try {
			archive()
					.configured(asJpmsModule()
							.named("immediate.bang")
							.autoRequiring(noopMapper))
					.with(ImportsDummyIntComparator.class)
					.toObjectModel();

			fail();
		} catch(IllegalStateException e) {}
	}


	@Test
	public void givenValidationDisableWhenImportsUmappedThenDoesNotThrow()
	{
		Archive archive = archive()
				.configured(asJpmsModule()
						.named("fail.silently.much.later.on")
						.disableValidation()
						.autoRequiring(noopMapper))
				.with()
				.toObjectModel();

		assertThat(archive.content(Names.typeToResourceName(ImportsDummyIntComparator.class)), is(not(nullValue())));
	}
}
