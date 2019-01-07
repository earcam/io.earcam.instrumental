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

import static io.earcam.instrumental.archive.tar.HeaderField.CHECKSUM;
import static io.earcam.instrumental.archive.tar.HeaderField.DEVICE_MAJOR;
import static io.earcam.instrumental.archive.tar.HeaderField.DEVICE_MINOR;
import static io.earcam.instrumental.archive.tar.HeaderField.FILE_MODE;
import static io.earcam.instrumental.archive.tar.HeaderField.FILE_NAME;
import static io.earcam.instrumental.archive.tar.HeaderField.FILE_SIZE;
import static io.earcam.instrumental.archive.tar.HeaderField.GID;
import static io.earcam.instrumental.archive.tar.HeaderField.GROUP_NAME;
import static io.earcam.instrumental.archive.tar.HeaderField.INDICATOR;
import static io.earcam.instrumental.archive.tar.HeaderField.LINKED_FILE;
import static io.earcam.instrumental.archive.tar.HeaderField.MTIME;
import static io.earcam.instrumental.archive.tar.HeaderField.OWNER_NAME;
import static io.earcam.instrumental.archive.tar.HeaderField.PREFIX_FILE;
import static io.earcam.instrumental.archive.tar.HeaderField.UID;
import static io.earcam.instrumental.archive.tar.HeaderField.USTAR_MAGIC;
import static io.earcam.instrumental.archive.tar.HeaderField.USTAR_VERSION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ROOT;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class Header {

	static final byte NULL = (byte) 0;
	static final byte ASCII_SPACE = 32;
	static final byte ASCII_ZERO = 48;
	static final Charset CHARSET = UTF_8;
	static final int HEADER_SIZE = 512;

	final byte[] buffer = new byte[HEADER_SIZE];
	Map<Long, String> inodes = new HashMap<>();

	String filename;
	long lastModified;
	long size;
	int uid;
	int gid;
	int mode;

	String userName;
	String groupName;
	Set<Permission> permissions;

	int checksum;

	TypeFlag indicator;

	String linkedFilename;

	int deviceMajor;
	int deviceMinor;
	String filenamePrefix;

	boolean rootless;


	public Header from(Path baseDir, Path path) throws IOException
	{
		linkedFilename = "";
		filenamePrefix = "";
		deviceMajor = deviceMinor = 0;
		userName = groupName = null;

		filename = baseDir.relativize(path).toString();  // TODO for individual files as sourceDirectories
		if("".equals(filename)) {
			return this;
		}

		String toStat = "unix:ino,nlink,mode,permissions,lastModifiedTime,size,uid,gid,owner,group";
		Map<String, Object> attrs = Files.readAttributes(path, toStat, LinkOption.NOFOLLOW_LINKS);

		lastModified = ((FileTime) attrs.get("lastModifiedTime")).to(TimeUnit.SECONDS);

		uid = (int) attrs.get("uid");
		gid = (int) attrs.get("gid");
		mode = (int) attrs.get("mode");

		userName = ((UserPrincipal) attrs.get("owner")).getName();
		groupName = ((GroupPrincipal) attrs.get("group")).getName();
		permissions = Permission.from(mode);

		Long inode = (Long) attrs.get("ino");

		linkedFilename = inodes.getOrDefault(inode, "");

		if("".equals(linkedFilename)) {
			// we've not seen this inode before
			inodes.put(inode, filename);
			indicator = TypeFlag.fromMode(mode);

			if(indicator == TypeFlag.SYMBOLIC_LINK) {
				linkedFilename = Files.readSymbolicLink(path).toString();
			}

		} else { // we've seen this inode before, ergo hard-link
			indicator = TypeFlag.HARD_LINK;
		}

		if(indicator == TypeFlag.DIRECTORY) {
			filename += '/';
		}

		size = (!"".equals(linkedFilename) || indicator == TypeFlag.DIRECTORY) ? 0
				: (long) attrs.get("size");
		return this;
	}


	public byte[] toBytes()
	{
		if("".equals(filename)) {
			return new byte[0];
		}
		clear();

		copyIntoBuffer(getBytes(filename), FILE_NAME);
		copyIntoBuffer(permissionsToOctalBytes(permissions), FILE_MODE, ASCII_ZERO);
		copyIntoBuffer(toOctalBytes(uid), UID, ASCII_ZERO);
		copyIntoBuffer(toOctalBytes(gid), GID, ASCII_ZERO);
		copyIntoBuffer(toOctalBytes(size), FILE_SIZE, ASCII_ZERO);
		copyIntoBuffer(toOctalBytes(lastModified), MTIME, ASCII_ZERO);
		copyIntoBuffer(indicator.flag, INDICATOR);
		copyIntoBuffer(getBytes(linkedFilename), LINKED_FILE);

		copyIntoBuffer(getBytes("ustar"), USTAR_MAGIC);
		copyIntoBuffer(getBytes("  "), USTAR_VERSION);
		copyIntoBuffer(getBytes(userName), OWNER_NAME);
		copyIntoBuffer(getBytes(groupName), GROUP_NAME);
		copyIntoBuffer(toOctalBytes(deviceMajor), DEVICE_MAJOR);
		copyIntoBuffer(toOctalBytes(deviceMinor), DEVICE_MINOR);
		copyIntoBuffer(getBytes(filenamePrefix), PREFIX_FILE);

		calculateAndCopyChecksum();

		return buffer;
	}


	private static byte[] getBytes(String text)
	{
		return text.getBytes(CHARSET);
	}


	private void copyIntoBuffer(byte[] bytes, HeaderField offset)
	{
		System.arraycopy(bytes, 0, buffer, offset.offset, bytes.length);
	}


	private static byte[] permissionsToOctalBytes(Set<Permission> permissions)
	{
		return toOctalBytes(Permission.to(permissions));

	}


	private void copyIntoBuffer(byte[] bytes, HeaderField offset, byte leftPad)
	{
		int padding = offset.offset + offset.size - bytes.length - 1;
		Arrays.fill(buffer, offset.offset, padding, leftPad);
		System.arraycopy(bytes, 0, buffer, padding, bytes.length);
	}


	private void copyIntoBuffer(char value, HeaderField offset)
	{
		buffer[offset.offset] = (byte) value;
	}


	private static byte[] toOctalBytes(int value)
	{
		return getBytes(Integer.toOctalString(value));
	}


	private static byte[] toOctalBytes(long value)
	{
		return getBytes(Long.toOctalString(value));
	}


	private void calculateAndCopyChecksum()
	{
		for(int i = 0; i < HEADER_SIZE; i++) {
			checksum += buffer[i];
		}
		checksum += 32 * (CHECKSUM.size);

		// GNU tar vs Wikipedia - we end up with what smells like an off-by-one (wikipedia doesn't compile, GNU tar wins
		// by default)
		// copyIntoBuffer(toOctalBytes(checksum), CHECKSUM)
		copyIntoBuffer(getBytes(String.format(ROOT, "%06o", checksum)), CHECKSUM);

		buffer[CHECKSUM.offset + CHECKSUM.size - 1] = ASCII_SPACE; // pfft
	}


	private void clear()
	{
		checksum = 0;
		Arrays.fill(buffer, NULL);
	}


	public boolean read(InputStream input) throws IOException
	{
		int read = input.read(buffer);
		if(read != HEADER_SIZE) {
			throw new IllegalStateException(); // TODO
		}
		return read();
	}


	private boolean read()
	{
		int nullPosition = firstIndexOfNull(buffer, FILE_NAME.offset);
		if(nullPosition == 0) {
			return false;
		}
		filename = new String(buffer, FILE_NAME.offset, nullPosition, CHARSET);

		int perms = Integer.parseInt(new String(buffer, FILE_MODE.offset, FILE_MODE.size - 1, CHARSET), 8);
		permissions = Permission.from(perms);

		uid = Integer.parseInt(new String(buffer, UID.offset, UID.size - 1, CHARSET), 8);
		gid = Integer.parseInt(new String(buffer, GID.offset, GID.size - 1, CHARSET), 8);
		size = Long.parseLong(new String(buffer, FILE_SIZE.offset, FILE_SIZE.size - 1, CHARSET), 8);
		lastModified = Long.parseLong(new String(buffer, MTIME.offset, MTIME.size - 1, CHARSET), 8);
		indicator = TypeFlag.from(buffer[INDICATOR.offset]);
		linkedFilename = new String(buffer, LINKED_FILE.offset, firstIndexOfNull(buffer, LINKED_FILE.offset) - LINKED_FILE.offset, CHARSET);

		// TODO assuming ustar magic and ustar version

		userName = new String(buffer, OWNER_NAME.offset, firstIndexOfNull(buffer, OWNER_NAME.offset) - OWNER_NAME.offset, CHARSET);
		groupName = new String(buffer, GROUP_NAME.offset, firstIndexOfNull(buffer, GROUP_NAME.offset) - GROUP_NAME.offset, CHARSET);

		if(buffer[DEVICE_MAJOR.offset] != 0) {
			deviceMajor = Integer.parseInt(new String(buffer, DEVICE_MAJOR.offset, DEVICE_MAJOR.size - 1, CHARSET), 8);
		}
		if(buffer[DEVICE_MINOR.offset] != 0) {
			deviceMinor = Integer.parseInt(new String(buffer, DEVICE_MINOR.offset, DEVICE_MINOR.size - 1, CHARSET), 8);
		}
		return true;
	}


	private static int firstIndexOfNull(byte[] buffer, int start)
	{
		int p = start;
		while(buffer[p++] != 0);
		return p - 1;
	}
}
