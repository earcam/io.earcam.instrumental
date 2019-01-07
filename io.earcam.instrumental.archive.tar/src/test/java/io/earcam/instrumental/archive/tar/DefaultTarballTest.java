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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.tar.Tarball;
import io.earcam.utilitarian.io.IoStreams;

public class DefaultTarballTest {

	@Nested
	public class Write {
		@Test
		public void createSimple() throws Exception
		{
			Tarball tarball = Tarball.tar();
			Path tarFile = Paths.get(".", "target", "simple-created.tar");
			Path directory = Paths.get(".", "src", "test", "resources", "simple", ".");
			tarball.write(tarFile, directory);

			assertSameTarballContents(tarFile, directory);
		}


		@Test
		public void createSoftLinks() throws Exception
		{
			Tarball tarball = Tarball.tar();
			Path tarFile = Paths.get(".", "target", "soft-links-created.tar");
			Path directory = Paths.get(".", "src", "test", "resources", "soft-links");
			tarball.write(tarFile, directory);

			assertSameTarballContents(tarFile, directory);
		}


		@Test
		public void createHardLinks() throws Exception
		{
			Tarball tarball = Tarball.tar();
			Path tarFile = Paths.get(".", "target", "hard-links-created.tar");
			Path directory = Paths.get(".", "src", "test", "resources", "hard-links");
			tarball.write(tarFile, directory);

			assertSameTarballContents(tarFile, directory);
		}


		@Test
		public void createLinks() throws Exception
		{
			Tarball tarball = Tarball.tar();
			Path tarFile = Paths.get(".", "target", "links-created.tar");
			Path directory = Paths.get(".", "src", "test", "resources", "links");
			tarball.write(tarFile, directory);

			assertSameTarballContents(tarFile, directory);
		}


		@Test
		public void createRootfs() throws Exception
		{
			Tarball tarball = Tarball.tar();
			Path tarFile = Paths.get(".", "target", "rootfs-created.tar");

			Path orginalTar = Paths.get(".", "src", "test", "resources", "rootfs.tar");
			Path directory = Paths.get(".", "target", "rootfs-os-unpacked");

			osUntar(orginalTar, directory);

			tarball.write(tarFile, directory);

			assertSameTarballContents(tarFile, directory);
		}
	}


	private static void osUntar(Path tar, Path insideDirectory) throws IOException, InterruptedException
	{
		File cwd = insideDirectory.toFile();
		cwd.mkdirs();
		Process process = new ProcessBuilder("tar", "-xf", tar.toAbsolutePath().toString())
				.directory(cwd)
				.redirectErrorStream(true)
				.start();

		int exitCode = process.waitFor();
		String output = new String(IoStreams.readAllBytes(process.getInputStream()), UTF_8);

		assertThat(output, is(emptyString()));
		assertThat(exitCode, is(0));
	}


	/**
	 * Because there's no order guarantee from {@link Files#walkFileTree(Path, java.nio.file.FileVisitor)}
	 * we cannot simply compare the file bytes.
	 */
	private static void assertSameTarballContents(Path tarFile, Path directory) throws IOException, InterruptedException
	{
		Path cwd = (".".equals(directory.getFileName().toString())) ? directory.getParent() : directory;
		// @formatter:off
		Process process = new ProcessBuilder(
						"tar", 
						"--no-same-owner", 
						"--no-same-permissions", 
						"-f", tarFile.toAbsolutePath().toString(), 
						"-d")
				.directory(cwd.toFile())
				.redirectErrorStream(true)
				.start();
		// @formatter:on

		int exitCode = process.waitFor();
		String output = new String(IoStreams.readAllBytes(process.getInputStream()), UTF_8);

		assertThat(output, is(emptyString()));
		assertThat(exitCode, is(0));
	}


	// TODO I think this is a bug, but need suspect bug here due to bulk mode setting (should be granular), further
	// investigation required
	private static void assertSameTarballContentsWithHardDereference(Path tarFile, Path extracted) throws IOException, InterruptedException
	{
		Path target = Paths.get(".", "target");

		String name = tarFile.getFileName().toString();
		name = name.substring(0, name.length() - 4);
		Path unpacked = target.resolve(name);

		unpacked.toFile().mkdirs();

		Path cwd = unpacked;
		// @formatter:off
		Process process = new ProcessBuilder(
						"tar", 
						"--no-same-owner", 
						"--no-same-permissions", 
						"--hard-dereference", 
						"-xf", tarFile.toAbsolutePath().toString())
				.directory(cwd.toFile())
				.redirectErrorStream(true)
				.start();
		// @formatter:on

		int exitCode = process.waitFor();
		String output = new String(IoStreams.readAllBytes(process.getInputStream()), UTF_8);

		assertThat("precondition: no warnings on unpack", output, is(emptyString()));
		assertThat("precondition: no errors on unpack", exitCode, is(0));

		process = new ProcessBuilder("diff", "-r", extracted.toAbsolutePath().toString(), unpacked.toAbsolutePath().toString())
				.directory(cwd.toFile())
				.redirectErrorStream(true)
				.start();

		exitCode = process.waitFor();
		output = new String(IoStreams.readAllBytes(process.getInputStream()), UTF_8);

		assertThat(output, is(emptyString()));
		assertThat(exitCode, is(0));
	}

	@Nested
	public class Read {

		@Nested
		public class Extract {
			@Test
			public void extractSimple() throws Exception
			{
				Tarball tarball = Tarball.tar();
				Path tarFile = Paths.get(".", "src", "test", "resources", "simple.tar");
				Path extract = Paths.get(".", "target", "simple-extracted");
				tarball.read(tarFile, extract);

				assertSameTarballContents(tarFile, extract);
			}


			@Test
			public void extractSimpleInputStream() throws Exception
			{
				Tarball tarball = Tarball.tar();
				Path tarFile = Paths.get(".", "src", "test", "resources", "simple.tar");
				Path extract = Paths.get(".", "target", "simple-extracted-inputstream");
				tarball.read(new BufferedInputStream(new FileInputStream(tarFile.toFile())), extract);

				assertSameTarballContents(tarFile, extract);
			}


			@Test
			public void extractSoftLinks() throws Exception
			{
				Tarball tarball = Tarball.tar();
				Path tarFile = Paths.get(".", "src", "test", "resources", "soft-links.tar");
				Path extract = Paths.get(".", "target", "soft-links-extracted");
				tarball.read(tarFile, extract);

				assertSameTarballContents(tarFile, extract);
			}


			@Test
			public void extractHardLinks() throws Exception
			{
				Tarball tarball = Tarball.tar();
				Path tarFile = Paths.get(".", "src", "test", "resources", "hard-links.tar");
				Path extract = Paths.get(".", "target", "hard-links-extracted");
				tarball.read(tarFile, extract);

				assertSameTarballContents(tarFile, extract);
			}


			@Test
			public void extractLinks() throws Exception
			{
				Tarball tarball = Tarball.tar();
				Path tarFile = Paths.get(".", "src", "test", "resources", "links.tar");
				Path extract = Paths.get(".", "target", "links-extracted");
				tarball.read(tarFile, extract);

				assertSameTarballContents(tarFile, extract);
			}


			@Test
			public void extractRootfs() throws Exception
			{
				Tarball tarball = Tarball.tar();
				Path tarFile = Paths.get(".", "src", "test", "resources", "rootfs.tar");
				Path extract = Paths.get(".", "target", "rootfs-extracted");
				tarball.read(tarFile, extract);

				assertSameTarballContentsWithHardDereference(tarFile, extract);
			}


			@Test
			public void extractRootfsAsRoot() throws Exception
			{
				Assumptions.assumeTrue("root".equals(System.getProperty("user.name")));

				Tarball tarball = Tarball.tar();
				Path tarFile = Paths.get(".", "src", "test", "resources", "rootfs.tar");
				Path extract = Paths.get(".", "target", "rootfs-extracted-as-root");
				tarball.read(tarFile, extract);

				assertSameTarballContents(tarFile, extract);
			}
		}
	}
}