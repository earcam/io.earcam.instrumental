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
package io.earcam.instrumental.archive.glue;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.compile.FileObjectProvider;
import io.earcam.instrumental.reflect.Names;

public final class ArchiveFileObjectProvider implements FileObjectProvider {

	private Archive archive;


	private ArchiveFileObjectProvider(Archive archive)
	{
		this.archive = archive;
	}


	public static ArchiveFileObjectProvider fromArchive(Archive archive)
	{
		return new ArchiveFileObjectProvider(archive);
	}


	@Override
	public List<CustomJavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException
	{
		if(location == StandardLocation.PLATFORM_CLASS_PATH) {
			return Collections.emptyList();
		}
		Set<String> k = kinds.stream().map(e -> e.extension).collect(Collectors.toSet());

		String packageResource = Names.typeToBinaryName(packageName);

		Predicate<? super ArchiveResource> recursive = r -> r.name().startsWith(packageResource);
		Predicate<? super ArchiveResource> nonRecursive = r -> {
			int index = r.name().lastIndexOf('/');
			return (index == -1) ? r.name().startsWith(packageResource) : r.name().substring(0, index).equals(packageResource);
		};

		Predicate<? super ArchiveResource> packageMatcher = (recurse) ? recursive : nonRecursive;

		return archive.contents()
				.stream()
				.filter(packageMatcher)
				.filter(r -> k.contains(r.extension()))
				.map(r -> new CustomJavaFileObject(
						Names.binaryToTypeName(r.name().substring(0, r.name().length() - r.extension().length())),
						kinds.stream()
								.filter(x -> x.extension.equals(r.extension()))
								.findFirst()
								.orElseThrow(IllegalStateException::new),
						r.bytes()))
				.collect(toList());
	}
}
