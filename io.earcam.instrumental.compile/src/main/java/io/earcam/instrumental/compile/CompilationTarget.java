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

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

import io.earcam.instrumental.reflect.Names;

/**
 * An extendible interface for compilation output
 *
 * @param <T>
 */
public interface CompilationTarget<T> extends Supplier<T> {

	/**
	 * <p>
	 * configureOutputFileManager.
	 * </p>
	 *
	 * @param manager a {@link JavaFileManager} instance.
	 * @return a {@link javax.tools.JavaFileManager} object.
	 */
	public abstract JavaFileManager configureOutputFileManager(StandardJavaFileManager manager);


	/**
	 * <p>
	 * Returns a builder, where you must explicitly set, as a minimum, the class output directory.
	 * </p>
	 * 
	 * @return a builder for the filesystem compilation target
	 * @see CompilationTarget#toFileSystem(Path)
	 */
	public static FilesystemCompilationTargetBuilder toFileSystem()
	{
		return new FilesystemCompilationTargetBuilder();
	}


	/**
	 * <p>
	 * Compile to a filesystem. The returned {@link CompilationTarget} is an instance of
	 * {@link FilesystemCompilationTarget},
	 * which allows additional output locations to be set (generated sources and native headers)
	 * </p>
	 *
	 * @param classOutputDirectory the path to write classes to.
	 * @return a {@link FilesystemCompilationTarget} instance, which may have further calls to set output locations
	 * @see CompilationTarget#toFileSystem()
	 */
	public static FilesystemCompilationTarget toFileSystem(Path classOutputDirectory)
	{
		return new FilesystemCompilationTarget(classOutputDirectory);
	}


	/**
	 * @return an in-memory {@link CompilationTarget}.
	 */
	public static CompilationTarget<Map<String, byte[]>> toByteArrays()
	{
		return new CompilationTarget<Map<String, byte[]>>() {

			List<ByteArrayJavaFileObject> output = new ArrayList<>();


			@Override
			public JavaFileManager configureOutputFileManager(StandardJavaFileManager manager)
			{
				return new StandardForwardingJavaFileManager(manager) {

					@Override
					public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException
					{
						ByteArrayJavaFileObject javaBytes = new ByteArrayJavaFileObject(className, kind);
						output.add(javaBytes);
						return javaBytes;
					}


					@Override
					public boolean isSameFile(FileObject a, FileObject b)
					{
						return a.getName().equals(b.getName());
					}


					@Override
					public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException
					{
						Kind kind = EnumSet.allOf(Kind.class).stream()
								.filter(k -> relativeName.endsWith(k.extension))
								.findFirst()
								.orElse(Kind.OTHER);

						ByteArrayJavaFileObject fileBytes = new ByteArrayJavaFileObject(packageName + '/' + relativeName, kind);
						output.add(fileBytes);
						return fileBytes;
					}
				};
			}


			@Override
			public Map<String, byte[]> get()
			{
				return output.stream()
						.collect(toMap(b -> b.getName().substring(1), ByteArrayJavaFileObject::getBytes));
			}
		};
	}


	/**
	 * <p>
	 * toClassLoader.
	 * </p>
	 *
	 * @return a {@link CompilationTarget} object.
	 */
	public static CompilationTarget<ClassLoader> toClassLoader()
	{
		return toClassLoader(ClassLoader.getSystemClassLoader());
	}


	/**
	 * <p>
	 * toClassLoader.
	 * </p>
	 *
	 * @param parent a {@link java.lang.ClassLoader} object.
	 * @return a {@link CompilationTarget} object.
	 */
	public static CompilationTarget<ClassLoader> toClassLoader(ClassLoader parent)
	{
		CompilationTarget<Map<String, byte[]>> underlying = toByteArrays();

		return new CompilationTarget<ClassLoader>() {

			@Override
			public JavaFileManager configureOutputFileManager(StandardJavaFileManager manager)
			{
				return underlying.configureOutputFileManager(manager);
			}


			@Override
			public ClassLoader get()
			{
				Map<String, byte[]> map = underlying.get();

				return new ClassLoader(parent) {
					@Override
					public Class<?> loadClass(String name) throws ClassNotFoundException
					{
						byte[] bytes = map.get(Names.typeToResourceName(name));
						return (bytes == null) ? super.loadClass(name) : defineClass(null, bytes, 0, bytes.length, null);
					}

				};
			}
		};
	}


	/**
	 * Useful if only interested in annotation processing
	 *
	 * @return a NOOP {@link CompilationTarget}
	 */
	public static CompilationTarget<Void> toBlackhole()
	{
		CompilationTarget<Map<String, byte[]>> map = toByteArrays();
		return new CompilationTarget<Void>() {

			@Override
			public Void get()
			{
				return null;
			}


			@Override
			public JavaFileManager configureOutputFileManager(StandardJavaFileManager manager)
			{
				return map.configureOutputFileManager(manager);
			}
		};
	}
}
