/*-
 * #%L
 * io.earcam.instrumental.archive
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
package io.earcam.instrumental.archive;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

import io.earcam.unexceptional.Exceptional;
import io.earcam.utilitarian.charstar.CharSequences;
import io.earcam.utilitarian.io.IoStreams;

/**
 * <p>
 * ArchiveResource class.
 * </p>
 * <p>
 * Callers are expected to close {@link InputStream}s where read. Otherwise, reliance is
 * placed upon the {@link InputStream} implementation to {@link InputStream#close()} itself in
 * it's own {@code finalize} method (e.g. where the underlying maintains file handles).
 * </p>
 *
 */
public class ArchiveResource {

	private String name;
	private byte[] contents;
	private InputStream inputStream;


	/**
	 * <p>
	 * Constructor for ArchiveResource.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param contents an array of {@link byte} objects.
	 */
	public ArchiveResource(String name, byte[] contents)
	{
		this.name = (name.charAt(0) == '/') ? name.substring(1) : name;
		this.contents = contents;
		this.inputStream = new ByteArrayInputStream(contents);
	}


	/**
	 * <p>
	 * Constructor for ArchiveResource.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param inputStream a {@link java.io.InputStream} object.
	 */
	public ArchiveResource(String name, InputStream inputStream)
	{
		this.name = name;
		this.inputStream = inputStream;
	}


	/**
	 * <p>
	 * Compares the {@code name} argument with this resource's name. A leading
	 * slash '/', if present, shall be removed.
	 * </p>
	 * 
	 * @param name a prospective resource name.
	 * @return {@code true} IFF the supplied argument matches this name.
	 *
	 * @see #sameName(CharSequence, CharSequence)
	 */
	public boolean sameName(CharSequence name)
	{
		CharSequence norm = trimLeadingSlash(name);
		return CharSequences.same(this.name, norm);
	}


	/**
	 * <p>
	 * A conveninance method allowing callers to make use of same logic as {@link #sameName(CharSequence)}.
	 * </p>
	 * 
	 * @param a first argument.
	 * @param b second argument.
	 * @return {@code true} IFF a &cong; b (given leading slashes removed)
	 * 
	 * @see #sameName(CharSequence)
	 */
	public static boolean sameName(CharSequence a, CharSequence b)
	{
		return CharSequences.same(trimLeadingSlash(a), trimLeadingSlash(b));
	}


	private static CharSequence trimLeadingSlash(CharSequence text)
	{
		return (text.charAt(0) == '/') ? text.subSequence(1, text.length()) : text;
	}


	/** {@inheritDoc} */
	@Override
	public boolean equals(Object other)
	{
		return other instanceof ArchiveResource && equals((ArchiveResource) other);
	}


	/**
	 * <p>
	 * equals.
	 * </p>
	 *
	 * @param that a {@link io.earcam.instrumental.archive.ArchiveResource} object.
	 * @return a boolean.
	 */
	public boolean equals(ArchiveResource that)
	{
		return that != null
				&& Objects.equals(that.name(), this.name);
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}


	/**
	 * Writes the underlying rep - which may be an {@link java.io.InputStream} or {@code byte[]}
	 *
	 * @param output a {@link java.io.OutputStream} object.
	 * @throws UncheckedIOException if an {@link java.io.IOException} is thrown
	 */
	public void write(OutputStream output)
	{
		if(isInputStreamBacked()) {
			IoStreams.transfer(inputStream, output);
		} else {
			Exceptional.accept(output::write, contents);
		}
	}


	/**
	 * <p>
	 * isInputStreamBacked.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public boolean isInputStreamBacked()
	{
		return contents == null;
	}


	/**
	 * <p>
	 * name.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String name()
	{
		return name;
	}


	public String extension()
	{
		int index = name.lastIndexOf('.');
		return (index == -1) ? "" : name.substring(index);
	}


	/**
	 * <p>
	 * bytes.
	 * </p>
	 *
	 * @return an array of {@link byte} objects.
	 */
	public byte[] bytes()
	{
		return isInputStreamBacked() ? drained() : contents;
	}


	private byte[] drained()
	{
		contents = IoStreams.readAllBytes(inputStream);
		Exceptional.run(inputStream::close);
		inputStream = null;
		return contents;
	}


	/**
	 * <p>
	 * inputStream.
	 * </p>
	 *
	 * @return a {@link java.io.InputStream} object.
	 */
	public InputStream inputStream()
	{
		return inputStream;
	}


	/**
	 * <p>
	 * unknownSize.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public boolean unknownSize()
	{
		return isInputStreamBacked();
	}


	/**
	 * <p>
	 * knownSize.
	 * </p>
	 *
	 * @return a long.
	 */
	public long knownSize()
	{
		return isInputStreamBacked() ? -1 : contents.length;
	}


	/**
	 * <p>
	 * pkg.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String pkg()
	{
		int end = name.lastIndexOf('/');
		return ((end == -1) ? "" : name.substring(0, end)).replace('/', '.');
	}
}
