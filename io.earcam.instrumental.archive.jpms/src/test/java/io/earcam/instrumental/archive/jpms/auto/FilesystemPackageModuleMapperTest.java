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
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.module.jpms.ModuleInfo;

public class FilesystemPackageModuleMapperTest {

	@Test
	public void moduleListIsReadOnlyOnce()
	{
		String moduleName = "on.the.path";

		Path path = archive().configured(asJpmsModule()
				.named(moduleName))
				.to(Paths.get(".", "target", now(systemDefault()) + "_" + randomUUID() + ".jar"));

		final List<Path> paths = new ArrayList<>();
		paths.add(path);

		FilesystemPackageModuleMapper mapper = new FilesystemPackageModuleMapper() {

			@Override
			protected Stream<Path> paths()
			{
				return paths.stream();
			}
		};

		assertThat("pre-condition; modules loaded", mappedModuleNames(mapper), contains(moduleName));

		paths.clear();

		assertThat(mappedModuleNames(mapper), contains(moduleName));

	}


	private List<String> mappedModuleNames(FilesystemPackageModuleMapper mapper)
	{
		return mapper.modules().stream()
				.map(ModuleInfo::name)
				.collect(toList());
	}
}
