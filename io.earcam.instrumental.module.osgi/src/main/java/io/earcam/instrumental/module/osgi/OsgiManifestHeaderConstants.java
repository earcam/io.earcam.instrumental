/*-
 * #%L
 * io.earcam.instrumental.module.osgi
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
package io.earcam.instrumental.module.osgi;

enum OsgiManifestHeaderConstants {

	// @formatter:off
	EXPORT_PACKAGE("Export-Package"),
	BUNDLE_MANIFESTVERSION("Bundle-ManifestVersion"),
	IMPORT_PACKAGE("Import-Package"),
	DYNAMICIMPORT_PACKAGE("DynamicImport-Package"),
	BUNDLE_VERSION("Bundle-Version"),
	BUNDLE_ACTIVATOR("Bundle-Activator"),
	BUNDLE_SYMBOLICNAME("Bundle-SymbolicName"),
	FRAGMENT_HOST("Fragment-Host");
	// @formatter:on

	final String value;


	private OsgiManifestHeaderConstants(String value)
	{
		this.value = value;
	}
}
