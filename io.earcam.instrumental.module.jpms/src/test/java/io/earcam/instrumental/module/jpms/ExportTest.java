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
import static io.earcam.instrumental.module.jpms.ExportModifier.SYNTHETIC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class ExportTest {

	private static final String PACKAGE = "com.acme.paquet";
	private static final String[] MODULES = { "mod.a", "mod.b", "mod.c" };

	private final Export export = new Export(PACKAGE, ACC_MANDATED, MODULES);


	@Test
	public void notEqualWhenPackageDiffers()
	{
		assertThat(export, is(not(equalTo(new Export("org.acme", ACC_MANDATED, MODULES)))));
	}


	@Test
	public void notEqualWhenAccessDiffers()
	{
		assertThat(export, is(not(equalTo(new Export(PACKAGE, 0, MODULES)))));
	}


	@Test
	public void notEqualWhenModulesDiffer()
	{
		assertThat(export, is(not(equalTo(new Export(PACKAGE, ACC_MANDATED, "mod.x", "mod.y", "mod.z")))));
	}


	@Test
	public void notEqualToNullType()
	{
		assertFalse(export.equals((Export) null));
	}


	@Test
	public void notEqualToNullObject()
	{
		assertFalse(export.equals((Object) null));
	}


	@Test
	public void modifierIsMappedFromAccess()
	{
		Export e = new Export(PACKAGE, ACC_SYNTHETIC, MODULES);

		assertThat(e.modifiers(), contains(SYNTHETIC));
	}


	@Test
	public void equal()
	{
		Export identical = new Export(PACKAGE, ACC_MANDATED, MODULES);
		assertThat(export, is(equalTo(identical)));
		assertThat(export.hashCode(), is(equalTo(identical.hashCode())));
	}
}
