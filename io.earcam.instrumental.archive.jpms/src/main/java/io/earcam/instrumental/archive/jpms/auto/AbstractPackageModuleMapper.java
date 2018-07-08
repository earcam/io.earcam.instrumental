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
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.annotation.WillNotClose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.earcam.instrumental.archive.jpms.PackageModuleMapper;
import io.earcam.instrumental.module.jpms.Export;
import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.instrumental.module.jpms.ModuleInfoBuilder;
import io.earcam.utilitarian.charstar.CharSequences;
import io.earcam.utilitarian.io.ExplodedJarInputStream;
import io.earcam.utilitarian.io.IoStreams;

/**
 * <p>
 * Abstract AbstractPackageModuleMapper class.
 * </p>
 *
 */
public abstract class AbstractPackageModuleMapper implements PackageModuleMapper {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractPackageModuleMapper.class);

	static final String HEADER_AUTOMATIC_MODULE_NAME = "Automatic-Module-Name";
	static final String HEADER_ADDS_EXPORTS = "Add-Exports";
	static final String HEADER_ADDS_OPENS = "Add-Opens";


	/**
	 * <p>
	 * modules.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	protected abstract List<ModuleInfo> modules();


	/** {@inheritDoc} */
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
					LOG.debug("Found module {} for package {}, required by module {}",
							m.name(), paquet, moduleName);
				});
	}


	/** {@inheritDoc} */
	@Override
	public Set<ModuleInfo> moduleOpenedFor(CharSequence moduleName, Iterator<? extends CharSequence> requiredPackages)
	{
		return mapAvailableModules(moduleName, requiredPackages, ModuleInfo::opens);
	}


	// TODO useful to expose independently
	/**
	 * <p>
	 * moduleInfoFrom.
	 * </p>
	 *
	 * @param path a {@link java.nio.file.Path} object.
	 * @return an array of {@link byte} objects.
	 * @throws java.io.IOException if any.
	 */
	protected static byte[] moduleInfoFrom(Path path) throws IOException
	{
		try(JarInputStream jin = ExplodedJarInputStream.jarInputStreamFrom(path)) {
			return moduleInfoFrom(jin);
		}
	}


	protected static byte[] moduleInfoFrom(@WillNotClose JarInputStream jin) throws IOException
	{
		Set<String> packages = new HashSet<>();
		String autoName = null;
		Manifest manifest = jin.getManifest();
		if(manifest != null) {
			autoName = manifest.getMainAttributes().getValue(HEADER_AUTOMATIC_MODULE_NAME);
		}
		JarEntry entry;
		while((entry = jin.getNextJarEntry()) != null) {
			String name = entry.getName();
			if(!entry.isDirectory() && "module-info.class".equals(name)) {
				return IoStreams.readAllBytes(jin);
			}
			if(!entry.isDirectory() && name.endsWith(".class")) {
				int end = name.lastIndexOf('/');
				if(end != -1) {
					packages.add(name.substring(0, end).replace('/', '.'));
				}
			}
		}
		if(autoName != null) {
			// TODO
			// additional manifest headers ... parse `Add-Exports` and `Add-Opens`
			// auto-requires .... not important ATM, as we're only looking at immediate dependencies, not transitive/a graph
			//
			ModuleInfoBuilder builder = moduleInfo()
					.withAccess(SYNTHETIC.access())
					.named(autoName);
			packages.forEach(builder::exporting);
			return builder.construct().toBytecode();
		}
		return new byte[0];
	}
}
