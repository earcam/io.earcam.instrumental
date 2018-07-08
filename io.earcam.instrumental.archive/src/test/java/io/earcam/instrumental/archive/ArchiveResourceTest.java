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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

public class ArchiveResourceTest {

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
}
