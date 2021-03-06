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

import static io.earcam.instrumental.archive.Archive.archive;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.io.ByteArrayInputStream;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.lade.ClassLoaders;
import io.earcam.utilitarian.io.IoStreams;

public class DefaultArchiveTest {

	@Test
	public void transformerFunction()
	{
		Archive archive = archive()
				.with("something.txt", "something".getBytes(UTF_8))
				.toObjectModel();

		Archive transformed = archive.to(Function.identity());

		assertThat(transformed, is(sameInstance(archive)));
	}


	@Test
	public void addInputStreamEntry()
	{
		byte[] content = "something".getBytes(UTF_8);
		String name = "something.txt";

		byte[] bytes = archive()
				.with(name, new ByteArrayInputStream(content))
				.toByteArray();

		byte[] read = IoStreams.readAllBytes(ClassLoaders.inMemoryClassLoader().jar(bytes).getResourceAsStream(name));

		assertThat(read, is(equalTo(content)));
	}
}
