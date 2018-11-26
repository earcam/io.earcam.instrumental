/*-
 * #%L
 * io.earcam.instrumental.module.auto
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
package io.earcam.instrumental.module.auto;

import static io.earcam.instrumental.module.auto.Reader.reader;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.jar.Manifest;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsAnything;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.signature.SignatureReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.earcam.acme.AcmeAnnotation;
import io.earcam.acme.Annotated;
import io.earcam.acme.Generics;
import io.earcam.acme.ImportsSlf4jApi;
import io.earcam.instrumental.module.auto.Reader;
import io.earcam.instrumental.reflect.Resources;
import io.earcam.utilitarian.charstar.CharSequences;

@SuppressWarnings("squid:S2187")  // SonarQube false-positive; tests are only in @Nested
public class ReaderTest {

	// EARCAM_SNIPPET_BEGIN: test-support
	private static String cn(Class<?> type)
	{
		return type.getCanonicalName();
	}


	private static String pkg(Class<?> type)
	{
		return type.getPackage().getName();
	}
	// EARCAM_SNIPPET_END: test-support

	@Nested
	class Annotations {

		@Test
		void readsAnnotationsAndTheirValuesByDefault()
		{
			// @formatter:off
			// EARCAM_SNIPPET_BEGIN: do-read-annotations
			Map<String, Set<String>> imports = new HashMap<>();

			reader()
					.addImportListener(imports::put)
					.processClass(Resources.classAsBytes(Annotated.class));

			assertThat(imports, is(aMapWithSize(1)));

			assertThat(imports, hasEntry(equalTo(cn(Annotated.class)), 
				containsInAnyOrder(
					cn(Object.class),
					cn(Comparable.class),
					cn(Class.class),
					cn(Throwable.class),
					cn(OutOfMemoryError.class),
					cn(Integer.class),

					cn(AcmeAnnotation.class),

					cn(CharSequences.class),
					cn(Test.class),
					cn(Label.class),
					cn(SignatureReader.class),
					cn(IllegalStateException.class),
					cn(Matchers.class),
					cn(IsAnything.class),
					cn(Comparator.class),
					cn(Objects.class),
					cn(UncheckedIOException.class))));
			// EARCAM_SNIPPET_END: do-read-annotations
			// @formatter:on
		}


		@Test
		void readingAnnotationsAndTheirValuesCanBeDisabled()
		{
			Map<String, Set<String>> imports = new HashMap<>();

			// EARCAM_SNIPPET_BEGIN: do-not-read-annotations
			reader()
					.ignoreAnnotations()
					.addImportListener(imports::put)
					.processClass(Resources.classAsBytes(Annotated.class));

			assertThat(imports, is(aMapWithSize(1)));

			assertThat(imports, hasEntry(equalTo(cn(Annotated.class)), containsInAnyOrder(
					cn(Object.class),
					cn(Comparable.class),
					cn(Class.class),
					cn(Throwable.class),
					cn(OutOfMemoryError.class),
					cn(Integer.class))));
			// EARCAM_SNIPPET_END: do-not-read-annotations
		}
	}

	@Nested
	class FromClass {

		@Nested
		class Simple {

			@Test
			void importListenerOnly() throws IOException
			{
				// EARCAM_SNIPPET_BEGIN: imports-simple-class
				Map<String, Set<String>> imports = new HashMap<>();

				reader()
						.addImportListener(imports::put)
						.processClass(Resources.classAsBytes(ImportsSlf4jApi.class));

				assertThat(imports, is(aMapWithSize(1)));

				assertThat(imports, hasEntry(equalTo(cn(ImportsSlf4jApi.class)), containsInAnyOrder(
						cn(Arrays.class),
						cn(Logger.class),
						cn(LoggerFactory.class),
						cn(Object.class),
						cn(Class.class),
						cn(StringBuilder.class),
						cn(String.class))));
				// EARCAM_SNIPPET_END: imports-simple-class
			}

		}
	}

	@Nested
	class FromJar {

		@Nested
		class Simple {

			@Test
			void importListenerOnly() throws IOException
			{
				Map<String, Set<String>> imports = new HashMap<>();

				InputStream jar = bundle()
						.add(ImportsSlf4jApi.class)
						.add("irrelevant/file.txt", new ByteArrayInputStream("hello".getBytes(UTF_8)))
						.build();

				reader()
						.addImportListener(imports::put)
						.processJar(jar);

				assertThat(imports, is(aMapWithSize(1)));

				assertThat(imports, hasEntry(equalTo(cn(ImportsSlf4jApi.class)), containsInAnyOrder(
						cn(Arrays.class),
						cn(Logger.class),
						cn(LoggerFactory.class),
						cn(Object.class),
						cn(Class.class),
						cn(StringBuilder.class),
						cn(String.class))));
			}


			@Test
			void generics() throws IOException
			{
				Map<String, Set<String>> imports = new HashMap<>();

				InputStream jar = bundle()
						.add(Generics.class)
						.build();

				reader()
						.addImportListener(imports::put)
						.processJar(jar);

				assertThat(imports, is(aMapWithSize(1)));

				assertThat(imports, hasEntry(equalTo(cn(Generics.class)), containsInAnyOrder(
						cn(UncheckedIOException.class),
						cn(Comparable.class),
						cn(Object.class),
						cn(Number.class),
						cn(Serializable.class))));
			}


			@Test
			void manifestListenerOnly() throws IOException
			{
				InputStream jar = bundle()
						.add(ImportsSlf4jApi.class)
						.add("irrelevant/file.txt", new ByteArrayInputStream("hello".getBytes(UTF_8)))
						.build();

				List<Manifest> manifests = new ArrayList<>();

				reader()
						.addManifestListener(manifests::add)
						.processJar(jar);

				assertThat(manifests, hasSize(1));
			}


			@Test
			void additionalBytecodeListener() throws IOException
			{
				InputStream jar = bundle()
						.add(ImportsSlf4jApi.class)
						.add("irrelevant/file.txt", new ByteArrayInputStream("hello".getBytes(UTF_8)))
						.build();

				Map<String, Set<String>> imports = new HashMap<>();
				List<byte[]> listener = new ArrayList<>();

				reader()
						.addImportListener(imports::put)
						.addByteCodeListener(listener::add)
						.processJar(jar);

				assertThat(listener, hasSize(1));
			}


			@Test
			void listenerAndTypeReducers() throws IOException
			{
				// EARCAM_SNIPPET_BEGIN: map-jar-reduced-to-packages
				Map<String, Set<String>> imports = new HashMap<>();

				InputStream jar = bundle()
						.add(ImportsSlf4jApi.class)
						.add("irrelevant/file.txt", new ByteArrayInputStream("hello".getBytes(UTF_8)))
						.build();

				BiConsumer<String, Set<String>> listener = imports::put;

				reader()
						.addImportListener(listener)
						.setImportedTypeReducer(Reader::typeToPackageReducer)
						.setImportingTypeReducer(Reader::typeToPackageReducer)
						.processJar(jar);

				assertThat(imports, is(aMapWithSize(1)));

				Set<String> expected = new HashSet<>(Arrays.asList(
						pkg(Arrays.class),
						pkg(Logger.class),
						pkg(LoggerFactory.class),
						pkg(Object.class),
						pkg(Class.class),
						pkg(StringBuilder.class),
						pkg(String.class)));

				assertThat(imports, hasEntry(equalTo(pkg(ImportsSlf4jApi.class)), equalTo(expected)));
				// EARCAM_SNIPPET_END: map-jar-reduced-to-packages
			}
		}
	}
}
