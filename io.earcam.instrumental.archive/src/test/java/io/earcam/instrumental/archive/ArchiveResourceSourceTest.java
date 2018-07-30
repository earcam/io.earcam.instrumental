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

import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.FINAL;
import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.INITIAL;
import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.PRE_MANIFEST;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.function.Predicate.isEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Collections;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ArchiveResourceSourceTest {

	@Nested
	public class AWrappedCollection {

		final ArchiveResource resource = new ArchiveResource("name", "contents".getBytes(UTF_8));
		final ArchiveResourceSource wrapped = ArchiveResourceSource.wrap(Collections.singleton(resource));


		@Test
		void aWrappedCollectionOnlyDrainsAtTheInitialStage()
		{
			assertThat(wrapped.drain(INITIAL).anyMatch(isEqual(resource)), is(true));

			assertThat(wrapped.drain(PRE_MANIFEST).findAny().isPresent(), is(false));
			assertThat(wrapped.drain(FINAL).findAny().isPresent(), is(false));
		}


		@Test
		void aWrappedCollectionMayBeDrainedMoreThanOnce()
		{
			assertThat(wrapped.drain(INITIAL).anyMatch(isEqual(resource)), is(true));
			assertThat(wrapped.drain(INITIAL).anyMatch(isEqual(resource)), is(true));
		}
	}
}
