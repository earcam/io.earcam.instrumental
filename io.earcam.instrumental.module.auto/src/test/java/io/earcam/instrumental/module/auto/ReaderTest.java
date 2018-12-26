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
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.function.BiConsumer;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsAnything;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.signature.SignatureReader;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.simple.SimpleLogger;
import org.slf4j.simple.SimpleLoggerFactory;

import io.earcam.acme.AcmeAnnotation;
import io.earcam.acme.Annotated;
import io.earcam.acme.Generics;
import io.earcam.acme.HasArrays;
import io.earcam.acme.ImportsSlf4jApi;
import io.earcam.acme.PrimitiveMethodArguments;
import io.earcam.instrumental.reflect.Resources;
import io.earcam.utilitarian.charstar.CharSequences;

@SuppressWarnings("squid:S2187")  // SonarQube false-positive; tests are only in @Nested
public class ReaderTest {

	// EARCAM_SNIPPET_BEGIN: test-support
	public static String cn(Class<?> type)
	{
		return type.getCanonicalName();
	}


	public static String pkg(Class<?> type)
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


			@Test
			void primitiveMethodArguments() throws IOException
			{
				Map<String, Set<String>> imports = new HashMap<>();

				reader()
						.addImportListener(imports::put)
						.processClass(Resources.classAsBytes(PrimitiveMethodArguments.class));

				assertThat(imports, is(aMapWithSize(1)));

				assertThat(imports, hasEntry(equalTo(cn(PrimitiveMethodArguments.class)), containsInAnyOrder(
						cn(Object.class))));
			}


			@Test
			void arrays() throws IOException
			{
				Map<String, Set<String>> imports = new HashMap<>();

				reader()
						.addImportListener(imports::put)
						.processClass(Resources.classAsBytes(HasArrays.class));

				assertThat(imports, is(aMapWithSize(1)));

				assertThat(imports, hasEntry(equalTo(cn(HasArrays.class)), containsInAnyOrder(
						cn(Object.class),
						cn(Number.class),
						cn(Comparable.class),
						cn(AtomicLongArray.class))));
			}
		}
	}

	@Nested
	class FromJar {

		@Nested
		class Simple {

			@Test
			void listeners() throws IOException
			{
				Map<String, Set<String>> imports = new HashMap<>();

				Set<String> nonClassEntries = new HashSet<>();

				String fileA = "directory.class/fileA.txt";
				String fileB = "directory.class/fileB.txt";
				String fileC = "irrelevant/file.txt";

				InputStream jar = bundle()
						.add(ImportsSlf4jApi.class)
						.add(fileA, new ByteArrayInputStream("hello".getBytes(UTF_8)))
						.add(fileB, new ByteArrayInputStream("hello".getBytes(UTF_8)))
						.add(fileC, new ByteArrayInputStream("hello".getBytes(UTF_8)))
						.build();

				reader()
						.addImportListener(imports::put)
						.setJarEntryListener((e, i) -> nonClassEntries.add(e.getName()))
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

				assertThat(nonClassEntries, containsInAnyOrder(fileA, fileB, fileC));
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

				InputStream jarJar = new JarInputStream(jar);

				Map<String, Set<String>> imports = new HashMap<>();
				List<byte[]> listener = new ArrayList<>();

				reader()
						.addImportListener(imports::put)
						.addByteCodeListener(listener::add)
						.processJar(jarJar);

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

		@Nested
		class Library {

			/**
			 * This loads a Jar with module-info.class, which allows us to test a branch of a null
			 * guard in {@link ImportsOf}
			 */
			@Test
			void slf4jSimple() throws IOException
			{
				Map<String, Set<String>> imports = new HashMap<>();

				Path slf4jSimpleJar = Paths.get(Resources.sourceOfResource(SimpleLogger.class));

				reader()
						.addImportListener(imports::put)
						.processJar(slf4jSimpleJar);

				assertThat(imports, is(aMapWithSize(9)));

				assertThat(imports, hasEntry(equalTo(cn(SimpleLoggerFactory.class)), containsInAnyOrder(
						cn(Logger.class),
						cn(ConcurrentHashMap.class),
						cn(Object.class),
						cn(SimpleLogger.class),
						cn(String.class),
						cn(ILoggerFactory.class),
						cn(ConcurrentMap.class))));
			}

		}
	}
}
