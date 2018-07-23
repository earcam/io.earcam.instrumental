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

import static io.earcam.instrumental.module.jpms.ModuleInfo.moduleInfo;
import static io.earcam.instrumental.module.jpms.ModuleModifier.SYNTHETIC;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.jar.Manifest;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.instrumental.module.jpms.ModuleInfoBuilder;

public final class ArchivePackageModuleMapper extends AbstractPackageModuleMapper {

	private final List<ModuleInfo> modules;


	private ArchivePackageModuleMapper(List<ModuleInfo> modules)
	{
		this.modules = Collections.unmodifiableList(modules);
	}


	public static ArchivePackageModuleMapper fromArchives(Archive... archives)
	{
		return fromArchives(Arrays.asList(archives));
	}


	public static ArchivePackageModuleMapper fromArchives(List<Archive> archives)
	{
		List<ModuleInfo> modules = archives.stream()
				.map(ArchivePackageModuleMapper::extractModuleInfo)
				.collect(toList());

		return new ArchivePackageModuleMapper(modules);
	}


	private static ModuleInfo extractModuleInfo(Archive archive)
	{

		return archive.content("module-info.class")
				.map(ArchiveResource::bytes)
				.map(ModuleInfo::read)
				.orElseGet((Supplier<ModuleInfo>) () -> archive.manifest()
						.map(ArchivePackageModuleMapper::manifestToModuleInfo)
						.map(m -> syntheticModuleInfoWithExports(m, archive))
						.map(ModuleInfoBuilder::construct)
						.orElse((ModuleInfo) null));
	}


	private static ModuleInfoBuilder manifestToModuleInfo(Manifest manifest)
	{
		String autoName = manifest.getMainAttributes().getValue(HEADER_AUTOMATIC_MODULE_NAME);

		return (autoName == null) ? null
				: moduleInfo()
						.withAccess(SYNTHETIC.access())
						.named(autoName);
	}


	private static ModuleInfoBuilder syntheticModuleInfoWithExports(ModuleInfoBuilder builder, Archive archive)
	{
		archive.contents().stream()
				.filter(ArchiveResource::isQualifiedClass)
				.map(ArchiveResource::pkg)
				.forEach(builder::exporting);
		return builder;
	}


	@Override
	protected List<ModuleInfo> modules()
	{
		return modules;
	}
}
