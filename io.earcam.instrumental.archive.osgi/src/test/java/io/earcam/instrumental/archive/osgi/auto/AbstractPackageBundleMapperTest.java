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

import static io.earcam.instrumental.module.osgi.BundleInfoBuilder.bundle;
import static io.earcam.instrumental.module.osgi.Clause.clause;
import static io.earcam.instrumental.module.osgi.ClauseParameters.ClauseParameter.attribute;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.module.osgi.BundleInfo;
import io.earcam.instrumental.module.osgi.Clause;
import io.earcam.instrumental.module.osgi.ClauseParameters;

public class AbstractPackageBundleMapperTest {

	private static final String EXPORTED = "com.acme.api";
	private static final ClauseParameters VERSION = attribute("version", "1.2.3");

	private AbstractPackageBundleMapper mapper = new AbstractPackageBundleMapper() {

		@Override
		protected List<BundleInfo> bundles()
		{
			return singletonList(
					bundle().exportPackages(EXPORTED, VERSION).construct());
		}
	};


	@Test
	void importFoundAndRemoved()
	{
		List<String> required = new ArrayList<>(Arrays.asList(EXPORTED));

		List<Clause> importPackage = mapper.importsFor(required.iterator());

		assertThat(importPackage, contains(clause(EXPORTED, VERSION)));
		assertThat(required, is(empty()));
	}


	@Test
	void standardJavaImportsImplicitlyRemoved()
	{
		List<String> required = new ArrayList<>(Arrays.asList(EXPORTED, "java.lang", "java.util.function"));

		List<Clause> importPackage = mapper.importsFor(required.iterator());

		assertThat(importPackage, contains(clause(EXPORTED, VERSION)));
		assertThat(required, is(empty()));
	}


	@Test
	void whenImportNotFoundThenNotRemoved()
	{
		String notFound = "com.acme.something.funky";
		List<String> required = new ArrayList<>(Arrays.asList(notFound));

		List<Clause> importPackage = mapper.importsFor(required.iterator());

		assertThat(importPackage, is(empty()));
		assertThat(required, contains(notFound));
	}
}
