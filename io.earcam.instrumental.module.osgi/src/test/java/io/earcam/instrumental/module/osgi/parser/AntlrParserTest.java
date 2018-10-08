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
package io.earcam.instrumental.module.osgi.parser;

import static io.earcam.instrumental.module.osgi.Clause.sortedSet;
import static io.earcam.instrumental.module.osgi.ClauseParameters.EMPTY_PARAMETERS;
import static io.earcam.instrumental.module.osgi.ClauseParameters.ClauseParameter.attribute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.module.osgi.BundleInfo;
import io.earcam.instrumental.module.osgi.BundleInfoBuilder;
import io.earcam.instrumental.module.osgi.Clause;

public class AntlrParserTest {

	@Test
	void parseManifestString()
	{
		Clause expectedExports = new Clause(sortedSet("com.acme.foo", "com.acme.bar"), attribute("foo", "bar").attribute("version", "0.1.42"));
		Clause expectedImports = new Clause(sortedSet("com.acme.base"), EMPTY_PARAMETERS);
		String expectedSymbolicName = "com.acme.sym.nom";

		String manifest = "Manifest-Version: 1.0\n" +
				"Bundle-ManifestVersion: 2\n" +
				"Bundle-SymbolicName: com.acme.sym.nom\n" +
				"Export-Package: com.acme.foo;com.acme.bar;foo=bar;version=0.1.42\n" +
				"Import-Package: com.acme.base\n" +
				"Arbitrary-Header: some.value\n" +
				"\n";

		BundleInfoBuilder parsed = BundleInfoParser.parse(manifest);

		BundleInfo bundleInfo = parsed.construct();

		assertThat(bundleInfo.symbolicName().uniqueNames(), contains(expectedSymbolicName));
		assertThat(bundleInfo.exportPackage(), contains(expectedExports));
		assertThat(bundleInfo.importPackage(), contains(expectedImports));

		assertThat(bundleInfo.mainAttribute("Arbitrary-Header"), is(equalTo("some.value")));
	}


	@Test
	void parseBundleActivatorFromManifestString()
	{
		String manifest = "Manifest-Version: 1.0\n" +
				"Bundle-ManifestVersion: 2\n" +
				"Bundle-Activator: com.acme.foo.Activator\n" +
				"Bundle-SymbolicName: com.acme.sym.nom\n" +
				"\n";

		BundleInfoBuilder parsed = BundleInfoParser.parse(manifest);

		BundleInfo bundleInfo = parsed.construct();

		assertThat(bundleInfo.activator(), is(equalTo("com.acme.foo.Activator")));
	}
}
