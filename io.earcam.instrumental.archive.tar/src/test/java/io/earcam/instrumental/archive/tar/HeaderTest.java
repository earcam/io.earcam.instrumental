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

import static io.earcam.instrumental.archive.tar.Header.HEADER_SIZE;
import static io.earcam.instrumental.archive.tar.HeaderField.INDICATOR;
import static io.earcam.instrumental.archive.tar.HeaderField.LINKED_FILE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.tar.Header;
import io.earcam.instrumental.archive.tar.TypeFlag;

public class HeaderTest {

	@Nested
	public class FromFile {

		@Test
		public void regularFile() throws IOException
		{
			Header header = new Header();

			Path baseDir = Paths.get("src", "test", "resources");
			Path path = baseDir.resolve(Paths.get("links", "a", "b", "c", "e.txt"));

			header.from(baseDir, path);

			assertThat(header.filename, is(equalTo("links/a/b/c/e.txt")));
			assertThat(header.indicator, is(TypeFlag.NORMAL_FILE));
			assertThat(header.size, is(equalTo(Files.size(path))));
			assertThat(header.linkedFilename, is(emptyString()));
			assertThat(header.lastModified, is(equalTo(Files.getLastModifiedTime(path).to(SECONDS))));
			assertThat(header.filenamePrefix, is(emptyString()));
			assertThat(header.inodes, is(aMapWithSize(1)));
		}


		@Test
		public void directory() throws IOException
		{
			Header header = new Header();

			Path baseDir = Paths.get("src", "test", "resources");
			Path path = baseDir.resolve(Paths.get("links", "a", "b"));

			header.from(baseDir, path);

			assertThat(header.filename, is(equalTo("links/a/b/")));
			assertThat(header.indicator, is(TypeFlag.DIRECTORY));
			assertThat(header.size, is(0L));
			assertThat(header.linkedFilename, is(emptyString()));
			assertThat(header.lastModified, is(equalTo(Files.getLastModifiedTime(path).to(SECONDS))));
			assertThat(header.filenamePrefix, is(emptyString()));
			assertThat(header.inodes, is(aMapWithSize(1)));
		}


		@Test
		public void softlink() throws IOException
		{
			Header header = new Header();

			Path baseDir = Paths.get("src", "test", "resources");
			Path path = baseDir.resolve(Paths.get("links", "soft-link-to-d.txt"));

			header.from(baseDir, path);

			assertThat(header.filename, is(equalTo("links/soft-link-to-d.txt")));
			assertThat(header.indicator, is(TypeFlag.SYMBOLIC_LINK));
			assertThat(header.size, is(0L));
			assertThat(header.linkedFilename, is(equalTo("a/b/c/d.txt")));
			assertThat(header.lastModified, is(equalTo(Files.getLastModifiedTime(path, NOFOLLOW_LINKS).to(SECONDS))));
			assertThat(header.filenamePrefix, is(emptyString()));
			assertThat(header.inodes, is(aMapWithSize(1)));
		}


		@Test
		public void hardlink() throws IOException
		{
			Header header = new Header();

			Path baseDir = Paths.get("src", "test", "resources");
			Path path = baseDir.resolve(Paths.get("links", "hard-link-to-d.txt"));

			header.from(baseDir, path);

			path = baseDir.resolve(Paths.get("links", "a", "b", "c", "d.txt"));

			header.from(baseDir, path);

			assertThat(header.filename, is(equalTo("links/a/b/c/d.txt")));
			assertThat(header.indicator, is(TypeFlag.HARD_LINK));
			assertThat(header.size, is(0L));
			assertThat(header.linkedFilename, is(equalTo("links/hard-link-to-d.txt")));
			assertThat(header.lastModified, is(equalTo(Files.getLastModifiedTime(path, NOFOLLOW_LINKS).to(SECONDS))));
			assertThat(header.filenamePrefix, is(emptyString()));
			assertThat(header.inodes, is(aMapWithSize(1)));
		}
	}

	@Nested
	public class ToBytes {

		@Test
		public void regularFile() throws IOException
		{
			Header header = new Header();

			Path baseDir = Paths.get("src", "test", "resources");
			String fileName = "links/a/b/c/e.txt";
			Path path = baseDir.resolve(fileName);

			byte[] bytes = header.from(baseDir, path).toBytes();
			String fullOfNulls = new String(bytes, UTF_8);

			assertThat(fullOfNulls.length(), is(HEADER_SIZE));
			assertThat(fullOfNulls.substring(0, fileName.length()), is(equalTo(fileName)));

			assertThat(fullOfNulls.charAt(INDICATOR.offset), is(equalTo(TypeFlag.NORMAL_FILE.flag)));
		}


		@Test
		public void directory() throws IOException
		{
			Header header = new Header();

			Path baseDir = Paths.get("src", "test", "resources");
			String fileName = "links/a/b";
			Path path = baseDir.resolve(fileName);

			byte[] bytes = header.from(baseDir, path).toBytes();
			String fullOfNulls = new String(bytes, UTF_8);

			assertThat(fullOfNulls.length(), is(HEADER_SIZE));
			assertThat(fullOfNulls.substring(0, fileName.length()), is(equalTo(fileName)));

			assertThat(fullOfNulls.charAt(INDICATOR.offset), is(equalTo(TypeFlag.DIRECTORY.flag)));
		}


		@Test
		public void softlink() throws IOException
		{
			Header header = new Header();

			Path baseDir = Paths.get("src", "test", "resources");
			String fileName = "links/soft-link-to-d.txt";
			Path path = baseDir.resolve(fileName);

			byte[] bytes = header.from(baseDir, path).toBytes();
			String fullOfNulls = new String(bytes, UTF_8);

			assertThat(fullOfNulls.length(), is(HEADER_SIZE));
			assertThat(fullOfNulls.substring(0, fileName.length()), is(equalTo(fileName)));

			assertThat(fullOfNulls.charAt(INDICATOR.offset), is(equalTo(TypeFlag.SYMBOLIC_LINK.flag)));
		}


		@Test
		public void hardlink() throws IOException
		{
			Header header = new Header();

			Path baseDir = Paths.get("src", "test", "resources");
			String linkTarget = "links/hard-link-to-d.txt";
			Path firstPath = baseDir.resolve(linkTarget);

			String fileName = "links/a/b/c/d.txt";
			Path path = baseDir.resolve(fileName);

			// HACK: we need to ensure hardlink exists on filesystem after git checkout
			// FIXME: resolve by replacing src/test/resources with dynamically generated content in target
			Files.delete(firstPath);
			Files.createLink(firstPath, path);

			header.from(baseDir, firstPath);

			byte[] bytes = header.from(baseDir, path).toBytes();
			String fullOfNulls = new String(bytes, UTF_8);

			assertThat(fullOfNulls.length(), is(HEADER_SIZE));
			assertThat(fullOfNulls.substring(0, fileName.length()), is(equalTo(fileName)));

			String extractedLink = new String(bytes, LINKED_FILE.offset, linkTarget.length(), StandardCharsets.UTF_8);
			assertThat(extractedLink, is(equalTo(linkTarget)));

			assertThat(fullOfNulls.charAt(INDICATOR.offset), is(equalTo(TypeFlag.HARD_LINK.flag)));
		}
	}

	@Nested
	public class FailsWhen {

		@Test
		void cannotReaderHeaderSize() throws Exception
		{
			new Header().read(new ByteArrayInputStream(new byte[Header.HEADER_SIZE]));

			try {
				new Header().read(new ByteArrayInputStream(new byte[Header.HEADER_SIZE - 1]));
				fail();
			} catch(IllegalStateException e) {}
		}
	}
}