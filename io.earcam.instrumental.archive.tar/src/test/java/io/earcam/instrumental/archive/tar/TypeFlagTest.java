/*-
 * #%L
 * io.earcam.instrumental.archive.tarball
 * %%
 * Copyright (C) 2019 earcam
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
package io.earcam.instrumental.archive.tar;

import static io.earcam.instrumental.archive.tar.Header.ASCII_ZERO;
import static io.earcam.instrumental.archive.tar.Header.NULL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.tar.TypeFlag;

public class TypeFlagTest {

	// Place-holder test - currently, AFAIK, we're POSIX/GNU compatible on read (writing GNU compat)
	@Test
	public void normalFileEquivalence()
	{
		assertThat(TypeFlag.from(NULL), is(equalTo(TypeFlag.from(ASCII_ZERO))));
	}
}
