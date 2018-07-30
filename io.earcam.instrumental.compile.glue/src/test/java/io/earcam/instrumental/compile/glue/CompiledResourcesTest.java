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

import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.FINAL;
import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.INITIAL;
import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.PRE_MANIFEST;
import static io.earcam.instrumental.compile.Compiler.compiling;
import static io.earcam.instrumental.compile.SourceSource.from;
import static io.earcam.instrumental.compile.glue.CompiledResources.toResources;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.compile.FileObjectProvider.CustomJavaFileObject;

public class CompiledResourcesTest {

	@Nested
	public class CanBeUsedAsDependencies {

		@Test
		public void whenNamespaced()
		{
			//@formatter:off
			// EARCAM_SNIPPET_BEGIN: compiled-resources-dependency
			
			CompiledResources resources;
			
			resources = compiling().versionAt(RELEASE_8)
				.source(from(
					"package com.acme;                          \n" + 
					"                                           \n" + 
					"public interface Api {                     \n" + 
					"                                           \n" + 
					"	public String greet(String recipient);  \n" + 
					"                                           \n" + 
					"}                                          \n"
				))
				.compile(toResources());

			CompiledResources dependent;
			
			dependent = compiling().versionAt(RELEASE_8)
				.withDependencies(resources)
				.source(from(
					"package com.acme;                     \n" + 
					"                                      \n" + 
					"public class Imp implements Api {     \n" + 
					"                                      \n" + 
					"	@Override                          \n" + 
					"	public String greet(String recip)  \n" + 
					"	{                                  \n" + 
					"		return \"Hi \" + recip;        \n" + 
					"	}                                  \n" + 
					"}                                     \n"
				))
				.compile(toResources());
			
			List<ArchiveResource> forArchive = dependent.drain(INITIAL).collect(toList());
			
			assertThat(forArchive, hasSize(1));
			
			assertThat(forArchive.get(0).name(), is(equalTo("com/acme/Imp.class")));
			
			// EARCAM_SNIPPET_END: compiled-resources-dependency
			//@formatter:on
		}


		@Test
		public void whenNotNamespaced()
		{
			//@formatter:off
			CompiledResources resources;
			
			resources = compiling().versionAt(RELEASE_8)
				.source(from(
					"public interface Api {                     \n" + 
					"                                           \n" + 
					"	public String greet(String recipient);  \n" + 
					"                                           \n" + 
					"}                                          \n"
				))
				.compile(toResources());

			CompiledResources dependent;
			
			dependent = compiling().versionAt(RELEASE_8)
				.withDependencies(resources)
				.source(from(
					"public class Imp implements Api {     \n" + 
					"                                      \n" + 
					"	@Override                          \n" + 
					"	public String greet(String recip)  \n" + 
					"	{                                  \n" + 
					"		return \"Hi \" + recip;        \n" + 
					"	}                                  \n" + 
					"}                                     \n"
				))
				.compile(toResources());
			
			
			List<ArchiveResource> forArchive = dependent.drain(INITIAL).collect(toList());
			
			assertThat(forArchive, hasSize(1));
			
			assertThat(forArchive.get(0).name(), is(equalTo("Imp.class")));
			
			//@formatter:on
		}


		@Test
		public void whenNamespacesDiffer()
		{
			//@formatter:off
			CompiledResources resources;
			
			resources = compiling().versionAt(RELEASE_8)
				.source(from(
						"package com.acme.api;                      \n" + 
						"                                           \n" + 
						"public interface Api {                     \n" + 
						"                                           \n" + 
						"	public String greet(String recipient);  \n" + 
						"                                           \n" + 
						"}                                          \n"
				))
				.source(from(
						"package com.acme.other;                    \n" + 
						"                                           \n" + 
						"public interface Other {                   \n" + 
						"}                                          \n"
				))
				.source(from(
						"package com.acme.another;                  \n" + 
						"                                           \n" + 
						"public interface Another {                 \n" + 
						"}                                          \n"
				))
				.compile(toResources());

			CompiledResources dependent;
			
			dependent = compiling().versionAt(RELEASE_8)
				.withDependencies(resources)
				.source(from(
					"package com.acme.imp;                 \n" + 
					"                                      \n" + 
					"import com.acme.api.*;                \n" + 
					"import com.acme.other.*;              \n" + 
					"import com.acme.another.*;            \n" + 
					"                                      \n" + 
					"public class Imp implements Api {     \n" + 
					"                                      \n" + 
					"	@Override                          \n" + 
					"	public String greet(String recip)  \n" + 
					"	{                                  \n" + 
					"		return \"Hi \" + recip;        \n" + 
					"	}                                  \n" + 
					"}                                     \n"
				))
				.compile(toResources());
			
			
			List<ArchiveResource> forArchive = dependent.drain(INITIAL).collect(toList());
			
			assertThat(forArchive, hasSize(1));
			
			assertThat(forArchive.get(0).name(), is(equalTo("com/acme/imp/Imp.class")));
			//@formatter:on
		}
	}

	@Nested
	public class Lists {
		private final CompiledResources resources = compiling()
				.versionAt(RELEASE_8)
				.source(from(
						"package com.acme.api;                      \n" +
								"                                           \n" +
								"public interface Api {                     \n" +
								"                                           \n" +
								"	public String greet(String recipient);  \n" +
								"                                           \n" +
								"}                                          \n"))
				.source(from(
						"package com.acme.other;                    \n" +
								"                                           \n" +
								"public interface Other {                   \n" +
								"}                                          \n"))
				.source(from(
						"package com.acme.other.another;            \n" +
								"                                           \n" +
								"public interface Another {                 \n" +
								"}                                          \n"))
				.compile(toResources());


		@Test
		public void platformClasspathIgnored() throws IOException
		{
			List<CustomJavaFileObject> list = resources.list(StandardLocation.PLATFORM_CLASS_PATH, "com.acme", EnumSet.allOf(Kind.class), true);

			assertThat(list, is(empty()));
		}


		@Test
		public void recursivelyLists() throws IOException
		{
			List<CustomJavaFileObject> list = resources.list(StandardLocation.CLASS_PATH, "com.acme", EnumSet.allOf(Kind.class), true);

			List<String> names = list.stream()
					.map(CustomJavaFileObject::getName)
					.collect(Collectors.toList());

			assertThat(names, containsInAnyOrder(
					"/com/acme/other/another/Another.class",
					"/com/acme/other/Other.class",
					"/com/acme/api/Api.class"));
		}


		@Test
		public void nonRecursivelyLists() throws IOException
		{
			List<CustomJavaFileObject> list = resources.list(StandardLocation.CLASS_PATH, "com.acme.other", EnumSet.allOf(Kind.class), false);

			List<String> names = list.stream()
					.map(CustomJavaFileObject::getName)
					.collect(Collectors.toList());

			assertThat(names, contains("/com/acme/other/Other.class"));
		}
	}


	@Test
	void onlyDrainsAtTheInitialStage()
	{
		CompiledResources resources;

		resources = compiling().versionAt(RELEASE_8)
				.source(from(
						"package com.acme;                          \n" +
								"                                           \n" +
								"public interface Api {                     \n" +
								"                                           \n" +
								"	public String greet(String recipient);  \n" +
								"                                           \n" +
								"}                                          \n"))
				.compile(toResources());

		assertThat(resources.drain(INITIAL).findAny().isPresent(), is(true));

		assertThat(resources.drain(PRE_MANIFEST).findAny().isPresent(), is(false));
		assertThat(resources.drain(FINAL).findAny().isPresent(), is(false));
	}


	@Test
	void aWrappedCollectionMayBeDrainedMoreThanOnce()
	{
		CompiledResources resources;

		resources = compiling().versionAt(RELEASE_8)
				.source(from(
						"package com.acme;                          \n" +
								"                                           \n" +
								"public interface Api {                     \n" +
								"                                           \n" +
								"	public String greet(String recipient);  \n" +
								"                                           \n" +
								"}                                          \n"))
				.compile(toResources());

		assertThat(resources.drain(INITIAL).findAny().isPresent(), is(true));
		assertThat(resources.drain(INITIAL).findAny().isPresent(), is(true));
		assertThat(resources.drain(INITIAL).findAny().isPresent(), is(true));
	}

}
