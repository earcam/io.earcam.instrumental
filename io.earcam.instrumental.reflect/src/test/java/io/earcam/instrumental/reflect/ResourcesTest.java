/*-
 * #%L
 * io.earcam.instrumental.reflect
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
 */package io.earcam.instrumental.reflect;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.io.FileMatchers.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.Immutable;

import org.junit.jupiter.api.Test;

import io.earcam.utilitarian.io.IoStreams;

public class ResourcesTest {

	@Test
	void classAsBytes() throws IOException
	{
		byte[] read = Resources.classAsBytes(ResourcesTest.class);

		String resource = ResourcesTest.class.getCanonicalName().replace('.', '/') + ".class";
		try(InputStream in = ResourcesTest.class.getClassLoader().getResourceAsStream(resource)) {
			byte[] expected = IoStreams.readAllBytes(in);

			assertThat(read, is(equalTo(expected)));
		}
	}


	@Test
	void sourceOfResourceIsAJar()
	{
		// EARCAM_SNIPPET_BEGIN: source-jar
		String resource = Resources.sourceOfResource(Immutable.class);

		assertThat(new File(resource), is(aReadableFile()));

		assertThat(resource, allOf(
				containsString("/com/google/code/findbugs/jsr305/"),
				endsWith(".jar")));
		// EARCAM_SNIPPET_END: source-jar
	}


	@Test
	void sourceOfResourceIsAClassFile()
	{
		// EARCAM_SNIPPET_BEGIN: source-dir
		String resource = Resources.sourceOfResource(ResourcesTest.class);

		assertThat(resource, new File(resource), is(anExistingDirectory()));

		assertThat(resource, endsWith("/target/test-classes/"));
		// EARCAM_SNIPPET_END: source-dir
	}
}
