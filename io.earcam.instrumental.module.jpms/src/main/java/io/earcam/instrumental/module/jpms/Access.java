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

import static java.util.EnumSet.noneOf;
import static java.util.stream.Collectors.toCollection;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;

final class Access {

	/** Constant <code>ACC_NOT_REAL=-42</code> */
	public static final int ACC_NOT_REAL = -42;

	/** Constant <code>ACC_MODULE=0x8000</code> */
	public static final int ACC_MODULE = 0x8000;
	/** Constant <code>ACC_OPEN=0x0020</code> */
	public static final int ACC_OPEN = 0x0020;
	/** Constant <code>ACC_TRANSITIVE=0x0020</code> */
	public static final int ACC_TRANSITIVE = 0x0020;
	/** Constant <code>ACC_STATIC_PHASE=0x0040</code> */
	public static final int ACC_STATIC_PHASE = 0x0040;
	/** Constant <code>ACC_SYNTHETIC=0x1000</code> */
	public static final int ACC_SYNTHETIC = 0x1000;
	/** Constant <code>ACC_MANDATED=0x8000</code> */
	public static final int ACC_MANDATED = 0x8000;

	private static final Predicate<Modifier> EXCLUDE_NOT_REAL = m -> m.access() != ACC_NOT_REAL;


	private Access()
	{}


	/**
	 * <p>
	 * access.
	 * </p>
	 *
	 * @param modifiers a {@link java.util.Set} object.
	 * @return a int.
	 */
	public static int access(Set<? extends Modifier> modifiers)
	{
		return modifiers.stream()
				.filter(EXCLUDE_NOT_REAL)
				.mapToInt(Modifier::access)
				.reduce(0, Access::bitwiseOr);
	}


	/**
	 * <p>
	 * bitwiseOr.
	 * </p>
	 *
	 * @param a a int.
	 * @param b a int.
	 * @return a int.
	 */
	public static int bitwiseOr(int a, int b)
	{
		return a | b;
	}


	/**
	 * <p>
	 * modifiers.
	 * </p>
	 *
	 * @param modifierType a {@link java.lang.Class} object.
	 * @param access a int.
	 * @param <M> a M object.
	 * @return a {@link java.util.Set} object.
	 */
	public static <M extends Enum<M> & Modifier> Set<M> modifiers(Class<M> modifierType, int access)
	{
		return EnumSet.allOf(modifierType).stream()
				.filter(EXCLUDE_NOT_REAL)
				.filter(m -> m.inFlag(access))
				.collect(toEnumSetOf(modifierType));
	}


	// This should be on Collectors...
	private static <E extends Enum<E>> Collector<E, ?, EnumSet<E>> toEnumSetOf(Class<E> enumType)
	{
		return toCollection(() -> noneOf(enumType));
	}
}
