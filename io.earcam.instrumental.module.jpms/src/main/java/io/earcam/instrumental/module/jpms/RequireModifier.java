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

/**
 * <p>
 * RequireModifier class.
 * </p>
 *
 */
public enum RequireModifier implements Modifier {

	TRANSITIVE(Access.ACC_TRANSITIVE, true), //
	STATIC(Access.ACC_STATIC_PHASE, true),   //
	SYNTHETIC(Access.ACC_SYNTHETIC, false),  //
	MANDATED(Access.ACC_MANDATED, false);    // implicitly added; i.e. Java base

	private int access;
	private boolean sourceVisible;


	private RequireModifier(int access, boolean sourceVisible)
	{
		this.access = access;
		this.sourceVisible = sourceVisible;
	}


	/** {@inheritDoc} */
	@Override
	public int access()
	{
		return access;
	}


	/** {@inheritDoc} */
	@Override
	public boolean sourceVisible()
	{
		return sourceVisible;
	}
}
