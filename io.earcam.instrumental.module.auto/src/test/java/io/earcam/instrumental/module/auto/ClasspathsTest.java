/*-
 * #%L
 * io.earcam.instrumental.module.auto
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
package io.earcam.instrumental.module.auto;

import static io.earcam.instrumental.module.auto.Classpaths.allClasspaths;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ClasspathsTest {

	@Test
	void classpathContains()
	{
		assertTrue(
				allClasspaths()
						.filter(cp -> cp.toString().contains("junit") || cp.toString().contains("instrumental"))
						.findAny()
						.isPresent());
	}
}
