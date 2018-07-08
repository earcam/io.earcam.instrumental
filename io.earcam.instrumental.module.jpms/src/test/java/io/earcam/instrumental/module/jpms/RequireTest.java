/*-
 * #%L
 * io.earcam.instrumental.module.jpms
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
package io.earcam.instrumental.module.jpms;

import static io.earcam.instrumental.module.jpms.Access.ACC_MANDATED;
import static io.earcam.instrumental.module.jpms.Access.ACC_SYNTHETIC;
import static io.earcam.instrumental.module.jpms.Access.ACC_TRANSITIVE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class RequireTest {

	private static final String VERSION = "42.0.1";

	private static final String MODULE = "module";

	Require require = new Require(MODULE, ACC_MANDATED, VERSION);


	@Test
	public void notEqualWhenModuleDiffers()
	{
		assertThat(require, is(not(equalTo(new Require("an.other", ACC_MANDATED, VERSION)))));
	}


	@Test
	public void notEqualWhenAccessDiffers()
	{
		assertThat(require, is(not(equalTo(new Require(MODULE, 0, VERSION)))));
	}


	@Test
	public void notEqualWhenVersionDiffers()
	{
		assertThat(require, is(not(equalTo(new Require(MODULE, ACC_MANDATED, "1.0.1")))));
	}


	@Test
	public void notEqualToNullType()
	{
		assertFalse(require.equals((Require) null));
	}


	@Test
	public void notEqualToNullObject()
	{
		assertFalse(require.equals((Object) null));
	}


	@Test
	public void modifierIsMappedFromAccess()
	{
		Require r = new Require(MODULE, ACC_TRANSITIVE, VERSION);

		assertThat(r.modifiers(), contains(RequireModifier.TRANSITIVE));
	}


	@Test
	public void modifiersAreMappedFromAccess()
	{
		Require r = new Require(MODULE, ACC_TRANSITIVE | ACC_SYNTHETIC, VERSION);

		assertThat(r.modifiers(), containsInAnyOrder(RequireModifier.SYNTHETIC, RequireModifier.TRANSITIVE));
	}


	@Test
	public void equal()
	{
		Require identical = new Require(MODULE, ACC_MANDATED, VERSION);
		assertThat(require, is(equalTo(identical)));
		assertThat(require.hashCode(), is(equalTo(identical.hashCode())));
	}
}
