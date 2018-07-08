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

import java.io.UncheckedIOException;
import java.nio.file.Path;

import io.earcam.instrumental.fluent.Fluent;

/**
 * TODO add a foundUnder(Path) method using a recursive file visitor to find all
 * source classes, and bytecode may contain a SourceFile attribute, find it and try it.
 */
@FunctionalInterface
public interface SourceSource {

	public interface SourceSink {

		public abstract void sink(Path path);


		public abstract void sink(String text);
	}


	public abstract void drain(SourceSink sink);


	/**
	 * Adds an existing source file from a filesystem
	 * 
	 * @param sourceFile
	 * @return a {@link SourceSource} instance wrapping the source file
	 */
	@Fluent
	public static SourceSource from(Path sourceFile)
	{
		BasicSourceSource.requireExistingFile(sourceFile);
		return s -> s.sink(sourceFile);
	}


	/**
	 * Adds the String text provided the argument as source code
	 * 
	 * @param sourceCode the text for a Compilation Unit (class, enum, interface, module-info, package-info)
	 * @return a {@link SourceSource} instance wrapping the text code
	 */
	@Fluent
	public static SourceSource from(String sourceCode)
	{
		return s -> s.sink(sourceCode);
	}


	/**
	 * <p>
	 * Assumes a standard Maven project layout.
	 * </p>
	 * <p>
	 * Will find source for a Maven dependency, if the source jar has already
	 * been downloaded (typically you can simply add the dependency
	 * with {@code <classifier>sources</classifier>}).
	 * </p>
	 * 
	 * @param type the {@link Class} to find it's originating source file
	 * @return a {@link SourceSource} instance referring to the source file
	 * @throws UncheckedIOException if the source cannot be found
	 */
	@Fluent
	public static SourceSource foundFor(Class<?> type)
	{
		return BasicSourceSource.foundFor(type);
	}
}
