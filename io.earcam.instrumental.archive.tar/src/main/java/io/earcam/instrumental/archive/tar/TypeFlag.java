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

import java.util.EnumSet;
import java.util.NoSuchElementException;

public enum TypeFlag {

	//@formatter:off
	           NORMAL_FILE('0', TypeFlag.S_IFREG),
	             HARD_LINK('1', -1),
	         SYMBOLIC_LINK('2', TypeFlag.S_IFLNK),
	     CHARACTER_SPECIAL('3', TypeFlag.S_IFCHR),
	         BLOCK_SPECIAL('4', TypeFlag.S_IFBLK),
	             DIRECTORY('5', TypeFlag.S_IFDIR),
	                  FIFO('6', TypeFlag.S_IFIFO),
	       CONTIGUOUS_FILE('7', -1),
	GLOBAL_EXTENDED_HEADER('g', -1),
	       EXTENDED_HEADER('x', -1);
	//@formatter:on

	private static final int S_IFMT = 0170000; // bitmask for file type

	// private static final int S_IFSOCK = 0140000
	private static final int S_IFLNK = 0120000;
	private static final int S_IFREG = 0100000;
	private static final int S_IFBLK = 0060000;
	private static final int S_IFDIR = 0040000;
	private static final int S_IFCHR = 0020000;
	private static final int S_IFIFO = 0010000;

	final char flag;
	final int modeMask;


	TypeFlag(char flag, int modeMask)
	{
		this.flag = flag;
		this.modeMask = modeMask;
	}


	public static TypeFlag from(byte flag)
	{
		return flag == 0 ? NORMAL_FILE
				: EnumSet.allOf(TypeFlag.class).stream()
						.filter(e -> e.flag == flag)
						.findFirst()
						.orElseThrow(NoSuchElementException::new);
	}


	public static TypeFlag fromMode(int mode)
	{
		int mod = mode & S_IFMT;

		return EnumSet.allOf(TypeFlag.class).stream()
				.filter(e -> e.modeMask == mod)
				.findFirst()
				.orElseThrow(NoSuchElementException::new);
	}
}
