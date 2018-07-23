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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.annotation.WillNotClose;

import io.earcam.instrumental.archive.jpms.PackageModuleMapper;
import io.earcam.instrumental.module.jpms.Export;
import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.instrumental.module.jpms.ModuleInfoBuilder;
import io.earcam.utilitarian.charstar.CharSequences;
import io.earcam.utilitarian.io.ExplodedJarInputStream;
import io.earcam.utilitarian.io.IoStreams;

/**
 * <p>
 * An abstract base for PackageModuleMapper.
 * </p>
 */
public abstract class AbstractPackageModuleMapper implements PackageModuleMapper {

	static final Name HEADER_AUTOMATIC_MODULE_NAME = new Name("Automatic-Module-Name");


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
	 * @return a {@link ModuleInfo} instance, possibly {@link SYNTHETIC}.
	 * @throws java.io.IOException if thrown by the underlying.
	 */
	protected ModuleInfo moduleInfoFrom(Path path) throws IOException
	{
		try(JarInputStream jin = ExplodedJarInputStream.jarInputStreamFrom(path)) {
			return moduleInfoFrom(jin);
		}
	}


	/**
	 * @param jin the JAR input stream.
	 * @return a {@link ModuleInfo} instance, possibly {@link SYNTHETIC}.
	 * @throws java.io.IOException if thrown by the underlying.
	 * 
	 * @see #moduleInfoFrom(Path)
	 */
	protected ModuleInfo moduleInfoFrom(@WillNotClose JarInputStream jin) throws IOException
	{
		Set<String> exports = new HashSet<>();

		String autoName = null;
		boolean hasAutoName = false;
		Manifest manifest = jin.getManifest();
		if(manifest != null) {
			autoName = manifest.getMainAttributes().getValue(HEADER_AUTOMATIC_MODULE_NAME);
			hasAutoName = (autoName != null);
		}
		JarEntry entry;
		while((entry = jin.getNextJarEntry()) != null) {
			String name = entry.getName();
			if(!entry.isDirectory() && "module-info.class".equals(name)) {
				return ModuleInfo.read(IoStreams.readAllBytes(jin));
			}
			if(hasAutoName && !entry.isDirectory() && name.endsWith(".class")) {
				int end = name.lastIndexOf('/');
				if(end != -1) {
					exports.add(name.substring(0, end).replace('/', '.'));
				}
			}
		}

		if(hasAutoName) {
			ModuleInfoBuilder builder = moduleInfo()
					.withAccess(SYNTHETIC.access())
					.named(autoName);
			exports.forEach(builder::exporting);
			return builder.construct();
		}
		return null;
	}
}
