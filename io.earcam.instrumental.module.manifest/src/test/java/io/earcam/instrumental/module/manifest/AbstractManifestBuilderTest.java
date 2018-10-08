/*-
 * #%L
 * io.earcam.instrumental.module.manifest
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
package io.earcam.instrumental.module.manifest;

import static io.earcam.instrumental.module.manifest.ManifestInfoBuilder.attribute;
import static io.earcam.instrumental.module.manifest.ManifestInfoBuilder.ManifestNamedEntry.entry;
import static java.util.jar.Attributes.Name.IMPLEMENTATION_TITLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.earcam.unexceptional.Closing;

public class AbstractManifestBuilderTest {

	static final class TestManifestBuilder extends AbstractManifestBuilder<TestManifestBuilder> {

		public boolean hookWasInvoked;


		@Override
		protected TestManifestBuilder self()
		{
			return this;
		}


		@Override
		protected void preBuildHook()
		{
			hookWasInvoked = true;
		}


		@Override
		public boolean equals(AbstractManifestBuilder<?> that)
		{
			return same(that);
		}
	}

	@Nested
	class Equality {

		private TestManifestBuilder a = new TestManifestBuilder()
				.manifestMain(attribute(IMPLEMENTATION_TITLE, "Imps"))
				.manifestNamed(entry("com.acme.SomeThing").attribute("XYZ-Digest", "xyz=="));


		@Test
		void equal()
		{
			ManifestInfo b = new TestManifestBuilder()
					.manifestMain(attribute(IMPLEMENTATION_TITLE, "Imps"))
					.manifestNamed(entry("com.acme.SomeThing").attribute("XYZ-Digest", "xyz=="));

			assertThat(a, is(equalTo(b)));
			assertThat(a.hashCode(), is(equalTo(b.hashCode())));
		}


		@Test
		void notEqualWhenMainAttributesDiffer()
		{
			ManifestInfo b = new TestManifestBuilder()
					.manifestMain(attribute(IMPLEMENTATION_TITLE, "Something completely different"))
					.manifestNamed(entry("com.acme.SomeThing").attribute("XYZ-Digest", "xyz=="));

			assertThat(a, is(not(equalTo(b))));
		}


		@Test
		void notEqualWhenNamedEntriesDiffer()
		{
			ManifestInfo b = new TestManifestBuilder()
					.manifestMain(attribute(IMPLEMENTATION_TITLE, "Imps"))
					.manifestNamed(entry("com.acme.SomeThing").attribute("XYZ-Digest", "ABC=="));

			assertThat(a, is(not(equalTo(b))));
		}


		@Test
		void notEqualToNull()
		{
			TestManifestBuilder b = null;
			assertFalse(a.equals(b));
		}


		@Test
		void notEqualToNullObject()
		{
			Object b = null;
			assertFalse(a.equals(b));
		}


		@Test
		void notSameAsNullObject()
		{
			TestManifestBuilder b = null;
			assertFalse(a.same(b));
		}
	}

	@Nested
	class Properties {

		@Test
		void mainAttribute()
		{

			ManifestInfo info = new TestManifestBuilder()
					.manifestMain(attribute(IMPLEMENTATION_TITLE, "Imps"));

			assertThat(info.mainAttribute(IMPLEMENTATION_TITLE.toString()), is(equalTo("Imps")));
		}


		@Test
		void namedEntry()
		{

			ManifestInfo info = new TestManifestBuilder()
					.manifestNamed(entry("com.acme.SomeThing").attribute("XYZ-Digest", "xyz=="));

			assertThat(info.namedEntry("com.acme.SomeThing"), hasEntry(new Attributes.Name("XYZ-Digest"), "xyz=="));
		}
	}


	@Test
	void hookInvokedWhenToManifest()
	{
		TestManifestBuilder builder = new TestManifestBuilder();

		builder.mergeFrom(createManifest()).toManifest();

		assertThat(builder.hookWasInvoked, is(true));
	}


	@Test
	void hookInvokedWhenToOutputStream()
	{
		TestManifestBuilder builder = new TestManifestBuilder();

		builder.mergeFrom(createManifest()).to(new ByteArrayOutputStream());

		assertThat(builder.hookWasInvoked, is(true));
	}


	@Test
	void symmetricFromTo()
	{
		Manifest manifest = createManifest();

		TestManifestBuilder builder = new TestManifestBuilder();

		Manifest rehydrated = builder.mergeFrom(manifest).toManifest();

		assertThat(rehydrated, is(equalTo(manifest)));
	}


	private Manifest createManifest()
	{
		Manifest manifest = new Manifest();
		Attributes main = manifest.getMainAttributes();
		main.put(Attributes.Name.IMPLEMENTATION_TITLE, "A test");
		main.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		Attributes attributes = new Attributes();
		attributes.putValue("aka", "anon");
		manifest.getEntries().put("nom.de.plume", attributes);
		return manifest;
	}


	@Test
	void symmetricFromToOutputStream()
	{
		Manifest manifest = createManifest();

		TestManifestBuilder builder = new TestManifestBuilder();

		ByteArrayOutputStream noCloseOut = new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException
			{
				throw new IOException("open 24/7, 365");
			}
		};
		builder.mergeFrom(manifest).to(noCloseOut);

		ByteArrayInputStream in = new ByteArrayInputStream(noCloseOut.toByteArray());
		Manifest rehydrated = Closing.closeAfterApplying(in, Manifest::new);

		assertThat(rehydrated, is(equalTo(manifest)));
	}


	@Test
	void unhook()
	{
		Manifest manifest = createManifest();

		TestManifestBuilder builder = new TestManifestBuilder();
		builder.mergeFrom(manifest).to(new ByteArrayOutputStream());

		assertThat(builder.hooked, is(true));
		builder.unhook();
		assertThat(builder.hooked, is(false));
	}
}
