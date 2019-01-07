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

final class HeaderField {

	static final HeaderField FILE_NAME = new HeaderField(0, 100);
	static final HeaderField FILE_MODE = new HeaderField(100, 8);
	static final HeaderField UID = new HeaderField(108, 8);
	static final HeaderField GID = new HeaderField(116, 8);
	static final HeaderField FILE_SIZE = new HeaderField(124, 12);
	static final HeaderField MTIME = new HeaderField(136, 12);
	static final HeaderField CHECKSUM = new HeaderField(148, 8);
	static final HeaderField INDICATOR = new HeaderField(156, 1);
	static final HeaderField LINKED_FILE = new HeaderField(157, 100);

	// TODO below is fudge for not understanding difference between POSIX and GNU - see
	// eg.http://webcache.googleusercontent.com/search?q=cache:nQa1iB7dFJ4J:openlab.ring.gr.jp/tsuneo/soft/tar32_2/tar32_2/src/tar.h+&cd=4&hl=en&ct=clnk&gl=uk
	// fudge around null terminals: shorten magic and move version to the left
// static final FieldOffset USTAR_MAGIC = new FieldOffset(257, 6)
// static final FieldOffset USTAR_VERSION = new FieldOffset(263, 2)
	static final HeaderField USTAR_MAGIC = new HeaderField(257, 5);
	static final HeaderField USTAR_VERSION = new HeaderField(262, 2);

	static final HeaderField OWNER_NAME = new HeaderField(265, 32);
	static final HeaderField GROUP_NAME = new HeaderField(297, 32);
	static final HeaderField DEVICE_MAJOR = new HeaderField(329, 8);
	static final HeaderField DEVICE_MINOR = new HeaderField(337, 8);
	static final HeaderField PREFIX_FILE = new HeaderField(345, 155);

	final int offset;
	final int size;


	HeaderField(int offset, int size)
	{
		this.offset = offset;
		this.size = size;
	}
}
