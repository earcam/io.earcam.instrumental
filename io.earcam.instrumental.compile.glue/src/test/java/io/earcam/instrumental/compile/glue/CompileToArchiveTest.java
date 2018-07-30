/*-
 * #%L
 * io.earcam.instrumental.compile.glue
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
package io.earcam.instrumental.compile.glue;

import static io.earcam.instrumental.compile.SourceSource.from;
import static io.earcam.instrumental.compile.glue.CompileToArchive.*;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.compile.Compiler;
import io.earcam.instrumental.lade.ClassLoaders;

public class CompileToArchiveTest {

	private Compiler compiler = Compiler.compiling();


	@Test
	public void compilesFromStringSourceToArchive()
	{
		//@formatter:off
		ArchiveResource compiled = 
		// EARCAM_SNIPPET_BEGIN: to-archive
		compiler.versionAt(RELEASE_8)
			.source(from(
				"package com.acme;                      \n" +

				"public class FooBar {                  \n" +

				"   private String field;               \n" +

				"   public String get()                 \n" +
				"   {                                   \n" +
				"      return field;                    \n" +
				"   }                                   \n" +

				"   public void set(String field)       \n" +
				"   {                                   \n" +
				"      this.field = field;              \n" +
				"   }                                   \n" +

				"}                                      \n")
			)
			.compile(toArchive())
			// EARCAM_SNIPPET_END: to-archive
			.toObjectModel()
			.content("/com/acme/FooBar.class")
			.orElseThrow(NullPointerException::new);
		//@formatter:on
		Class<?> loaded = ClassLoaders.load(compiled.bytes());

		assertThat(loaded.getCanonicalName(), is(equalTo("com.acme.FooBar")));
	}


	@Test
	public void contentCompiledFromStringSource()
	{
		//@formatter:off
		ArchiveResource compiled =
		// EARCAM_SNIPPET_BEGIN: compiled-from
			Archive.archive()
				.sourcing(contentCompiledFrom(
					compiler.versionAt(RELEASE_8)
					.source(from(
					"package com.acme;                 \n" +
	
					"public class FooBar {             \n" +
	
					"   private String field;          \n" +
	
					"   public String get()            \n" +
					"   {                              \n" +
					"      return field;               \n" +
					"   }                              \n" +
	
					"   public void set(String field)  \n" +
					"   {                              \n" +
					"      this.field = field;         \n" +
					"   }                              \n" +
	
					"}                                 \n"))
				))
			// EARCAM_SNIPPET_END: compiled-from
			.toObjectModel()
			.content("/com/acme/FooBar.class")
			.orElseThrow(NullPointerException::new);
		//@formatter:on
		Class<?> loaded = ClassLoaders.load(compiled.bytes());

		assertThat(loaded.getCanonicalName(), is(equalTo("com.acme.FooBar")));
	}
}
