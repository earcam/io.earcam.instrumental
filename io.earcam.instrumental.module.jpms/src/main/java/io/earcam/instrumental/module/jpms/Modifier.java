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

import static java.util.Locale.ROOT;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * <p>
 * Modifier interface.
 * </p>
 *
 */
public interface Modifier<M extends Enum<M>> {

	/**
	 * <p>
	 * access.
	 * </p>
	 *
	 * @return a int.
	 */
	public abstract int access();


	/**
	 * <p>
	 * name.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String name();


	/**
	 * <p>
	 * inFlag.
	 * </p>
	 *
	 * @param flag a int.
	 * @return a boolean.
	 */
	public default boolean inFlag(int flag)
	{
		return (access() & flag) == access();
	}


	/**
	 * <p>
	 * sourceVisible.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public default boolean sourceVisible()
	{
		return true;
	}


	public static <M extends Enum<M> & Modifier<M>> Set<M> from(Class<M> type, String text)
	{
		EnumSet<M> set = EnumSet.noneOf(type);

		Arrays.stream(text.split("\\s+"))
				.map(e -> e.toUpperCase(ROOT))
				.map(n -> Enum.valueOf(type, n))
				.forEach(set::add);

		return set;
	}


	public static int access(Set<? extends Modifier<?>> modifiers)
	{
		return modifiers.stream()
				.filter(Access.EXCLUDE_NOT_REAL)
				.mapToInt(Modifier::access)
				.reduce(0, Access::bitwiseOr);
	}
}
