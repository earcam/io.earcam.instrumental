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

import static java.io.File.pathSeparator;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;

import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;

import io.earcam.instrumental.fluent.Fluent;

public interface Compiler {

	@Fluent
	public static Compiler compiling()
	{
		return new DefaultCompiler();
	}


	public abstract <T> T compile(CompilationTarget<T> target);


	public default Compiler source(SourceSource source)
	{
		return sources(source);
	}


	public default Compiler sources(SourceSource... sources)
	{
		return sources(Arrays.asList(sources));
	}


	public abstract Compiler sources(Iterable<SourceSource> sources);


	public default Compiler versionAt(SourceVersion version)
	{
		return sourceVersionAt(version).targetVersionAt(version);
	}


	public default Compiler sourceVersionAt(SourceVersion version)
	{
		return withOption("-source").withOption(Integer.toString(version.ordinal()));
	}


	public default Compiler targetVersionAt(SourceVersion version)
	{
		return withOption("-target").withOption(Integer.toString(version.ordinal()));
	}


	public abstract Compiler withOption(String option);


	/**
	 * <p>
	 * The {@link Locale} use when formatting diagnostic messages.
	 * </p>
	 * 
	 * @param locale
	 * @return this builder
	 */
	public abstract Compiler localisedTo(Locale locale);


	/**
	 * <p>
	 * The {@link Charset} used when decoding byte sources.
	 * </p>
	 * 
	 * @param encoding
	 * @return this builder
	 */
	public abstract Compiler encodedAs(Charset encoding);


	public default Compiler withProcessorOption(String key, String value)
	{
		return withOption("-A" + key + "=" + value);
	}


	public default Compiler withClasspath(Path... classpath)
	{
		return withOption("-classpath").withOption(stream(classpath)
				.map(Path::toAbsolutePath)
				.map(Object::toString)
				.collect(joining(pathSeparator)));
	}


	public abstract Compiler withDependencies(FileObjectProvider provider);


	public abstract Compiler usingCompiler(String spi);


	public abstract Compiler usingCompiler(JavaCompiler implementation);


	public abstract Compiler processedBy(Processor annotationProcessor);


	public abstract Compiler loggingTo(Writer compilerOutputWriter);


	public default Compiler consumingDiagnositicMessages(Consumer<String> listener)
	{
		DiagnosticListener<? super JavaFileObject> l = d -> listener.accept(d.getMessage(ROOT));
		return consumingDiagnositics(l);
	}


	public abstract Compiler consumingDiagnositics(DiagnosticListener<? super JavaFileObject> listener);


	/**
	 * <p>
	 * If compilation errors are encountered, then typically an {@link Exception} is thrown,
	 * invoking this method suppresses that behaviour.
	 * </p>
	 * 
	 * @return this builder
	 */
	public abstract Compiler ignoreCompilationErrors();
}
