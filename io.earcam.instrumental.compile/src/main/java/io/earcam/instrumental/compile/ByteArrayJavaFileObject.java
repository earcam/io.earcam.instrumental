/*-
 * #%L
 * io.earcam.instrumental.compile
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
package io.earcam.instrumental.compile;

import static javax.tools.JavaFileObject.Kind.CLASS;
import static javax.tools.JavaFileObject.Kind.SOURCE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.tools.SimpleJavaFileObject;

class ByteArrayJavaFileObject extends SimpleJavaFileObject {

	private final ByteArrayOutputStream outputStream;


	/**
	 * <p>
	 * Constructor for ByteArrayJavaFileObject.
	 * </p>
	 *
	 * @param name the fully qualifed name
	 * @param kind the file object {@link Kind}
	 */
	protected ByteArrayJavaFileObject(String name, Kind kind)
	{
		super(uriFromName(name, kind), kind);
		outputStream = new ByteArrayOutputStream();
	}


	static URI uriFromName(String name, Kind kind)
	{
		return URI.create("bytes:///" + nameBasedOnKind(name, kind));
	}


	static String nameBasedOnKind(String name, Kind kind)
	{
		return (kind == CLASS || kind == SOURCE) ? (name.replace('.', '/') + kind.extension) : name;
	}


	/** {@inheritDoc} */
	@Override
	public OutputStream openOutputStream() throws IOException
	{
		return outputStream;
	}


	/**
	 * <p>
	 * getBytes.
	 * </p>
	 *
	 * @return an array of {@link byte} objects.
	 */
	public byte[] getBytes()
	{
		return outputStream.toByteArray();
	}

	// for ECJ? or remove
	/*
	 * @Override
	 * public InputStream openInputStream() throws IOException
	 * {
	 * return new ByteArrayInputStream(getBytes());
	 * }
	 */


	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
	{
		return new String(getBytes(), StandardCharsets.UTF_8); // note: charset must be taken from that passed into FileManager
	}
}
