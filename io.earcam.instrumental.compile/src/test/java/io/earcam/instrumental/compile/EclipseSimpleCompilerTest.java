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
import static io.earcam.instrumental.compile.CompilationTarget.toByteArrays;
import static io.earcam.instrumental.compile.CompilationTarget.toFileSystem;
import static io.earcam.instrumental.compile.SourceSource.foundFor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import javax.lang.model.SourceVersion;

import org.junit.jupiter.api.Test;

import io.earcam.acme.Pojo;
import io.earcam.instrumental.compile.DefaultCompilerTest.Simple.FromFileSystem;
import io.earcam.instrumental.reflect.Names;

/**
 * java -cp /data/repository/maven/org/eclipse/jdt/ecj/3.14.0/ecj-3.14.0.jar
 * org.eclipse.jdt.internal.compiler.batch.Main -help
 * 
 * 
 * The Eclipse compiler doesn't use JavaFileObject properly - it converts input objects to URIs and
 * assumes these are Files. So we can only test sourcing from the filesystem.
 * 
 * This test just checks the SPI functionality, and is otherwise a duplicate of some tests from
 * {@link DefaultCompilerTest}
 */
public class EclipseSimpleCompilerTest {

	private static final String ECLIPSE = "org.eclipse.jdt.internal.compiler.tool.EclipseCompiler";


	protected Compiler compiling()
	{
		return Compiler.compiling()
				.usingCompiler(ECLIPSE);
	}


	@Test
	void fromFileSystemToMap()
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
	void fromFileSystemToFileSystem() throws IOException
	{
		Path path = Paths.get(".", "target", FromFileSystem.class.getCanonicalName(), "filesystem", UUID.randomUUID().toString());

		compiling()
				.versionAt(SourceVersion.latestSupported())
				.source(foundFor(Pojo.class))
				.compile(toFileSystem(path));

		Path compiled = path.resolve(Names.typeToResourceName(Pojo.class));
		byte[] bytes = Files.readAllBytes(compiled);

		assertValidClass(bytes, Pojo.class.getCanonicalName());
	}
}
