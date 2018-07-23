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

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

/**
 * <p>
 * Require class.
 * </p>
 *
 */
@Immutable
public class Require implements Serializable {

	private static final long serialVersionUID = 935948472668991696L;
	private final String module;
	private final int access;
	private final String version;


	/**
	 * <p>
	 * Constructor for Require.
	 * </p>
	 *
	 * @param module a {@link java.lang.String} object.
	 * @param access a int.
	 * @param version a {@link java.lang.String} object.
	 */
	public Require(String module, int access, String version)
	{
		this.module = module;
		this.access = access;
		this.version = version;
	}


	@Override
	public boolean equals(Object other)
	{
		return other instanceof Require && equals((Require) other);
	}


	/**
	 * <p>
	 * equals.
	 * </p>
	 *
	 * @param that a {@link io.earcam.instrumental.module.jpms.Require} object.
	 * @return a boolean.
	 */
	public boolean equals(Require that)
	{
		return that != null
				&& Objects.equals(that.module(), module)
				&& that.access() == access
				&& Objects.equals(that.version(), version);
	}


	@Override
	public int hashCode()
	{
		return Objects.hash(module, access, version);
	}


	/**
	 * <p>
	 * module.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String module()
	{
		return module;
	}


	/**
	 * <p>
	 * access.
	 * </p>
	 *
	 * @return a int.
	 */
	public int access()
	{
		return access;
	}


	/**
	 * <p>
	 * modifiers.
	 * </p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<RequireModifier> modifiers()
	{
		return Access.modifiers(RequireModifier.class, access());
	}


	/**
	 * <p>
	 * version.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String version()
	{
		return version;
	}
}
