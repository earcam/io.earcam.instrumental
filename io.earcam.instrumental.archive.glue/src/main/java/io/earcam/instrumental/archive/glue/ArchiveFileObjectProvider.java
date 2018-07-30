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
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.compile.FileObjectProvider;
import io.earcam.instrumental.reflect.Names;

public final class ArchiveFileObjectProvider implements FileObjectProvider {

	private List<Archive> archives;


	private ArchiveFileObjectProvider(List<Archive> archives)
	{
		this.archives = archives;
	}


	public static ArchiveFileObjectProvider fromArchive(Archive archive)
	{
		return fromArchives(Collections.singletonList(archive));
	}


	public static ArchiveFileObjectProvider fromArchives(List<Archive> archives)
	{
		return new ArchiveFileObjectProvider(archives);
	}


	@Override
	public List<CustomJavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException
	{
		if(location == StandardLocation.PLATFORM_CLASS_PATH) {
			return Collections.emptyList();
		}
		return list(packageName, kinds, recurse);
	}


	private List<CustomJavaFileObject> list(String packageName, Set<Kind> kinds, boolean recurse)
	{
		Set<String> extensions = kinds.stream().map(e -> e.extension).collect(toSet());
		Predicate<? super ArchiveResource> packageMatcher = packageMatcher(recurse, packageName);

		return listFromArchives(kinds, extensions, packageMatcher);
	}


	private Predicate<? super ArchiveResource> packageMatcher(boolean recurse, String packageName)
	{
		String packageResource = Names.typeToInternalName(packageName);
		Predicate<? super ArchiveResource> recursive = r -> r.name().startsWith(packageResource);
		Predicate<? super ArchiveResource> nonRecursive = r -> {
			int index = r.name().lastIndexOf('/');
			return (index == -1) ? r.name().startsWith(packageResource) : r.name().substring(0, index).equals(packageResource);
		};
		return (recurse) ? recursive : nonRecursive;
	}


	private List<CustomJavaFileObject> listFromArchives(Set<Kind> kinds, Set<String> extensions, Predicate<? super ArchiveResource> packageMatcher)
	{
		return archives.stream()
				.map(Archive::contents)
				.flatMap(List::stream)
				.filter(packageMatcher)
				.filter(r -> extensions.contains(r.extension()))
				.map(r -> new CustomJavaFileObject(
						Names.internalToTypeName(r.name().substring(0, r.name().length() - r.extension().length())),
						kinds.stream()
								.filter(x -> x.extension.equals(r.extension()))
								.findFirst()
								.orElseThrow(IllegalStateException::new),
						r.bytes()))
				.collect(toList());
	}
}
