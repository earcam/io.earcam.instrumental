/*-
 * #%L
 * io.earcam.instrumental.archive.glue
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

import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.INITIAL;

import java.util.Map;
import java.util.stream.Stream;

import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.archive.ArchiveConfiguration;
import io.earcam.instrumental.archive.ArchiveConfigurationPlugin;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.archive.ArchiveResourceSource;
import io.earcam.instrumental.compile.CompilationTarget;
import io.earcam.instrumental.fluent.Fluent;

/**
 * <p>
 * CompileToArchive class.
 * </p>
 *
 */
public final class CompileToArchive {

	private CompileToArchive()
	{}


	/**
	 * <p>
	 * toArchive.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.compile.CompilationTarget} object.
	 */
	@Fluent
	public static CompilationTarget<ArchiveConfiguration> toArchive()
	{
		CompilationTarget<Map<String, byte[]>> underlying = CompilationTarget.toByteArrays();

		ArchiveResourceSource source = stage -> {
			if(stage == INITIAL) {
				return underlying.get().entrySet().stream()
						.map(e -> new ArchiveResource(e.getKey(), e.getValue()));
			}
			return Stream.empty();
		};

		ArchiveConfigurationPlugin plugin = core -> core.registerResourceSource(source);

		ArchiveConfiguration archive = Archive.archive().configured(plugin);

		return new CompilationTarget<ArchiveConfiguration>() {

			@Override
			public JavaFileManager configureOutputFileManager(StandardJavaFileManager manager)
			{
				return underlying.configureOutputFileManager(manager);
			}


			public ArchiveConfiguration get()
			{
				return archive;
			}
		};
	}
}
