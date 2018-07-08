/*-
 * #%L
 * io.earcam.instrumental.fluency
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
package io.earcam.instrumental.fluency;

import static io.earcam.instrumental.compile.CompilationTarget.toFileSystem;
import static io.earcam.instrumental.compile.Compiler.compiling;
import static io.earcam.instrumental.compile.SourceSource.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.fluent.Fluent;

public class FluencyProcessorTest {

	// tests with/out the "-parameters" argument

	@Test
	void exploratory()
	{
		Path baseDir = Paths.get("target", FluencyProcessorTest.class.getCanonicalName(), UUID.randomUUID().toString());
		Path generatedSourceDirectory = baseDir.resolve("source");
		Path generatedBinaryDirectory = baseDir.resolve("binary");

		FluencyProcessor processor = new FluencyProcessor();

		String annotation = Fluent.class.getCanonicalName();

		//@formatter:off
		compiling()
			.sources(from(
					"package com.acme;                      \n" +
			
					"public class Foo {                     \n" +
					
					"   @" + annotation +                  "\n" +
					"   public static String bar()          \n" +
					"   {                                   \n" +
					"       return \"bingbong\";            \n" +
					"   }                                   \n" +

					"   /**                                 \n" + 
					"    * Hum just multiplies the          \n" + 
					"    * argument {@code bug} by 2        \n" + 
					"    * @param bug the multiplicand      \n" + 
					"    * @return {@code bug} &times; 2    \n" + 
					"    */                                 \n" + 
					"   @" + annotation +                  "\n" +
					"   public static int hum(int bug)      \n" +
					"   {                                   \n" +
					"       return bug * 2;                 \n" +
					"   }                                   \n" +
					
					"}\n"))
			.processedBy(processor)
			.withProcessorOption("name", "TaDaApi")
			.compile(toFileSystem(generatedBinaryDirectory).generatingSourcesIn(generatedSourceDirectory));
		//@formatter:on

		assertThat(generatedSourceDirectory.resolve(Paths.get("com", "acme", "TaDaApi.java")).toFile(), is(anExistingFile()));
		assertThat(generatedBinaryDirectory.resolve(Paths.get("com", "acme", "TaDaApi.class")).toFile(), is(anExistingFile()));
		assertThat(generatedBinaryDirectory.resolve(Paths.get("com", "acme", "Foo.class")).toFile(), is(anExistingFile()));
	}
}
