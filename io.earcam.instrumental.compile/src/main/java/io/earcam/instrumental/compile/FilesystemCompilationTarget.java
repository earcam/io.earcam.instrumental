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

import static java.util.Collections.singleton;
import static javax.tools.StandardLocation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;

import io.earcam.unexceptional.Exceptional;

public final class FilesystemCompilationTarget implements CompilationTarget<Path> {

	private Path classOutputDirectory;
	private Path sourceOutputDirectory;
	private Path nativeHeaderOutputDirectory;


	FilesystemCompilationTarget(Path classOutputDirectory)
	{
		this.nativeHeaderOutputDirectory = this.sourceOutputDirectory = this.classOutputDirectory = classOutputDirectory;
	}


	public FilesystemCompilationTarget generatingSourcesIn(Path sourceOutputDirectory)
	{
		this.sourceOutputDirectory = sourceOutputDirectory;
		return this;
	}


	public FilesystemCompilationTarget generatingNativeHeadersIn(Path nativeHeaderOutputDirectory)
	{
		this.nativeHeaderOutputDirectory = nativeHeaderOutputDirectory;
		return this;
	}


	@Override
	public JavaFileManager configureOutputFileManager(StandardJavaFileManager manager)
	{
		Exceptional.accept(this::configure, manager);
		return manager;
	}


	public void configure(StandardJavaFileManager manager) throws IOException
	{
		File classes = mkdirs(classOutputDirectory);
		File sources = mkdirs(sourceOutputDirectory);
		File natives = mkdirs(nativeHeaderOutputDirectory);

		manager.setLocation(CLASS_OUTPUT, singleton(classes));
		manager.setLocation(SOURCE_OUTPUT, singleton(sources));
		manager.setLocation(NATIVE_HEADER_OUTPUT, singleton(natives));
	}


	private File mkdirs(Path path)
	{
		File file = path.toFile();
		file.mkdirs();
		return file;
	}


	/**
	 * Returns the output <b>classes</b> directory
	 */
	@Override
	public Path get()
	{
		return classOutputDirectory;
	}
}
