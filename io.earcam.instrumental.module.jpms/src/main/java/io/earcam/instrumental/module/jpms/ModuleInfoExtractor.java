/*-
 * #%L
 * io.earcam.instrumental.module.jpms
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
package io.earcam.instrumental.module.jpms;

import static io.earcam.instrumental.module.jpms.Constants.HEADER_AUTOMATIC_MODULE_NAME;
import static io.earcam.instrumental.module.jpms.Constants.META_INF_SERVICES;
import static io.earcam.instrumental.module.jpms.ModuleModifier.SYNTHETIC;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.annotation.WillClose;

import io.earcam.utilitarian.io.ExplodedJarInputStream;
import io.earcam.utilitarian.io.IoStreams;

final class ModuleInfoExtractor {

	private ModuleInfoExtractor()
	{}


	static Optional<ModuleInfo> extract(Path jar) throws IOException
	{
		return extract(ExplodedJarInputStream.jarInputStreamFrom(jar));
	}


	static Optional<ModuleInfo> extract(@WillClose JarInputStream jar) throws IOException
	{
		try(JarInputStream jin = jar) {

			String autoName = null;
			boolean hasAutoName = false;
			Manifest manifest = jin.getManifest();
			if(manifest != null) {
				autoName = manifest.getMainAttributes().getValue(HEADER_AUTOMATIC_MODULE_NAME);
				hasAutoName = (autoName != null);
			}
			ModuleInfoBuilder builder = ModuleInfo.moduleInfo()
					.withAccess(SYNTHETIC.access());
			JarEntry entry;
			while((entry = jin.getNextJarEntry()) != null) {
				if(!entry.isDirectory()) {
					if("module-info.class".equals(entry.getName())) {
						return Optional.of(ModuleInfo.read(IoStreams.readAllBytes(jin)));
					}
					if(hasAutoName) {
						processEntryForAutomaticModule(builder, jin, entry);
					}
				}
			}
			return (hasAutoName) ? Optional.of(builder.named(autoName).construct()) : Optional.empty();
		}
	}


	private static void processEntryForAutomaticModule(ModuleInfoBuilder builder, JarInputStream jin, JarEntry entry)
	{
		if(entry.getName().endsWith(".class")) {
			int end = entry.getName().lastIndexOf('/');
			if(end != -1) {
				builder.exporting(entry.getName().substring(0, end).replace('/', '.'));
			}
		} else if(entry.getName().startsWith(META_INF_SERVICES)) {
			String service = entry.getName().substring(META_INF_SERVICES.length());
			String[] implementations = new String(IoStreams.readAllBytes(jin), UTF_8).split("\r?\n");
			builder.providing(service, implementations);
		}
	}
}
