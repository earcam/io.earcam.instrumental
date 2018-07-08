/*-
 * #%L
 * io.earcam.instrumental.archive.glue
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
package io.earcam.instrumental.archive.glue;

import static io.earcam.instrumental.archive.AsJar.asJar;
import static io.earcam.instrumental.archive.glue.ArchiveFileObjectProvider.fromArchive;
import static io.earcam.instrumental.compile.Compiler.compiling;
import static io.earcam.instrumental.compile.SourceSource.from;
import static io.earcam.instrumental.compile.glue.CompileToArchive.toArchive;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.Archive;

public class ArchiveFileObjectProviderTest {

	@Test
	public void dependencyComesFromArchive()
	{
		// @formatter:off
		// EARCAM_SNIPPET_BEGIN: with-dependency
		
		Archive api, imp;
		
		api = compiling()
				.versionAt(RELEASE_8)
				.source(
					from(
						"package com.acme.api;                          \n" +

						"public interface Greet {                       \n" +

						"   public abstract String greeting(String to); \n" +

						"}\n")
				)
				.compile(toArchive())
				.configured(asJar())
				.toObjectModel();

		imp = compiling()
				.versionAt(RELEASE_8)
				.withDependencies(fromArchive(api))
				.source(
					from(
						"package com.acme.imp;                          \n" +

						"import com.acme.api.Greet;                     \n" +

						"public class HelloService implements Greet {   \n" +

						"   @Override                                   \n" +
						"   public String greeting(String to)           \n" +
						"   {                                           \n" +
						"      return \"Hello \" + to;                  \n" +
						"   }                                           \n" +

						"}\n")
				)
				.compile(toArchive())
				.configured(asJar())
				.toObjectModel();

		assertThat(imp.content("com/acme/imp/HelloService.class").isPresent(), is(true));

		// EARCAM_SNIPPET_END: with-dependency

		// @formatter:on
	}
}
