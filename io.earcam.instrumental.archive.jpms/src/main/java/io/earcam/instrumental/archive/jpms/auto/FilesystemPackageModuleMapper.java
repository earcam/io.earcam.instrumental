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

import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.unexceptional.Exceptional;

/**
 * Provides a PackageModuleMapper for subclasses simply implementing {@link #paths()}
 * 
 */
public abstract class FilesystemPackageModuleMapper extends AbstractPackageModuleMapper {

	private List<ModuleInfo> modules;


	@Override
	protected final List<ModuleInfo> modules()
	{
		loadModules();
		return modules;
	}


	protected abstract Stream<Path> paths();


	protected final synchronized void loadModules()
	{
		if(modules == null) {
			modules = paths()
					.map(Exceptional.uncheckFunction(this::moduleInfoFrom))
					.filter(Objects::nonNull)
					.collect(toList());
		}
	}
}
