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

import static io.earcam.instrumental.module.jpms.ExportModifier.MANDATED;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

/**
 * <p>
 * Export class.
 * </p>
 *
 */
@Immutable
public class Export implements Serializable {

	private static final long serialVersionUID = -4919749651818958350L;
	private static final transient String[] NO_MODULES = new String[0];
	private final String paquet;
	private final int access;
	private final String[] modules;


	/**
	 * <p>
	 * Constructor for Export.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param modules a {@link java.lang.String} object.
	 */
	public Export(String paquet, String... modules)
	{
		this(paquet, MANDATED.access(), modules);
	}


	/**
	 * <p>
	 * Constructor for Export.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param access a int.
	 * @param modules a {@link java.lang.String} object.
	 */
	public Export(String paquet, int access, String... modules)
	{
		this.paquet = paquet;
		this.access = access;
		this.modules = (modules == null) ? NO_MODULES : Arrays.copyOf(modules, modules.length);
	}


	/** {@inheritDoc} */
	@Override
	public boolean equals(Object other)
	{
		return other instanceof Export && equals((Export) other);
	}


	/**
	 * <p>
	 * equals.
	 * </p>
	 *
	 * @param that a {@link io.earcam.instrumental.module.jpms.Export} object.
	 * @return a boolean.
	 */
	public boolean equals(Export that)
	{
		return that != null
				&& Objects.equals(that.paquet(), paquet)
				&& that.access() == access
				&& Arrays.equals(that.modules(), modules);
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		return Objects.hash(paquet, access, Arrays.hashCode(modules));
	}


	/**
	 * <p>
	 * paquet.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String paquet()
	{
		return paquet;
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
	public Set<ExportModifier> modifiers()
	{
		return Access.modifiers(ExportModifier.class, access());
	}


	/**
	 * <p>
	 * modules.
	 * </p>
	 *
	 * @return an array of {@link java.lang.String} objects.
	 */
	public String[] modules()
	{
		return modules;
	}
}
