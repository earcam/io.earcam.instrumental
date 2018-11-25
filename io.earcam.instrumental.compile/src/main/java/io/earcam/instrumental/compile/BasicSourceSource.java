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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.earcam.instrumental.reflect.Resources;
import io.earcam.utilitarian.io.IoStreams;

final class BasicSourceSource {

	private BasicSourceSource()
	{}


	public static SourceSource foundFor(Class<?> type)
	{
		Path resource = Paths.get(Resources.sourceOfResource(type));

		String paquet = type.getPackage().getName().replace('.', File.separatorChar);
		Path tail = Paths.get(paquet).resolve(type.getSimpleName() + ".java");

		if(resource.toFile().isDirectory()) {
			Path middle;
			if(resource.endsWith(Paths.get("target", "test-classes"))) {
				middle = Paths.get("src", "test", "java");
			} else if(resource.endsWith(Paths.get("target", "classes"))) {
				middle = Paths.get("src", "main", "java");
			} else {
				throw new IllegalStateException("Cannot determine source for file " + type.getCanonicalName());
			}

			Path source = resource.getParent().getParent().resolve(middle).resolve(tail);
			requireExistingFile(source);
			return s -> s.sink(source);
		}

		Path sourceJar = resource.getParent().resolve(resource.getFileName().toString().replaceAll("(.*)\\.jar", "$1-sources.jar"));

		try(ZipInputStream zip = new ZipInputStream(new FileInputStream(sourceJar.toFile()))) {
			ZipEntry entry;
			while((entry = zip.getNextEntry()) != null) {
				if(tail.toString().equals(entry.getName())) {
					String source = new String(IoStreams.readAllBytes(zip), UTF_8);
					return s -> s.sink(source);
				}
			}
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
		throw new IllegalStateException("Did not find source for " + type + " in " + sourceJar);
	}


	static void requireExistingFile(Path file)
	{
		if(!file.toFile().isFile()) {
			FileNotFoundException fnfe = new FileNotFoundException(file.toAbsolutePath().toString());
			throw new UncheckedIOException(fnfe);
		}
	}
}
