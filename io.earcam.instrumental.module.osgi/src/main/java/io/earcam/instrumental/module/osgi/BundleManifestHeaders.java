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

import java.util.jar.Attributes.Name;

// see spec and https://www.osgi.org/bundle-headers-reference/
/**
 * <p>
 * BundleManifestHeaders class.
 * </p>
 *
 */
public enum BundleManifestHeaders {

	//@formatter:off
                        BUNDLE_CATEGORY("Bundle-Category"),   //csv
	                   BUNDLE_CLASSPATH("Bundle-ClassPath"),
	                   BUNDLE_COPYRIGHT("Bundle-Copyright"),
                     BUNDLE_DESCRIPTION("Bundle-Description"),
	                        BUNDLE_NAME("Bundle-Name"),
	                  BUNDLE_NATIVECODE("Bundle-NativeCode"),
	                     EXPORT_PACKAGE("Export-Package"),
	                     IMPORT_PACKAGE("Import-Package"),
	              DYNAMICIMPORT_PACKAGE("DynamicImport-Package"),
	                      BUNDLE_VENDOR("Bundle-Vendor"),
	                     BUNDLE_VERSION("Bundle-Version"),
	                      BUNDLE_DOCURL("Bundle-DocURL"),
	              BUNDLE_CONTACTADDRESS("Bundle-ContactAddress"),
	                   BUNDLE_ACTIVATOR("Bundle-Activator"),
	         EXTENSION_BUNDLE_ACTIVATOR("ExtensionBundle-Activator"),
	              BUNDLE_UPDATELOCATION("Bundle-UpdateLocation"),
	BUNDLE_REQUIREDEXECUTIONENVIRONMENT("Bundle-RequiredExecutionEnvironment"),
	                BUNDLE_SYMBOLICNAME("Bundle-SymbolicName"),
	                BUNDLE_LOCALIZATION("Bundle-Localization"),
	                     REQUIRE_BUNDLE("Require-Bundle"),
	                      FRAGMENT_HOST("Fragment-Host"),
	             BUNDLE_MANIFESTVERSION("Bundle-ManifestVersion"),
	            BUNDLE_ACTIVATIONPOLICY("Bundle-ActivationPolicy"),
	                 PROVIDE_CAPABILITY("Provide-Capability"),
	                 REQUIRE_CAPABILITY("Require-Capability"),
	                        BUNDLE_ICON("Bundle-Icon"),
	                     BUNDLE_LICENSE("Bundle-License");
	//@formatter:on

	private Name header;


	BundleManifestHeaders(String header)
	{
		this.header = new Name(header);
	}


	/**
	 * <p>
	 * header.
	 * </p>
	 *
	 * @return a {@link java.util.jar.Attributes.Name} object.
	 */
	public Name header()
	{
		return header;
	}
}
