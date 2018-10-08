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

import static javax.lang.model.SourceVersion.RELEASE_7;
import static org.hamcrest.Matchers.is;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

public class AsReleaseJarTest {

	private static final class DummyAsReleaseJar implements AsReleaseJar {

		int capturedVersionOrdinal;


		@Override
		public void attach(ArchiveRegistrar core)
		{}


		@Override
		public AsReleaseJar release(int versionOrdinal, ArchiveConstruction archive)
		{

			this.capturedVersionOrdinal = versionOrdinal;
			return this;
		}
	}


	@Test
	public void defaultMethodUsesOrdinal()
	{
		DummyAsReleaseJar asReleaseJar = new DummyAsReleaseJar();

		asReleaseJar.release(RELEASE_7, null);

		MatcherAssert.assertThat(asReleaseJar.capturedVersionOrdinal, is(RELEASE_7.ordinal()));
	}
}
