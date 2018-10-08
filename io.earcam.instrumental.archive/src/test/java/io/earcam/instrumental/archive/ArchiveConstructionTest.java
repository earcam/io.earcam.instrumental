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

import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.INITIAL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class ArchiveConstructionTest {

	@Test
	void with() throws IOException
	{
		String name = "foo/bah/hum.bug";
		byte[] content = "fee fi foe thumb".getBytes(UTF_8);

		Path file = Paths.get(".", "target", UUID.randomUUID() + ".test-resource");
		Files.write(file, content);

		Archive archive = Archive.archive()
				.with(name, file)
				.toObjectModel();

		assertThat(archive.content(name).get().bytes(), is(equalTo(content)));

	}


	@Test
	void contentFrom() throws IOException
	{
		String name = "foo/bah/hum.bug";
		byte[] content = "fee fi foe thumb".getBytes(UTF_8);

		Path file = Paths.get(".", "target", UUID.randomUUID() + ".test-resource");
		Files.write(file, content);

		Archive archive = Archive.archive()
				.with(name, file)
				.toObjectModel();

		ArchiveResourceSource resourceSource = ArchiveConstruction.contentFrom(archive);

		List<ArchiveResource> resources = resourceSource.drain(INITIAL).collect(Collectors.toList());

		assertThat(resources, hasSize(1));

		assertThat(resources.get(0).name(), is(equalTo(name)));
		assertThat(resources.get(0).bytes(), is(equalTo(content)));
	}
}
