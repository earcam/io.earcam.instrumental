/*-
 * #%L
 * io.earcam.instrumental.archive.tarball
 * %%
 * Copyright (C) 2019 earcam
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
package io.earcam.instrumental.archive.tar;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import javax.annotation.WillClose;

import io.earcam.unexceptional.Exceptional;

public interface Tarball {

	public static Tarball tar()
	{
		return new DefaultTarball();
	}


	/**
	 * If a sourceDirectory is specified with a trailing period '.'
	 * e.g. {@code a/b/c/.}, then the root will be {@code c}
	 * Otherwise the directory's contents will be added to the root.
	 * 
	 * @param outputTarFile
	 * @param sourceDirectories
	 * @throws IOException
	 */
	public default void write(Path outputTarFile, Path... sourceDirectories) throws IOException
	{
		write(new FileOutputStream(outputTarFile.toFile()), sourceDirectories);
	}


	public abstract void write(@WillClose OutputStream outputTar, Path... sourceDirectories) throws IOException;

	public class FilesystemExtractor implements BiConsumer<Header, InputStream> {

		protected final Path explodeToDirectory;
		protected final CopyOption[] copyOptions;


		public FilesystemExtractor(Path explodeToDirectory)
		{
			this(explodeToDirectory, StandardCopyOption.REPLACE_EXISTING);
		}


		public FilesystemExtractor(Path explodeToDirectory, CopyOption... copyOptions)
		{
			this.explodeToDirectory = explodeToDirectory;
			this.copyOptions = copyOptions;
			explodeToDirectory.toFile().mkdirs();
		}


		@Override
		public void accept(Header header, InputStream contentStream)
		{
			Exceptional.accept(this::doAccept, header, contentStream);
		}


		protected void doAccept(Header header, InputStream contentStream) throws IOException
		{
			Path output = explodeToDirectory.resolve(header.filename);
			if(header.indicator == TypeFlag.DIRECTORY) {
				output.toFile().mkdirs();
			} else if(header.indicator == TypeFlag.SYMBOLIC_LINK) {
				Files.deleteIfExists(output);
				Files.createSymbolicLink(output, Paths.get(header.linkedFilename));

			} else if(header.indicator == TypeFlag.HARD_LINK) {
				Files.deleteIfExists(output);
				Files.createLink(output, explodeToDirectory.resolve(header.linkedFilename));

			} else {
				// TODO assert header.indicator == TypeFlag.NORMAL_FILE
				Files.copy(contentStream, output, copyOptions);
			}

			try {
				Files.setAttribute(output, "unix:mode", Permission.to(header.permissions), LinkOption.NOFOLLOW_LINKS);
			} catch(FileSystemException e) {
				Exceptional.swallow(e);
			}
			try {
				Files.setAttribute(output, "lastModifiedTime", FileTime.from(header.lastModified, TimeUnit.SECONDS), LinkOption.NOFOLLOW_LINKS);
			} catch(FileSystemException e) {
				Exceptional.swallow(e);
			}

			try {
				Files.setAttribute(output, "unix:uid", header.uid, LinkOption.NOFOLLOW_LINKS);
				Files.setAttribute(output, "unix:gid", header.gid, LinkOption.NOFOLLOW_LINKS);
			} catch(FileSystemException e) {
				Exceptional.swallow(e);
			}
		}

	}


	public default void read(Path inputTarFile, Path explodeToDirectory) throws IOException
	{
		read(inputTarFile, new FilesystemExtractor(explodeToDirectory));
	}


	public default void read(Path inputTarFile, BiConsumer<Header, InputStream> extractor) throws IOException
	{
		read(new BufferedInputStream(new FileInputStream(inputTarFile.toFile())), extractor);
	}


	public default void read(@WillClose InputStream inputTar, Path explodeToDirectory) throws IOException
	{
		read(inputTar, new FilesystemExtractor(explodeToDirectory));
	}


	public abstract void read(@WillClose InputStream inputTar, BiConsumer<Header, InputStream> extractor) throws IOException;
}
