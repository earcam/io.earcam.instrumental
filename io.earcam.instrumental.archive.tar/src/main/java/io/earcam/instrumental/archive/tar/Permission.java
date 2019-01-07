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

import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collector.Characteristics.UNORDERED;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

public enum Permission {
	//@formatter:off
	SET_ON_EXEC_UID(04000),
	SET_ON_EXEC_GID(02000),
	  SET_ON_STICKY(01000),
	      USER_READ(00400),
	     USER_WRITE(00200),
	      USER_EXEC(00100),
	     GROUP_READ(00040),
	    GROUP_WRITE(00020),
	     GROUP_EXEC(00010),
	     OTHER_READ(00004),
	    OTHER_WRITE(00002),
	     OTHER_EXEC(00001);
	//@formatter:on

	final int value;


	Permission(int value)
	{
		this.value = value;
	}


	public static Set<Permission> from(int mode)
	{
		return EnumSet.allOf(Permission.class).stream()
				.filter(p -> (p.value & mode) == p.value)
				.collect(toEnumSet());
	}


	public static int to(Set<Permission> permissions)
	{
		return permissions.stream()
				.mapToInt(Permission::value)
				.reduce(0, (a, b) -> a | b);
	}


	public int value()
	{
		return value;
	}


	static Collector<Permission, EnumSet<Permission>, EnumSet<Permission>> toEnumSet()
	{
		return toEnumSet(Permission.class);
	}


	private static <T extends Enum<T>> Collector<T, EnumSet<T>, EnumSet<T>> toEnumSet(Class<T> type)
	{
		Supplier<EnumSet<T>> supplier = () -> EnumSet.noneOf(type);
		BiConsumer<EnumSet<T>, T> biConsumer = EnumSet::add;
		BinaryOperator<EnumSet<T>> binaryOperator = (left, right) -> {
			left.addAll(right);
			return left;
		};

		return Collector.of(
				supplier,
				biConsumer,
				binaryOperator,
				UNORDERED,
				IDENTITY_FINISH);
	}
}
