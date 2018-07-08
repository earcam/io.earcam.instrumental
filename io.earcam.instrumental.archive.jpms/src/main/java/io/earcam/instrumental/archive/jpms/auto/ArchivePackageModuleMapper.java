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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.module.jpms.ModuleInfo;

public final class ArchivePackageModuleMapper extends AbstractPackageModuleMapper {

	private final List<ModuleInfo> modules;


	private ArchivePackageModuleMapper(List<ModuleInfo> modules)
	{
		this.modules = Collections.unmodifiableList(modules);
	}


	public static ArchivePackageModuleMapper byMappingModuleArchives(Archive... archives)
	{
		return fromArchives(Arrays.asList(archives));
	}


	// TODO this doesn't take into account Auto-Modules @see AbstractPackageModuleMapper#moduleInfoFrom(JarInputStream)
	public static ArchivePackageModuleMapper fromArchives(List<Archive> archives)
	{
		List<ModuleInfo> modules = archives.stream()
				.map(a -> a.content("module-info.class"))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(ArchiveResource::bytes)
				.map(ModuleInfo::read)
				.collect(toList());

		return new ArchivePackageModuleMapper(modules);
	}


	@Override
	protected List<ModuleInfo> modules()
	{
		return modules;
	}
}
