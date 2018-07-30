/*-
 * #%L
 * io.earcam.instrumental.compile.glue
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
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.archive.ArchiveResourceSource;
import io.earcam.instrumental.compile.CompilationTarget;
import io.earcam.instrumental.compile.FileObjectProvider;
import io.earcam.instrumental.fluent.Fluent;
import io.earcam.instrumental.reflect.Names;

public final class CompiledResources implements FileObjectProvider, ArchiveResourceSource {

	private final List<CustomJavaFileObject> resources;


	private CompiledResources(List<CustomJavaFileObject> resources)
	{
		this.resources = resources;
	}


	@Fluent
	public static CompilationTarget<CompiledResources> toResources()
	{
		CompilationTarget<Map<String, byte[]>> underlying = CompilationTarget.toByteArrays();

		return new CompilationTarget<CompiledResources>() {

			@Override
			public CompiledResources get()
			{
				List<CustomJavaFileObject> fileObjects = underlying.get().entrySet().stream()
						.map(CompiledResources::toFileObject)
						.collect(toList());

				return new CompiledResources(fileObjects);
			}


			@Override
			public JavaFileManager configureOutputFileManager(StandardJavaFileManager manager)
			{
				return underlying.configureOutputFileManager(manager);
			}

		};
	}


	private static CustomJavaFileObject toFileObject(Map.Entry<String, byte[]> entry)
	{
		String name = entry.getKey();

		Kind kind = kindForName(name);
		String fileObjectName = Names.internalToTypeName(name.substring(0, name.length() - kind.extension.length()));
		return new CustomJavaFileObject(fileObjectName, kind, entry.getValue());
	}


	private static Kind kindForName(String name)
	{
		return Arrays.stream(Kind.values())
				.filter(k -> name.endsWith(k.extension))
				.findAny()
				.orElse(Kind.OTHER);
	}


	@Override
	public Stream<ArchiveResource> drain(ResourceSourceLifecycle stage)
	{
		return INITIAL.equals(stage) ? resources.stream().map(CompiledResources::toArchiveResource) : Stream.empty();
	}


	private static ArchiveResource toArchiveResource(CustomJavaFileObject fileObject)
	{
		return new ArchiveResource(fileObject.getName(), fileObject.bytes());
	}


	@Override
	public List<CustomJavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException
	{
		if(location == StandardLocation.PLATFORM_CLASS_PATH) {
			return emptyList();
		}

		String packageResource = packageName.isEmpty() ? "/" : ('/' + packageName.replace('.', '/') + '/');

		Predicate<CustomJavaFileObject> packageMatcher = o -> o.getName().startsWith(packageResource);

		if(!recurse) {
			packageMatcher = packageMatcher.and(o -> {
				String name = o.getName();
				name = name.substring(0, name.length() - o.getKind().extension.length());
				return name.lastIndexOf('/') < packageResource.length();
			});
		}

		return resources.stream()
				.filter(r -> kinds.contains(r.getKind()))
				.filter(packageMatcher)
				.collect(toList());
	}

}
