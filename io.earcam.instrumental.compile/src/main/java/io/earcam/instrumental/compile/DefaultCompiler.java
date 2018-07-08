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

import static java.util.stream.Collectors.toList;

import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.processing.Processor;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import io.earcam.instrumental.compile.SourceSource.SourceSink;

final class DefaultCompiler implements Compiler, SourceSink {

	private static class NoopWriter extends Writer {

		@Override
		public void write(char[] cbuf, int off, int len)
		{
			/* NoOp */
		}


		@Override
		public void flush()
		{
			/* NoOp */
		}


		@Override
		public void close()
		{
			/* NoOp */
		}

	}

	private final List<FileObjectProvider> providers = new ArrayList<>();
	private final List<Processor> processors = new ArrayList<>();
	private final List<String> options = new ArrayList<>();
	private final List<String> sourceCodes = new ArrayList<>();
	private final List<Path> sourceFiles = new ArrayList<>();
	private Charset encoding = Charset.defaultCharset();
	private Locale locale = Locale.getDefault();

	private DiagnosticListener<? super JavaFileObject> diagnosticListener;
	private Writer compilerOutput = new NoopWriter();

	private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private boolean throwOnError = true;


	@Override
	public void sink(Path path)
	{
		sourceFiles.add(path);
	}


	@Override
	public void sink(String text)
	{
		sourceCodes.add(text);
	}


	@Override
	public <T> T compile(CompilationTarget<T> output)
	{
		StandardJavaFileManager standard = compiler.getStandardFileManager(diagnosticListener, locale, encoding);
		StandardForwardingJavaFileManager fileManager = new CustomJavaFileManager(standard, providers);
		JavaFileManager configuredManager = output.configureOutputFileManager(fileManager);

		List<JavaFileObject> javaFiles = inputJavaFileObjects(fileManager);
		Iterable<String> classesForAnnotationProcessing = null;

		CompilationTask task = compiler.getTask(compilerOutput, configuredManager, diagnosticListener, options, classesForAnnotationProcessing, javaFiles);
		task.setLocale(locale);
		task.setProcessors(processors);

		if(!task.call() && throwOnError) {
			throw new IllegalStateException("Compilation did not complete without errors and `throwOnError` set to true");
		}
		return output.get();
	}


	private List<JavaFileObject> inputJavaFileObjects(StandardJavaFileManager fileManager)
	{
		List<JavaFileObject> javaFiles = this.sourceCodes.stream().map(StringJavaFileObject::new).collect(toList());
		if(!sourceFiles.isEmpty()) {
			fileManager.getJavaFileObjectsFromFiles(sourceFiles.stream().map(Path::toFile).collect(Collectors.toList())).forEach(javaFiles::add);
		}
		return javaFiles;
	}


	@Override
	public Compiler sources(Iterable<SourceSource> sources)
	{
		sources.forEach(s -> s.drain(this));
		return this;
	}


	@Override
	public Compiler localisedTo(Locale locale)
	{
		this.locale = locale;
		return this;
	}


	@Override
	public Compiler encodedAs(Charset encoding)
	{
		this.encoding = encoding;
		return this;
	}


	@Override
	public Compiler withOption(String option)
	{
		options.add(option);
		return this;
	}


	@Override
	public Compiler usingCompiler(String spi)
	{
		return usingCompiler(loadCompiler(spi));
	}


	private static JavaCompiler loadCompiler(String spiCompilerClassName)
	{
		ServiceLoader<JavaCompiler> serviceLoader = ServiceLoader.load(JavaCompiler.class);

		return stream(serviceLoader)
				.filter(c -> c.getClass().getCanonicalName().equals(spiCompilerClassName))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}


	private static <T> Stream<T> stream(Iterable<T> iterable)
	{
		return StreamSupport.stream(iterable.spliterator(), false);
	}


	@Override
	public Compiler usingCompiler(JavaCompiler implementation)
	{
		compiler = implementation;
		return this;
	}


	@Override
	public Compiler processedBy(Processor annotationProcessor)
	{
		processors.add(annotationProcessor);
		return this;
	}


	@Override
	public Compiler loggingTo(Writer compilerOutputWriter)
	{
		this.compilerOutput = compilerOutputWriter;
		return this;
	}


	@Override
	public Compiler consumingDiagnositics(DiagnosticListener<? super JavaFileObject> listener)
	{
		this.diagnosticListener = listener;
		return this;
	}


	@Override
	public Compiler ignoreCompilationErrors()
	{
		this.throwOnError = false;
		return this;
	}


	@Override
	public Compiler withDependencies(FileObjectProvider provider)
	{
		providers.add(provider);
		return this;
	}
}
