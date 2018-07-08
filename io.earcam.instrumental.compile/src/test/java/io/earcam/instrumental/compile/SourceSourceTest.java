/*-
 * #%L
 * io.earcam.instrumental.compile
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
package io.earcam.instrumental.compile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.io.FileMatchers.*;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.compile.SourceSource.SourceSink;

public class SourceSourceTest {

	static class DummySink implements SourceSink {

		final List<File> paths = new ArrayList<>();
		final List<String> texts = new ArrayList<>();


		public void unblock()
		{
			paths.clear();
			texts.clear();
		}


		@Override
		public void sink(Path path)
		{
			paths.add(path.toFile());
		}


		@Override
		public void sink(String text)
		{
			texts.add(text);
		}

	}


	@Test
	void findsLocalProjectTestSource()
	{
		Class<?> type = SourceSourceTest.class;

		DummySink sink = new DummySink();

		SourceSource.foundFor(type).drain(sink);

		assertThat(sink.paths, contains(aFileNamed(endsWith(type.getSimpleName() + ".java"))));
	}


	@Test
	void findsLocalProjectMainSource()
	{
		Class<?> type = SourceSource.class;

		DummySink sink = new DummySink();

		SourceSource.foundFor(type).drain(sink);

		assertThat(sink.paths, contains(aFileNamed(endsWith(type.getSimpleName() + ".java"))));
	}


	@Test
	void findsMavenDependencySource()
	{
		Class<?> type = Test.class;

		DummySink sink = new DummySink();

		SourceSource.foundFor(type).drain(sink);

		assertThat(sink.texts, contains(allOf(
				containsString("package org.junit.jupiter.api;"),
				containsString("public @interface Test"))));
	}


	@Test
	void failsFastWhenDoesNotFindJdkSource()
	{
		Class<?> type = String.class;

		try {
			SourceSource.foundFor(type);
			fail();
		} catch(Exception e) {}
	}


	@Test
	void failsFastWhenSourceFileDoesNotExist()
	{
		try {
			SourceSource.from(
					Paths.get(".", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()));
			fail();
		} catch(UncheckedIOException e) {}
	}
}
