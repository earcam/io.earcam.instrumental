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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import javax.annotation.WillClose;

import io.earcam.utilitarian.io.CountedInputStream;
import io.earcam.utilitarian.io.CountedOutputStream;
import io.earcam.utilitarian.io.IoStreams;
import io.earcam.utilitarian.io.LimitedInputStream;

final class DefaultTarball implements Tarball {

	private static final int BLOCKING_FACTOR = 20;


	@Override
	public void write(@WillClose OutputStream outputTar, Path... sourceDirectories) throws IOException
	{
		AtomicInteger blocks = new AtomicInteger();

		Header header = new Header();

		try(CountedOutputStream output = new CountedOutputStream(outputTar)) {

			for(Path source : sourceDirectories) {

				FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
					{
						writeHeader(dir);
						return super.preVisitDirectory(dir, attrs);
					}


					private void writeHeader(Path path) throws IOException
					{
						output.write(header.from(source, path).toBytes());
						blocks.incrementAndGet();
					}


					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
					{
						writeHeader(file);
						if(header.indicator == TypeFlag.NORMAL_FILE && header.size > 0) {
							Files.copy(file, output);
							blocks.incrementAndGet();
							pad(output);
						}
						return super.visitFile(file, attrs);
					}


					private void pad(CountedOutputStream output) throws IOException
					{
						long written = output.count();
						int pad = (int) (512 - (written % 512));
						output.write(new byte[pad]);
					}
				};
				Files.walkFileTree(source, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, visitor);
			}

			byte[] emptyRecord = new byte[512];
			int writtenBlocks = blocks.get();
			int blocksToWrite = blocksToWrite(writtenBlocks);
			for(int i = 0; i < blocksToWrite; i++) {
				output.write(emptyRecord);
			}
		}
	}


	private int blocksToWrite(int writtenBlocks)
	{
		return (writtenBlocks < BLOCKING_FACTOR) ? BLOCKING_FACTOR - writtenBlocks
				: BLOCKING_FACTOR - (writtenBlocks % BLOCKING_FACTOR);
	}


	@Override
	public void read(@WillClose InputStream inputTar, BiConsumer<Header, InputStream> extractor) throws IOException
	{
		Header header = new Header();
		int emptyBlockCount = 0;
		try(CountedInputStream cis = new CountedInputStream(inputTar)) {
			while(emptyBlockCount < 2) {
				if(header.read(cis)) {
					LimitedInputStream contentStream = new LimitedInputStream(cis, header.size);
					extractor.accept(header, contentStream);

					// we now need to skip any remaining bytes in contentStream
					IoStreams.skipOrDie(cis, header.size - contentStream.count());

					long read = cis.count();
					int pad = (int) (512 - (read % 512));
					if(pad != 512) {
						IoStreams.skipOrDie(cis, pad);
					}
				} else {
					++emptyBlockCount;
				}
			}
		}
	}
}
