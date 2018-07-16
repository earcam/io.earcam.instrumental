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

import static io.earcam.instrumental.module.osgi.Clause.sortedSet;
import static io.earcam.instrumental.module.osgi.ClauseParameters.EMPTY_PARAMETERS;
import static io.earcam.instrumental.module.osgi.ClauseParameters.ClauseParameter.attribute;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Test;

public class BundleInfoBuilderTest {

	@Test
	void bundleFromManifest() throws IOException
	{
		Clause expectedExports = new Clause(sortedSet("com.acme.foo", "com.acme.bar"), attribute("foo", "bar").attribute("version", "0.1.42"));
		Clause expectedImports = new Clause(sortedSet("com.acme.base"), EMPTY_PARAMETERS);
		String expectedSymbolicName = "com.acme.sym.nom";

		// EARCAM_SNIPPET_BEGIN: from-manifest
		String manifest = "Manifest-Version: 1.0\n" +
				"Bundle-ManifestVersion: 2\n" +
				"Bundle-SymbolicName: com.acme.sym.nom\n" +
				"Fragment-Host: com.acme.joine.me;version=0.1.10\n" +
				"Export-Package: com.acme.foo;com.acme.bar;foo=bar;version=0.1.42\n" +
				"Import-Package: com.acme.base\n" +
				"DynamicImport-Package: com.acme.ya.never.know.*, *\n" +
				"Arbitrary-Header: some.value\n" +
				"\n";

		Manifest manifestation = new Manifest(new ByteArrayInputStream(manifest.getBytes(UTF_8)));

		BundleInfoBuilder parsed = BundleInfoBuilder.bundleFrom(manifestation);

		BundleInfo bundleInfo = parsed.construct();

		assertThat(bundleInfo.symbolicName().uniqueNames(), contains(expectedSymbolicName));
		assertThat(bundleInfo.fragmentHost().uniqueNames(), contains("com.acme.joine.me"));
		assertThat(bundleInfo.exportPackage(), contains(expectedExports));
		assertThat(bundleInfo.importPackage(), contains(expectedImports));
		// ...
		// EARCAM_SNIPPET_END: from-manifest

		Set<String> dynamicImports = bundleInfo.dynamicImportPackage().stream()
				.map(Clause::uniqueNames)
				.flatMap(Set::stream)
				.collect(toSet());
		assertThat(dynamicImports, containsInAnyOrder("com.acme.ya.never.know.*", "*"));

		assertThat(bundleInfo.mainAttribute("Arbitrary-Header"), is(equalTo("some.value")));
	}


	@Test
	void bundleFromManifestOfUtilitarianCharstar() throws IOException
	{
		String manifest = "Manifest-Version: 1.0\n" +
				"Bundle-Description: java.lang.CharSequence is woefully under-used and \n" +
				" under-supported IMO... Tiny module named \"charstar\" in homage to C's \n" +
				" char*\n" +
				"Automatic-Module-Name: io.earcam.utilitarian.charstar\n" +
				"Bundle-License: http://www.apache.org/licenses/LICENSE-2.0.txt, https:\n" +
				" //opensource.org/licenses/BSD-3-Clause, https://www.eclipse.org/legal\n" +
				" /epl-v10.html, https://opensource.org/licenses/MIT\n" +
				"Bundle-SymbolicName: io.earcam.utilitarian.charstar\n" +
				"Adds-Exports: io.earcam.utilitarian.charstar\n" +
				"Bundle-Category: io.earcam.utilitarian\n" +
				"Built-By: caspar\n" +
				"Bnd-LastModified: 1531733362702\n" +
				"Bundle-ManifestVersion: 2\n" +
				"Bundle-DocURL: https://utilitarian.earcam.io/charstar\n" +
				"Bundle-Vendor: Caspar J. MacRae - earcam\n" +
				"Import-Package: javax.annotation;version=\"[3.0,4)\"\n" +
				"Require-Capability: osgi.ee;filter:=\"(&(osgi.ee=JavaSE)(version=1.8))\"\n" +
				"Bundle-ContactAddress: https://earcam.io\n" +
				"Tool: Bnd-3.5.0.201709291849\n" +
				"Export-Package: io.earcam.utilitarian.charstar;uses:=\"javax.annotation\n" +
				" \";version=\"1.2.0.SNAPSHOT\"\n" +
				"Bundle-Name: io.earcam.utilitarian.charstar\n" +
				"Bundle-Version: 1.2.0.SNAPSHOT\n" +
				"Build-Jdk: 1.8.0_171\n" +
				"Created-By: Apache Maven Bundle Plugin\n" +
				"\n";

		Manifest manifestation = new Manifest(new ByteArrayInputStream(manifest.getBytes(UTF_8)));

		BundleInfoBuilder parsed = BundleInfoBuilder.bundleFrom(manifestation);

		BundleInfo bundleInfo = parsed.construct();

		assertThat(bundleInfo.mainAttribute("Bundle-Description"), hasToString(containsString("*")));
	}
}
