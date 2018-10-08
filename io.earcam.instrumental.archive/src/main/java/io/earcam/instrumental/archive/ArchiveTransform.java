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

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.SYNC;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.function.Function;

import javax.annotation.WillClose;

import io.earcam.unexceptional.Exceptional;

public interface ArchiveTransform {

	/**
	 * <p>
	 * toByteArray.
	 * </p>
	 *
	 * @return an array of {@link byte} objects.
	 */
	public default byte[] toByteArray()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		to(baos);
		return baos.toByteArray();
	}


	/**
	 * @return an InputStream (that may be wrapped in a JarInputStream).
	 */
	public default InputStream toInputStream()
	{
		return new ByteArrayInputStream(toByteArray());
	}


	public abstract <R> R to(Function<Archive, R> transformer);


	/**
	 * <p>
	 * to.
	 * </p>
	 *
	 * @param os a {@link java.io.OutputStream} object.
	 */
	public abstract void to(@WillClose OutputStream os);


	/**
	 * Write an archive file
	 * Invokes {@link #to(Path, OpenOption...)} with {@link java.nio.file.StandardOpenOption#CREATE},
	 * {@link java.nio.file.StandardOpenOption#TRUNCATE_EXISTING} and {@link java.nio.file.StandardOpenOption#SYNC}
	 *
	 * @param archive the {@link java.nio.file.Path} to write the archive to
	 * @return a {@link java.nio.file.Path} object.
	 * 
	 * @see #to(Path, OpenOption...)
	 * @see #explodeTo(Path)
	 * @see #explodeTo(Path, boolean)
	 */
	public default Path to(Path archive)
	{
		return to(archive, CREATE, TRUNCATE_EXISTING, SYNC);
	}


	/**
	 * Write an archive file
	 *
	 * @param archive the {@link java.nio.file.Path} to write the archive to
	 * @param options optional {@link java.nio.file.OpenOption}s
	 * @return a {@link java.nio.file.Path} object.
	 *
	 * @see #to(Path)
	 * @see #explodeTo(Path)
	 * @see #explodeTo(Path, boolean)
	 */
	public default Path to(Path archive, OpenOption... options)
	{
		archive.getParent().toFile().mkdirs();
		OutputStream outputStream = Exceptional.apply(Files::newOutputStream, archive, options);
		to(outputStream);
		return archive;
	}


	/**
	 * Explodes to directory, overwriting by first deleting if exists
	 *
	 * @param directory a {@link java.nio.file.Path} object.
	 * @return the {@code directory} parameter
	 */
	public default Path explodeTo(Path directory)
	{
		return explodeTo(directory, false);
	}


	/**
	 * <p>
	 * explodeTo.
	 * </p>
	 *
	 * @param directory a {@link java.nio.file.Path} object.
	 * @param merge a boolean.
	 * @return a {@link java.nio.file.Path} object.
	 */
	public abstract Path explodeTo(Path directory, boolean merge);
}
