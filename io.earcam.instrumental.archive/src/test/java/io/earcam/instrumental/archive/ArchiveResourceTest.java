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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ArchiveResourceTest {

	@Nested
	class Equality {

		@Test
		void equalByNameAlone()
		{
			ArchiveResource a = new ArchiveResource("name", new ByteArrayInputStream(new byte[0]));
			ArchiveResource b = new ArchiveResource("name", new byte[] { 0, 1, 2, 3 });

			assertThat(a, is(equalTo(b)));
			assertThat(a.hashCode(), is(equalTo(b.hashCode())));

		}


		@Test
		void notEqualByNameAlone()
		{
			byte[] contents = new byte[] { 0, 1, 2, 3 };
			ArchiveResource a = new ArchiveResource("eman", contents);
			ArchiveResource b = new ArchiveResource("name", contents);

			assertThat(a, is(not(equalTo(b))));
		}


		@Test
		void notEqualToNull()
		{
			ArchiveResource a = new ArchiveResource("name", new byte[0]);
			ArchiveResource b = null;

			assertFalse(a.equals(b));
		}


		@Test
		void notEqualToNullObject()
		{
			ArchiveResource a = new ArchiveResource("name", new byte[0]);
			Object b = null;

			assertFalse(a.equals(b));
		}
	}

	@Nested
	public class Naming {

		@Test
		void extensionPresentGivenPathIsNonExistent()
		{
			ArchiveResource a = new ArchiveResource("name.ext", new byte[0]);

			assertThat(a.extension(), is(equalTo(".ext")));
		}


		@Test
		void extensionPresentGivenPathIsPresent()
		{
			ArchiveResource a = new ArchiveResource("/some/path/to/name.yay", new byte[0]);

			assertThat(a.extension(), is(equalTo(".yay")));
		}


		@Test
		void extensionNotPresentGivenPathIsNonExistent()
		{
			ArchiveResource a = new ArchiveResource("name", new byte[0]);

			assertThat(a.extension(), is(emptyString()));
		}


		@Test
		void extensionNotPresentGivenPathIsPresent()
		{
			ArchiveResource a = new ArchiveResource("/some/path/to/no-extension", new byte[0]);

			assertThat(a.extension(), is(emptyString()));
		}


		@Test
		void isClassThatIsNotQualfied()
		{
			ArchiveResource a = new ArchiveResource("Poor.class", new byte[0]);

			assertThat(a.isClass(), is(true));
		}


		@Test
		void isNotClassButIsQualified()
		{
			ArchiveResource a = new ArchiveResource("/something/probably/a/resource", new byte[0]);

			assertThat(a.isClass(), is(false));
		}


		@Test
		void isClassThatIsQualfied()
		{
			ArchiveResource a = new ArchiveResource("/com/acme/dummy/Fqn.class", new byte[0]);

			assertThat(a.isClass(), is(true));
		}


		@Test
		void isQualfiedClass()
		{
			ArchiveResource a = new ArchiveResource("/com/acme/dummy/Fqn.class", new byte[0]);

			assertThat(a.isQualifiedClass(), is(true));
		}


		@Test
		void isNotQualfiedClassButIsQualified()
		{
			ArchiveResource a = new ArchiveResource("/something/probably/a/resource", new byte[0]);

			assertThat(a.isQualifiedClass(), is(false));
		}


		@Test
		void isNotQualfiedClassButIsClass()
		{
			ArchiveResource a = new ArchiveResource("Poor.class", new byte[0]);

			assertThat(a.isQualifiedClass(), is(false));
		}
	}

	@Nested
	public class Renaming {

		@Test
		public void byteArrayArchiveResource()
		{
			String newName = "a/b/C.class";
			byte[] content = new byte[0];
			ArchiveResource a = ArchiveResource.rename(newName, new ArchiveResource("x/y/Z.class", content));

			assertThat(a.name(), is(equalTo(newName)));
			assertThat(a.bytes(), is(sameInstance(content)));
		}


		@Test
		public void inputStreamArchiveResource()
		{
			String newName = "a/b/C.class";
			InputStream content = new ByteArrayInputStream(new byte[0]);
			ArchiveResource a = ArchiveResource.rename(newName, new ArchiveResource("x/y/Z.class", content));

			assertThat(a.name(), is(equalTo(newName)));
			assertThat(a.inputStream(), is(sameInstance(content)));
		}
	}

	@Nested
	public class Backing {

		@Test
		public void aByteArrayBackedArchiveResourceHasKnownSize()
		{
			ArchiveResource a = new ArchiveResource("/com/acme/dummy/Fqn.class", new byte[42]);

			assertThat(a.unknownSize(), is(false));
			assertThat(a.knownSize(), is(42L));
		}


		@Test
		public void anInputStreamBackedArchiveResourceHasUnknownSize()
		{
			ArchiveResource a = new ArchiveResource("/com/acme/dummy/Fqn.class", new ByteArrayInputStream(new byte[101]));

			assertThat(a.unknownSize(), is(true));
			assertThat(a.knownSize(), is(-1L));
		}


		@Test
		public void givenAnInputStreamBackedArchiveResourceWhenBytesInvokedThenHasKnownSize()
		{
			ArchiveResource a = new ArchiveResource("/com/acme/dummy/Fqn.class", new ByteArrayInputStream(new byte[19]));

			a.bytes();

			assertThat(a.unknownSize(), is(false));
			assertThat(a.knownSize(), is(19L));
		}


		@Test
		public void byteArrayBackedArchiveResourceIsWritten()
		{
			String props = "run=forest\nend=all.good.things.must.come.to.an\nstart=a.fire.we.did.not";
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ArchiveResource a = new ArchiveResource("acme.properties", props.getBytes(UTF_8));

			a.write(output);

			String read = new String(output.toByteArray(), UTF_8);

			assertThat(read, is(equalTo(props)));
		}


		@Test
		public void inputStreamBackedArchiveResourceIsWritten()
		{
			String props = "run=forest\nend=all.good.things.must.come.to.an\nstart=a.fire.we.did.not";
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ArchiveResource a = new ArchiveResource("acme.properties", new ByteArrayInputStream(props.getBytes(UTF_8)));

			a.write(output);

			String read = new String(output.toByteArray(), UTF_8);

			assertThat(read, is(equalTo(props)));
		}
	}
}
