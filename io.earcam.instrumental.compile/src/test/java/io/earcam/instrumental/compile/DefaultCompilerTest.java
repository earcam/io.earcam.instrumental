/*-
 * #%L
 * io.earcam.instrumental.compile
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
package io.earcam.instrumental.compile;

import static io.earcam.instrumental.compile.ClassAssertions.assertValidClass;
import static io.earcam.instrumental.compile.CompilationTarget.toBlackhole;
import static io.earcam.instrumental.compile.CompilationTarget.toByteArrays;
import static io.earcam.instrumental.compile.CompilationTarget.toClassLoader;
import static io.earcam.instrumental.compile.CompilationTarget.toFileSystem;
import static io.earcam.instrumental.compile.SourceSource.foundFor;
import static io.earcam.instrumental.compile.SourceSource.from;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static java.util.Locale.CHINESE;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.earcam.acme.AnnotatedPojo;
import io.earcam.acme.HasNativeHeaders;
import io.earcam.acme.Pojo;
import io.earcam.instrumental.lade.ClassLoaders;
import io.earcam.instrumental.reflect.Methods;
import io.earcam.instrumental.reflect.Names;

/**
 * 
 * @see HotspotCompilerTest
 * @see EclipseCompilerTest
 *
 */
public abstract class DefaultCompilerTest {

	protected abstract Compiler compiling();

	@Nested
	class Simple {

		@Nested
		class FromFileSystem {

			@Test
			void map()
			{
				Map<String, byte[]> map;

				map = compiling()
						.versionAt(SourceVersion.latestSupported())
						.source(foundFor(Pojo.class))
						.compile(toByteArrays());

				byte[] bytes = map.get(Names.typeToResourceName(Pojo.class));

				assertValidClass(bytes, Pojo.class.getCanonicalName());
			}


			@Test
			void filesystem() throws IOException
			{
				Path path = Paths.get(".", "target", FromFileSystem.class.getCanonicalName(), "filesystem", UUID.randomUUID().toString());

				// EARCAM_SNIPPET_BEGIN: filesystem
				compiling()
						.versionAt(SourceVersion.latestSupported())
						.source(foundFor(Pojo.class))
						.compile(toFileSystem(path));

				Path compiled = path.resolve(Names.typeToResourceName(Pojo.class));
				byte[] bytes = Files.readAllBytes(compiled);

				assertValidClass(bytes, Pojo.class.getCanonicalName());
				// EARCAM_SNIPPET_END: filesystem
			}
		}

		@Nested
		class FromString {

			// EARCAM_SNIPPET_BEGIN: string
			final String fqn = "com.acme.Thing";

			final String text = "" +
					"package com.acme;   \n" +
					"                    \n" +
					"class Thing {       \n" +

					" int fortyTwo()     \n" +
					" {                  \n" +
					"   return 42;       \n" +
					" }                  \n" +
					"}                   \n";


			@Test
			void map()
			{
				Map<String, byte[]> map;

				map = compiling()
						.versionAt(latestSupported())
						.source(from(text))
						.compile(toByteArrays());

				byte[] bytes = map.get(Names.typeToResourceName(fqn));

				assertValidClass(bytes, fqn);
			}
			// EARCAM_SNIPPET_END: string


			@Test
			void filesystem() throws IOException
			{
				Path path = Paths.get(".", "target", FromString.class.getCanonicalName(), "filesystem", UUID.randomUUID().toString());

				compiling()
						.versionAt(SourceVersion.latestSupported())
						.source(from(text))
						.compile(toFileSystem(path));

				Path compiled = path.resolve(Names.typeToResourceName(fqn));
				byte[] bytes = Files.readAllBytes(compiled);

				assertValidClass(bytes, fqn);
			}
		}
	}


	// TODO the encoding is not passed on to file sources and is irrelevant to String sources
	@Test
	void encodingCurrentlyAppearsToDoAbsolutelyNothing() throws ReflectiveOperationException
	{
		final String fqn = "com.acme.Smile";

		final String text = "" +
				"package com.acme;            \n" +
				"                             \n" +
				"public class Smile {         \n" +

				" public String smile()       \n" +
				" {                           \n" +
				"   return \"\u263B\";        \n" +
				" }                           \n" +
				"}                            \n";

		Map<String, byte[]> map;

		map = compiling()
				.versionAt(latestSupported())
				.encodedAs(ISO_8859_1)
				.source(from(text))
				.compile(toByteArrays());

		byte[] bytes = map.get(Names.typeToResourceName(fqn));

		assertValidClass(bytes, fqn);

		Object instance = ClassLoaders.load(bytes).newInstance();

		String returned = (String) Methods.getMethod(instance, "smile")
				.orElseThrow(NullPointerException::new)
				.invoke(instance);

		assertThat(returned, is(equalTo("\u263B")));  // should be compiler error/warning
	}

	@Nested
	class SupportedVersions {

	}

	@Nested
	class Native {
		@Test
		void filesystem() throws IOException
		{

			Path path = Paths.get(".", "target", Native.class.getCanonicalName(), "filesystem", UUID.randomUUID().toString());
			Path bin = path.resolve("bin");
			Path src = path.resolve("src");
			Path cpp = path.resolve("cpp");

			compiling()
					.versionAt(latestSupported())
					.source(foundFor(HasNativeHeaders.class))
					.compile(toFileSystem()
							.generatingBinariesIn(bin)
							.generatingSourcesIn(src)
							.generatingNativeHeadersIn(cpp));

			byte[] bytes = bytesForClass(bin, HasNativeHeaders.class);
			assertValidClass(bytes, HasNativeHeaders.class.getCanonicalName());

			Path header = cpp.resolve(HasNativeHeaders.class.getCanonicalName().replace('.', '_') + ".h");
			assertThat(header.toFile(), is(anExistingFile()));
		}


		private byte[] bytesForClass(Path basePath, Class<HasNativeHeaders> type) throws IOException
		{
			Path classFile = basePath.resolve(Names.typeToResourceName(type));
			assertThat(classFile.toFile(), is(anExistingFile()));
			byte[] bytes = Files.readAllBytes(classFile);
			return bytes;
		}


		@Test
		void filesystemAllInSameDirectory() throws IOException
		{
			Path path = Paths.get(".", "target", Native.class.getCanonicalName(), "filesystemAllInSameDirectory", UUID.randomUUID().toString());

			compiling()
					.versionAt(latestSupported())
					.source(foundFor(HasNativeHeaders.class))
					.compile(toFileSystem(path));

			byte[] bytes = bytesForClass(path, HasNativeHeaders.class);
			assertValidClass(bytes, HasNativeHeaders.class.getCanonicalName());

			Path header = path.resolve(HasNativeHeaders.class.getCanonicalName().replace('.', '_') + ".h");
			assertThat(header.toFile(), is(anExistingFile()));
		}
	}

	@Nested
	class Classpath {

		@Test
		public void implicitlyUsesCurrentClasspath() throws Exception
		{
			Class<?> loaded = compiling()
					.versionAt(RELEASE_8)
					.source(foundFor(DefaultCompilerTest.class))
					.compile(toClassLoader())
					.loadClass(DefaultCompilerTest.class.getCanonicalName());

			assertThat(loaded, is(not(sameInstance(DefaultCompilerTest.class))));
			assertThat(loaded.getCanonicalName(), is(equalTo(DefaultCompilerTest.class.getCanonicalName())));
		}


		@Test
		public void explicitClasspathReplacesImplicitInheritedClasspath() throws Exception
		{
			try {
				compiling()
						.versionAt(RELEASE_8)
						.source(foundFor(DefaultCompilerTest.class))
						.withClasspath(Paths.get("/", "tmp", UUID.randomUUID().toString()))
						.compile(toClassLoader());
				fail();
			} catch(Exception e) {}
		}

	}

	@Nested
	class CustomFileObjectProvider {

		@Test
		void testName()
		{
			Path fakeSourceRoot = Paths.get("src", "test", "resources", "source");

			Path notOnClasspathHasDependencies = Paths.get("io", "earcam", "acme", "resource", "NotOnClasspathHasDependencies.java");
			Path notOnClasspathPojo = Paths.get("io", "earcam", "acme", "resource", "NotOnClasspathPojo.java");

			Path sourceFile = fakeSourceRoot.resolve(notOnClasspathHasDependencies);
			Path dependencyFile = fakeSourceRoot.resolve(notOnClasspathPojo);

			FileObjectProvider customProvider = (l, p, k, r) -> {
				if(notOnClasspathHasDependencies.getParent().toString().replace('/', '.').equals(p)) {
					String name = notOnClasspathPojo.toString().replace('/', '.');
					return singletonList(
							new FileObjectProvider.CustomJavaFileObject(name.substring(0, name.length() - 5), JavaFileObject.Kind.SOURCE, dependencyFile));
				}
				return Collections.emptyList();
			};
			compiling()
					.versionAt(RELEASE_8)
					.source(from(sourceFile))
					.withDependencies(customProvider)
					.compile(toClassLoader());
		}
	}

	private static final String OPTION = "key";
	private static final Kind MESSAGE_KIND = Kind.NOTE;
	private static final String DIAGNOSTIC_MESSAGE = "Hello Writer";

	@Nested
	class AnnotationProcessing {

		private final class MessageLoggingProcessor extends AbstractProcessor {

			private final Kind kind;
			private final String message;


			public MessageLoggingProcessor()
			{
				this(MESSAGE_KIND, DIAGNOSTIC_MESSAGE);
			}


			public MessageLoggingProcessor(Kind kind, String message)
			{
				this.kind = kind;
				this.message = message;
			}


			@Override
			public Set<String> getSupportedAnnotationTypes()
			{
				return Collections.singleton("*");
			}


			@Override
			public SourceVersion getSupportedSourceVersion()
			{
				return SourceVersion.latestSupported();
			}


			@Override
			public synchronized void init(ProcessingEnvironment processingEnv)
			{
				processingEnv.getMessager().printMessage(kind, message);
				super.init(processingEnv);
			}


			@Override
			public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
			{
				return false;
			}
		};

		@SupportedAnnotationTypes("javax.annotation.concurrent.NotThreadSafe")
		@SupportedSourceVersion(RELEASE_8)
		@SupportedOptions(OPTION)
		private final class DummyProcessor extends AbstractProcessor {

			String annotation;
			String rootElement;
			String option;


			@Override
			public synchronized void init(ProcessingEnvironment processingEnv)
			{
				super.init(processingEnv);
				this.option = processingEnv.getOptions().get(OPTION);
			}


			@Override
			public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
			{
				if(!annotations.isEmpty()) {
					this.annotation = annotations.iterator().next().getQualifiedName().toString();
				}
				if(!roundEnv.getRootElements().isEmpty()) {
					this.rootElement = roundEnv.getRootElements().iterator().next().toString();
				}
				return true;
			}
		}

		private final class SourceGeneratingProcessor extends AbstractProcessor {

			String paquet = "com.acme.doo.dah.day";
			String name = "Yippee";
			String fqn = paquet + '.' + name;

			String resourcePath = "resources";
			String resourceName = "config.properties";
			String resourceFqn = resourcePath + "/" + resourceName;
			String property1 = "some=property";
			String property2 = "another=prop";


			@Override
			public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
			{
				if(roundEnv.processingOver()) {
					try {
						JavaFileObject jfo = processingEnv.getFiler().createSourceFile(fqn);
						try(BufferedWriter bw = new BufferedWriter(jfo.openWriter())) {
							bw.append("package ").append(paquet).append(";\n\n");
							bw.append("public class ").append(name).append(" {}");
						}

						FileObject resource = processingEnv.getFiler().createResource(SOURCE_OUTPUT, resourcePath, resourceName);
						try(BufferedWriter bw = new BufferedWriter(resource.openWriter())) {
							bw.append(property1).append("\n").append(property2);
						}

					} catch(IOException e) {
						throw new UncheckedIOException(e);
					}
				}
				return true;
			}


			@Override
			public SourceVersion getSupportedSourceVersion()
			{
				return SourceVersion.RELEASE_8;
			}


			@Override
			public Set<String> getSupportedAnnotationTypes()
			{
				return Collections.singleton(NotThreadSafe.class.getCanonicalName());
			}
		}


		@Test
		public void annotationProcessorIsInvokedOnCompiledClass() throws IOException
		{
			DummyProcessor processor = new DummyProcessor();

			compiling()
					.source(foundFor(AnnotatedPojo.class))
					.versionAt(RELEASE_8)
					.processedBy(processor)
					.compile(toBlackhole());

			assertThat(processor.rootElement, is(equalTo(AnnotatedPojo.class.getCanonicalName())));
			assertThat(processor.annotation, is(equalTo(NotThreadSafe.class.getCanonicalName())));
		}


		@Test
		public void annotationProcessorRecievesOptions() throws IOException
		{
			DummyProcessor processor = new DummyProcessor();
			String value = "value";

			compiling()
					.source(foundFor(AnnotatedPojo.class))
					.versionAt(RELEASE_8)
					.processedBy(processor)
					.withProcessorOption(OPTION, value)
					.compile(toBlackhole());

			assertThat(processor.option, is(equalTo(value)));
		}


		@Test
		public void failsOnDiagnosticError() throws Exception
		{
			String message = "Oi Goose, Boo!";
			MessageLoggingProcessor messageLoggingProcessor = new MessageLoggingProcessor(Kind.ERROR, message);
			StringWriter diagnostics = new StringWriter();

			try {
				compiling()
						.source(foundFor(AnnotatedPojo.class))
						.versionAt(RELEASE_8)
						.processedBy(messageLoggingProcessor)
						.consumingDiagnositicMessages(diagnostics::write)
						.compile(toBlackhole());
				fail();
			} catch(Exception e) {}

			assertThat(diagnostics, hasToString(equalTo(message)));
		}


		@Test
		public void doesNotFailOnDiagnosticErrorWhenIgnoreErrorsSet() throws Exception
		{
			String message = "Oi Goose, Boo!";
			MessageLoggingProcessor messageLoggingProcessor = new MessageLoggingProcessor(Kind.ERROR, message);
			StringWriter diagnostics = new StringWriter();

			compiling()
					.source(foundFor(AnnotatedPojo.class))
					.versionAt(RELEASE_8)
					.processedBy(messageLoggingProcessor)
					.consumingDiagnositicMessages(diagnostics::write)
					.ignoreCompilationErrors()
					.compile(toBlackhole());

			assertThat(diagnostics, hasToString(equalTo(message)));
		}


		@Test
		public void diagnosticsWriter() throws Exception
		{
			MessageLoggingProcessor messageLoggingProcessor = new MessageLoggingProcessor();
			StringWriter diagnostics = new StringWriter();

			compiling()
					.source(foundFor(AnnotatedPojo.class))
					.versionAt(RELEASE_8)
					.processedBy(messageLoggingProcessor)
					.consumingDiagnositicMessages(diagnostics::write)
					.compile(toBlackhole());

			assertThat(diagnostics, hasToString(equalTo(DIAGNOSTIC_MESSAGE)));
		}


		@Test
		public void loggingOutput() throws Exception
		{
			MessageLoggingProcessor messageLoggingProcessor = new MessageLoggingProcessor();
			StringWriter diagnostics = new StringWriter();

			compiling()
					.source(foundFor(AnnotatedPojo.class))
					.versionAt(RELEASE_8)
					.processedBy(messageLoggingProcessor)
					.loggingTo(diagnostics)
					.compile(toBlackhole());

			assertThat(diagnostics, hasToString(allOf(
					containsString(DIAGNOSTIC_MESSAGE),
					containsStringIgnoringCase(MESSAGE_KIND.name()))));
		}


		@Test
		public void generatedSourceOutputToMap() throws Exception
		{
			SourceGeneratingProcessor processor = new SourceGeneratingProcessor();

			Map<String, byte[]> map;
			map = compiling()
					.source(foundFor(AnnotatedPojo.class))
					.versionAt(RELEASE_8)
					.processedBy(processor)
					.compile(toByteArrays());

			assertThat(map, hasKey(processor.fqn.replace('.', '/') + ".java"));

			assertThat(map, hasKey(processor.resourceFqn));
		}


		@Test
		public void generatedSourceOutputToFilesystem() throws Exception
		{
			Path path = Paths.get(".", "target", AnnotationProcessing.class.getCanonicalName(), "generatedSourceOutputToFilesystem",
					UUID.randomUUID().toString());

			SourceGeneratingProcessor processor = new SourceGeneratingProcessor();

			compiling()
					.source(foundFor(AnnotatedPojo.class))
					.versionAt(RELEASE_8)
					.processedBy(processor)
					.compile(toFileSystem(path));

			File sourceFile = path.resolve(processor.fqn.replace('.', File.separatorChar) + ".java").toFile();
			assertThat(sourceFile, is(anExistingFile()));

			Path resource = path.resolve(processor.resourceFqn);

			assertThat(resource.toFile(), is(anExistingFile()));
			String contents = new String(Files.readAllBytes(resource), UTF_8);
			assertThat(contents, allOf(
					containsString(processor.property1),
					containsString(processor.property2)));
		}


		@Test
		void localeIsAvailableForAnnotationProcessor()
		{
			AtomicBoolean processed = new AtomicBoolean(false);
			AtomicReference<Locale> locale = new AtomicReference<Locale>();
			Processor processor = new AbstractProcessor() {

				@Override
				public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
				{
					processed.set(true);
					locale.set(processingEnv.getLocale());
					return false;
				}


				@Override
				public Set<String> getSupportedAnnotationTypes()
				{
					return Collections.singleton(NotThreadSafe.class.getCanonicalName());
				}
			};
			compiling()
					.source(foundFor(AnnotatedPojo.class))
					.versionAt(RELEASE_8)
					.localisedTo(CHINESE)
					.processedBy(processor)
					.compile(toBlackhole());

			assertThat(processed.get(), is(true));

			assumeFalse(System.getProperty("java.home").contains("8"), "Not working in default jdk.compile for v8");

			assertThat(locale.get(), is(CHINESE));
		}
	}
}
