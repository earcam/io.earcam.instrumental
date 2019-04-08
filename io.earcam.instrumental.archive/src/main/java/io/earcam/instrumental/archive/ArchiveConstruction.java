/*-
 * #%L
 * io.earcam.instrumental.archive
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
package io.earcam.instrumental.archive;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import io.earcam.instrumental.fluent.Fluent;
import io.earcam.unexceptional.CheckedBiFunction;
import io.earcam.unexceptional.Exceptional;
import io.earcam.utilitarian.io.ExplodedJarInputStream;
import io.earcam.utilitarian.io.ExplodedJarInputStream.ExplodedJarEntry;
import io.earcam.utilitarian.io.IoStreams;

/**
 * <p>
 * ArchiveConstruction interface.
 * </p>
 *
 */
public interface ArchiveConstruction extends ArchiveTransform {

	public static final class ContentFilters {

		public static final Predicate<String> NO_FILTER = x -> true;


		private ContentFilters()
		{}
	}


	/**
	 * <p>
	 * filteredBy.
	 * </p>
	 *
	 * @param filter a {@link io.earcam.instrumental.archive.ArchiveResourceFilter} object.
	 * @return a {@link io.earcam.instrumental.archive.ArchiveConstruction} object.
	 */
	public abstract ArchiveConstruction filteredBy(ArchiveResourceFilter filter);


	/**
	 * <p>
	 * sourcing.
	 * </p>
	 *
	 * @param source a {@link io.earcam.instrumental.archive.ArchiveResourceSource} object.
	 * @return a {@link io.earcam.instrumental.archive.ArchiveConstruction} object.
	 */
	public abstract ArchiveConstruction sourcing(ArchiveResourceSource source);


	/**
	 * <p>
	 * with.
	 * </p>
	 *
	 * @param types a {@link java.lang.Class} object.
	 * @return a {@link io.earcam.instrumental.archive.ArchiveConstruction} object.
	 */
	public default ArchiveConstruction with(Class<?>... types)
	{
		Arrays.stream(types).forEach(this::with);
		return this;
	}


	/**
	 * <p>
	 * with.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @return a {@link io.earcam.instrumental.archive.ArchiveConstruction} object.
	 */
	public abstract ArchiveConstruction with(Class<?> type);


	/**
	 * <p>
	 * with.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param resource a {@link java.nio.file.Path} object.
	 * @return a {@link io.earcam.instrumental.archive.ArchiveConstruction} object.
	 */
	public default ArchiveConstruction with(String name, Path resource)
	{
		return with(name, resource.toFile());
	}


	/**
	 * <p>
	 * with.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param resource a {@link java.io.File} object.
	 * @return a {@link io.earcam.instrumental.archive.ArchiveConstruction} object.
	 */
	public default ArchiveConstruction with(String name, File resource)
	{
		InputStream inputStream = Exceptional.apply(FileInputStream::new, resource);
		return with(name, inputStream);
	}


	/**
	 * <p>
	 * with.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param resource a {@link java.io.InputStream} object.
	 * @return a {@link io.earcam.instrumental.archive.ArchiveConstruction} object.
	 */
	public abstract ArchiveConstruction with(String name, InputStream resource);


	/**
	 * <p>
	 * with.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param contents an array of {@link byte} objects.
	 * @return a {@link io.earcam.instrumental.archive.ArchiveConstruction} object.
	 */
	public abstract ArchiveConstruction with(String name, byte[] contents);


	/**
	 * <p>
	 * Include an Archive's resources in the construction of another.
	 * </p>
	 * <p>
	 * Note: this does not copy the manifest, see {@link AbstractAsJarBuilder#mergingManifest(java.util.jar.Manifest)}
	 * </p>
	 * 
	 * @param archive the archive with content
	 * @return an {@link ArchiveResourceSource} that will be drained on first call.
	 * 
	 * @see #contentFrom(Archive, ArchiveResourceFilter)
	 * @see #sourcing(ArchiveResourceSource)
	 */
	public static ArchiveResourceSource contentFrom(Archive archive)
	{
		ArchiveResourceFilter noop = UnaryOperator.<ArchiveResource> identity()::apply;
		return contentFrom(archive, noop);
	}


	/**
	 * <p>
	 * Include an Archive's resources in the construction of another, using the supplied {@code filter}
	 * to optionally modify or exclude content.
	 * </p>
	 * <p>
	 * Note: this does not copy the manifest, see {@link AbstractAsJarBuilder#mergingManifest(java.util.jar.Manifest)}
	 * </p>
	 * 
	 * @param archive the archive with content
	 * @param filter the filter to apply to content from the archive
	 * @return an {@link ArchiveResourceSource} that will be drained on first call.
	 * 
	 * @see #contentFrom(Archive)
	 * @see #sourcing(ArchiveResourceSource)
	 */
	public static ArchiveResourceSource contentFrom(Archive archive, ArchiveResourceFilter filter)
	{
		List<ArchiveResource> resources = archive.contents().stream()
				.map(filter)
				.filter(Objects::nonNull)
				.collect(toList());
		return ArchiveResourceSource.wrap(resources);
	}


	/**
	 * <p>
	 * contentFrom.
	 * </p>
	 *
	 * @param path a JAR or exploded-JAR directory
	 * @return a {@link ArchiveResourceSource}.
	 * 
	 * @see #sourcing(ArchiveResourceSource)
	 */
	@Fluent
	public static ArchiveResourceSource contentFrom(Path path)
	{
		return contentFrom(path, ContentFilters.NO_FILTER);
	}


	/**
	 * <p>
	 * contentFrom.
	 * </p>
	 *
	 * @param path a JAR or exploded-JAR directory
	 * @return a {@link ArchiveResourceSource}.
	 * 
	 * @see #sourcing(ArchiveResourceSource)
	 */
	@Fluent
	public static ArchiveResourceSource contentFrom(File path)
	{
		return contentFrom(path, ContentFilters.NO_FILTER);
	}


	/**
	 * <p>
	 * contentFrom.
	 * </p>
	 *
	 * @param path a JAR or exploded-JAR directory.
	 * @param filter a resource name filter (note: must not expect a leading slash).
	 * @return a {@link ArchiveResourceSource}.
	 * 
	 * @see #sourcing(ArchiveResourceSource)
	 */
	@Fluent
	public static ArchiveResourceSource contentFrom(File path, Predicate<String> filter)
	{
		return contentFrom(path.toPath(), filter);
	}


	/**
	 * <p>
	 * contentFrom.
	 * </p>
	 *
	 * @param path a JAR or exploded-JAR directory.
	 * @param filter a resource name filter (note: must not expect a leading slash).
	 * @return a {@link ArchiveResourceSource}.
	 * 
	 * @see #sourcing(ArchiveResourceSource)0
	 */
	@Fluent
	public static ArchiveResourceSource contentFrom(Path path, Predicate<String> filter)
	{
		JarInputStream inputStream = Exceptional.apply(ExplodedJarInputStream::jarInputStreamFrom, path);
		return contentFrom(inputStream, filter);
	}


	/**
	 * 
	 * @param inputStream a JAR or exploded-JAR directory.
	 * @param filter a resource name filter (note: must not expect a leading slash).
	 * @return a {@link ArchiveResourceSource}.
	 * 
	 * @see #sourcing(ArchiveResourceSource)
	 */
	@Fluent
	public static ArchiveResourceSource contentFrom(JarInputStream inputStream, Predicate<String> filter)
	{
		CheckedBiFunction<JarInputStream, Predicate<String>, ArchiveResourceSource, IOException> extract = new CheckedBiFunction<JarInputStream, Predicate<String>, ArchiveResourceSource, IOException>() {

			@Override
			public ArchiveResourceSource apply(JarInputStream t, Predicate<String> u) throws IOException
			{
				BasicArchiveResourceSource source = new BasicArchiveResourceSource();
				JarEntry entry;
				try(JarInputStream jin = inputStream) {
					while((entry = jin.getNextJarEntry()) != null) {
						if(!filter.test(entry.getName()) || entry.isDirectory()) {
							/* noop */
						} else if(entry instanceof ExplodedJarEntry) {
							source.with(entry.getName(), new FileInputStream(((ExplodedJarEntry) entry).path().toFile()));
						} else {
							source.with(entry.getName(), IoStreams.readAllBytes(jin));
						}
					}
				}
				return source;
			}
		};
		return Exceptional.apply(extract, inputStream, filter);
	}


	/**
	 * <p>
	 * toObjectModel.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.Archive} object.
	 */
	public abstract Archive toObjectModel();
}
