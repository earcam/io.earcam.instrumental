/*-
 * #%L
 * io.earcam.instrumental.archive.osgi
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
package io.earcam.instrumental.archive.osgi.auto;

import static io.earcam.instrumental.module.auto.Classpaths.allClasspaths;
import static io.earcam.instrumental.module.osgi.BundleManifestHeaders.BUNDLE_SYMBOLICNAME;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import io.earcam.instrumental.module.osgi.BundleInfo;
import io.earcam.instrumental.module.osgi.BundleInfoBuilder;
import io.earcam.unexceptional.Exceptional;
import io.earcam.utilitarian.io.ExplodedJarInputStream;

/**
 * <p>
 * ClasspathBundles class.
 * </p>
 *
 */
public class ClasspathBundles extends AbstractPackageBundleMapper {

	/** {@inheritDoc} */
	@Override
	protected List<BundleInfo> bundles()
	{
		return allClasspaths()
				.map(Exceptional.uncheckFunction(ClasspathBundles::bundleInfoFrom))
				.filter(Objects::nonNull)
				.collect(toList());
	}


	private static BundleInfo bundleInfoFrom(Path classpathElement) throws IOException
	{
		try(JarInputStream jin = ExplodedJarInputStream.jarInputStreamFrom(classpathElement)) {
			Manifest manifest = jin.getManifest();
			if(manifest == null || manifest.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME.header()) == null) {
				return null;
			}
			return BundleInfoBuilder.bundleFrom(manifest).construct();
		}
	}
}
