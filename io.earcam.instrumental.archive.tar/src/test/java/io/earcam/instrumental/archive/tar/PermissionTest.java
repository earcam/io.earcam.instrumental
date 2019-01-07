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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PermissionTest {

	@Test
	public void toEnumSetForceTestCoverageOfTheBinaruOperatorCombiner()
	{
		List<Permission> alotOf = new ArrayList<Permission>();
		for(int i = 0; i < 1_000; i++) {
			alotOf.addAll(EnumSet.allOf(Permission.class));
		}
		EnumSet<Permission> collected = alotOf.stream().parallel().collect(Permission.toEnumSet());

		assertThat(collected, is(equalTo(EnumSet.allOf(Permission.class))));
	}
}
