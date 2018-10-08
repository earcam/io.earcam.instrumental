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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import io.earcam.instrumental.archive.jpms.PackageModuleMapper;
import io.earcam.instrumental.module.jpms.Export;
import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.utilitarian.charstar.CharSequences;

/**
 * <p>
 * An abstract base for PackageModuleMapper.
 * </p>
 */
public abstract class AbstractPackageModuleMapper implements PackageModuleMapper {

	/**
	 * <p>
	 * The modules observed by this {@link PackageModuleMapper}.
	 * </p>
	 *
	 * @return the list of {@link ModuleInfo}s resolved by the package-module-mapper
	 */
	protected abstract List<ModuleInfo> modules();


	@Override
	public Set<ModuleInfo> moduleRequiredFor(CharSequence moduleName, Iterator<? extends CharSequence> requiredPackages)
	{
		return mapAvailableModules(moduleName, requiredPackages, ModuleInfo::exports);
	}


	Set<ModuleInfo> mapAvailableModules(CharSequence moduleName, Iterator<? extends CharSequence> requiredPackages,
			Function<ModuleInfo, Set<Export>> exportMode)
	{
		Set<ModuleInfo> mods = new HashSet<>();
		while(requiredPackages.hasNext()) {
			mapAvailableModules(moduleName, exportMode, requiredPackages, mods);
		}
		return mods;
	}


	void mapAvailableModules(CharSequence moduleName, Function<ModuleInfo, Set<Export>> exportMode, Iterator<? extends CharSequence> packages,
			Set<ModuleInfo> modules)
	{
		CharSequence paquet = packages.next();
		modules().stream().filter(m -> exportMode.apply(m).stream()
				.filter(x -> x.modules().length == 0 || Arrays.asList(x.modules()).contains(moduleName.toString()))
				.map(Export::paquet)
				.anyMatch(p -> CharSequences.same(p, paquet)))
				.findFirst().ifPresent(m -> {
					modules.add(m);
					packages.remove();
				});
	}


	@Override
	public Set<ModuleInfo> moduleOpenedFor(CharSequence moduleName, Iterator<? extends CharSequence> requiredPackages)
	{
		return mapAvailableModules(moduleName, requiredPackages, ModuleInfo::opens);
	}


	/**
	 * <p>
	 * Extracts ModuleInfo for the given JAR.
	 * </p>
	 * <ul>
	 * <li>If the {@literal #HEADER_AUTOMATIC_MODULE_NAME} header
	 * is encountered, then a {@link SYNTHETIC} module is generated exporting all packages</li>
	 * <li>Header values {@code Add-Exports} and {@code Add-Opens} are ignored (<i>though methods exposed to
	 * subclasses</i>)</li>
	 * </ul>
	 *
	 * @param path a {@link Path} to a JAR (zip file or exploded directory).
	 * @return a {@link ModuleInfo} instance, possibly {@link SYNTHETIC},
	 * or {@code null} if no module-info can be derived.
	 * 
	 * @throws java.io.IOException if thrown by the underlying.
	 */
	protected ModuleInfo moduleInfoFrom(Path path) throws IOException
	{
		return ModuleInfo.extract(path).orElse(null);
	}
}
