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

import static io.earcam.instrumental.archive.ArchiveConstruction.contentFrom;
import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.Hamcrest.present;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.io.FileMatchers.anExistingFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class ArchiveBuilderTest {

	final Path baseDir = Paths.get(".", "target", getClass().getCanonicalName(), UUID.randomUUID().toString());


	@Test
	public void emptyArchive()
	{
		Archive archive = archive().toObjectModel();

		assertThat(archive.contents(), is(empty()));
		assertThat(archive.manifest(), is(not(present())));
	}


	@Test
	public void singleContent()
	{
		String name = "/path/to/some.file";
		byte[] contents = bytes("Some Content");

		Archive archive = archive()
				.with(name, contents)
				.toObjectModel();

		assertThat(archive.contents(), hasSize(1));
		assertThat(archive.contents(), contains(new ArchiveResource(name, contents)));

		assertThat(archive.manifest(), is(not(present())));
	}


	private static byte[] bytes(String string)
	{
		return string.getBytes(UTF_8);
	}


	@Test
	public void sourcingContentFrom()
	{
		Path dir = baseDir.resolve("sourcingContentFrom");

		String name = "/path/to/a/file.txt";
		byte[] contents = bytes("Hey, how ya doin'?");

		Path aJar = archive()
				.with(name, contents)
				.to(dir.resolve("a.jar"));

		Path bJar = archive()
				.with(name, bytes("You shall not pass"))
				.to(dir.resolve("b.jar"));

		File cJar = archive()
				.with("another.txt", bytes("May thee fare well"))
				.explodeTo(dir.resolve("c.jar")).toFile();

		Archive sourced = archive()
				.sourcing(contentFrom(aJar))
				.sourcing(contentFrom(bJar, n -> !ArchiveResource.sameName(n, name)))
				.sourcing(contentFrom(cJar))
				.with("other.txt", bytes("other"))
				.toObjectModel();

		assertThat(sourced.content(name).get().bytes(), is(equalTo(contents)));
		assertThat(sourced.content("another.txt").get().bytes(), is(equalTo(bytes("May thee fare well"))));
		assertThat(sourced.content("other.txt").get().bytes(), is(equalTo(bytes("other"))));
	}


	@Test
	public void explodeToExistingDirectoryWithoutMerging() throws IOException
	{
		Path dir = baseDir.resolve("explodeToExistingDirectoryWithoutMerging");
		Path subdir = dir.resolve("sub");
		subdir.toFile().mkdirs();

		Path preexisting = subdir.resolve("existing.txt");
		Files.write(preexisting, bytes("existing"), CREATE);

		archive()
				.with("something.txt", bytes("something"))
				.explodeTo(dir);

		assertThat(preexisting.toFile(), is(not(anExistingFile())));
	}


	@Test
	public void explodeToExistingDirectoryByMerging() throws IOException
	{
		Path dir = baseDir.resolve("explodeToExistingDirectoryByMerging");
		Path subdir = dir.resolve("sub");
		subdir.toFile().mkdirs();

		Path preexisting = subdir.resolve("preexisting.txt");
		Files.write(preexisting, bytes("existing"), CREATE);

		archive()
				.with("something.txt", bytes("something"))
				.explodeTo(dir, true);

		assertThat(preexisting.toFile(), is(anExistingFile()));
	}


	@Test
	public void filterChangesContent()
	{
		ArchiveResourceFilter filter = r -> {
			if("something.txt".equals(r.name())) {
				return new ArchiveResource("something.txt", bytes("anotherthing"));
			}
			return r;
		};

		Archive archive = archive()
				.configured(a -> a.registerResourceFilter(filter))
				.with("different.txt", bytes("something"))
				.with("something.txt", bytes("something"))
				.toObjectModel();

		assertThat(archive.content("something.txt").get().bytes(), is(equalTo(bytes("anotherthing"))));
	}


	@Test
	public void filterAddedInConfigurationExcludesResource()
	{
		ArchiveResourceFilter filter = r -> {
			if("something.txt".equals(r.name())) {
				return null;
			}
			return r;
		};

		Archive archive = archive()
				.configured(a -> a.registerResourceFilter(filter))
				.with("different.txt", bytes("something"))
				.with("something.txt", bytes("something"))
				.toObjectModel();

		assertThat(archive.content("something.txt").isPresent(), is(false));
		assertThat(archive.content("different.txt").isPresent(), is(true));
	}


	@Test
	public void filterByExcludesResource()
	{
		ArchiveResourceFilter filter = r -> {
			if("something.txt".equals(r.name())) {
				return null;
			}
			return r;
		};

		Archive archive = archive()
				.with("different.txt", bytes("something"))
				.with("something.txt", bytes("something"))
				.filteredBy(filter)
				.toObjectModel();

		assertThat(archive.content("something.txt").isPresent(), is(false));
		assertThat(archive.content("different.txt").isPresent(), is(true));
	}


	@Test
	public void filterChangesContentListenerInformedAfterTheFact()
	{
		ArchiveResourceFilter filter = r -> {
			if("something.txt".equals(r.name())) {
				return new ArchiveResource("something.txt", bytes("anotherthing"));
			}
			return r;
		};

		AtomicReference<ArchiveResource> heard = new AtomicReference<ArchiveResource>();
		ArchiveResourceListener listener = heard::set;

		archive()
				.configured(a -> a.registerResourceFilter(filter))
				.configured(a -> a.registerResourceListener(listener))
				.with("something.txt", bytes("something"))
				.toObjectModel();

		assertThat(heard.get().name(), is(equalTo("something.txt")));
		assertThat(heard.get().bytes(), is(equalTo(bytes("anotherthing"))));
	}


	@Test
	public void transformerFunction()
	{
		Archive archive = archive()
				.with("something.txt", bytes("something"))
				.to(Function.identity());

		assertThat(archive.content("something.txt").get().bytes(), is(equalTo(bytes("something"))));
	}
}
