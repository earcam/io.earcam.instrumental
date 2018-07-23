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

import static java.lang.Integer.MAX_VALUE;
import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import io.earcam.unexceptional.Exceptional;

final class RecursiveDeleteVisitor extends SimpleFileVisitor<Path> {

	private static final EnumSet<FileVisitOption> NO_FOLLOW_SYMLINKS = EnumSet.noneOf(FileVisitOption.class);
	private static final RecursiveDeleteVisitor DELETE_VISITOR = new RecursiveDeleteVisitor();


	private RecursiveDeleteVisitor()
	{}


	/**
	 * <p>
	 * delete.
	 * </p>
	 *
	 * @param directory a {@link java.nio.file.Path} object.
	 */
	protected static void delete(Path directory)
	{
		Exceptional.accept(RecursiveDeleteVisitor::delete, directory, NO_FOLLOW_SYMLINKS);
	}


	private static void delete(Path directory, EnumSet<FileVisitOption> options) throws IOException
	{
		Files.walkFileTree(directory, options, MAX_VALUE, DELETE_VISITOR);
	}


	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	{
		Exceptional.accept(Files::delete, file);
		return CONTINUE;
	}


	@Override
	public FileVisitResult postVisitDirectory(Path directory, IOException exception)
	{
		Exceptional.accept(super::postVisitDirectory, directory, exception);
		Exceptional.accept(Files::delete, directory);
		return CONTINUE;
	}
}
