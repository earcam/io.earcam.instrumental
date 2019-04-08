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

import static io.earcam.instrumental.archive.AbstractAsJarBuilder.MULTI_RELEASE_ROOT_PATH;
import static io.earcam.instrumental.archive.AbstractAsJarBuilder.SPI_ROOT_PATH;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

import io.earcam.unexceptional.Exceptional;
import io.earcam.utilitarian.io.IoStreams;

/**
 * <p>
 * Rather poor abstraction over elements of an archive's contents.
 * </p>
 * <p>
 * Callers are expected to close {@link InputStream}s where read. Otherwise, reliance is
 * placed upon the {@link InputStream} implementation to {@link InputStream#close()} itself in
 * it's own {@code finalize} method (e.g. where the underlying maintains file handles).
 * </p>
 *
 */
public class ArchiveResource {

	private final String name;
	private final String className;
	private byte[] contents;
	private InputStream inputStream;


	/**
	 * <p>
	 * Constructor for byte array content.
	 * </p>
	 *
	 * @param name the fully qualified name
	 * @param contents the "file" contents
	 */
	public ArchiveResource(String name, byte[] contents)
	{
		this(name);
		this.contents = contents;
		this.inputStream = new ByteArrayInputStream(contents);
	}


	private ArchiveResource(String name)
	{
		this.name = trimLeadingSlash(reduceConsecutiveSlashes(name));
		this.className = isClass() ? stripLeadingMultiVersion(this.name) : "";
	}


	private static String reduceConsecutiveSlashes(String name)
	{
		return name.replaceAll("//+", "/");
	}


	private static String trimLeadingSlash(String name)
	{
		return (name.charAt(0) == '/') ? name.substring(1) : name;
	}


	private static String stripLeadingMultiVersion(String name)
	{
		return name.startsWith(MULTI_RELEASE_ROOT_PATH) ? name.substring(name.indexOf('/', MULTI_RELEASE_ROOT_PATH.length()) + 1) : name;
	}


	/**
	 * <p>
	 * Constructor for {@link InputStream} content.
	 * </p>
	 *
	 * @param name the fully qualified name
	 * @param inputStream the "file" contents
	 */
	public ArchiveResource(String name, InputStream inputStream)
	{
		this(name);
		this.inputStream = inputStream;
	}


	/**
	 * <p>
	 * Rename an existing {@link ArchiveResource}
	 * </p>
	 * 
	 * @param aka the new fully qualified name
	 * @param formerly an existing {@link ArchiveResource}
	 * @return an {@link ArchiveResource}
	 */
	public static ArchiveResource rename(String aka, ArchiveResource formerly)
	{
		return formerly.isInputStreamBacked() ? new ArchiveResource(aka, formerly.inputStream())
				: new ArchiveResource(aka, formerly.bytes());
	}


	/**
	 * <p>
	 * Compares the {@code name} argument with this resource's name. A leading
	 * slash '/', if present, shall be removed.
	 * </p>
	 * 
	 * @param name a prospective resource name.
	 * @return {@code true} IFF the supplied argument matches this name.
	 */
	public boolean sameName(String name)
	{
		return Objects.equals(this.name, trimLeadingSlash(name));
	}


	@Override
	public boolean equals(Object other)
	{
		return other instanceof ArchiveResource && equals((ArchiveResource) other);
	}


	/**
	 * <p>
	 * A type specific overload of {@link #equals(Object)}.
	 * </p>
	 *
	 * @param that another {@link ArchiveResource}.
	 * @return {@code true} iff {@code this} resource has the same {@link #name()} as {@code that}.
	 * 
	 * @see #equals(Object)
	 */
	public boolean equals(ArchiveResource that)
	{
		return that != null
				&& Objects.equals(that.name(), this.name);
	}


	@Override
	public int hashCode()
	{
		return name.hashCode();
	}


	/**
	 * Writes the underlying rep - which may be an {@link java.io.InputStream} or {@code byte[]}
	 *
	 * @param output the output stream to write too.
	 * @throws UncheckedIOException if an {@link java.io.IOException} is thrown
	 */
	public void write(/* @WillNotClose */ OutputStream output)
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
	 * @return {@code true} iff this {@link ArchiveResource} is backed by an InputStream.
	 */
	public boolean isInputStreamBacked()
	{
		return contents == null;
	}


	/**
	 * <p>
	 * The fully qualified path name (i.e. this will include {@value AbstractAsJarBuilder#MULTI_RELEASE_ROOT_PATH}
	 * if present).
	 * </p>
	 *
	 * @return the resource's full path name
	 */
	public String name()
	{
		return name;
	}


	/**
	 * <p>
	 * The binary class name (this only differs from {@link #name()} in that
	 * {@value AbstractAsJarBuilder#MULTI_RELEASE_ROOT_PATH}
	 * will be stripped if present).
	 * </p>
	 *
	 * @return the binary class name (i.e. slash delimited), or empty string if not a class.
	 */
	public String className()
	{
		return className;
	}


	/**
	 * @return the filename suffix.
	 */
	public String extension()
	{
		int index = name.lastIndexOf('.');
		return (index == -1) ? "" : name.substring(index);
	}


	/**
	 * @return the contents as a byte array.
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
	 * @return the contents as an {@link InputStream}.
	 */
	public InputStream inputStream()
	{
		return inputStream;
	}


	/**
	 * @return {@code true} iff the size is known (i.e. backed by byte array).
	 */
	public boolean unknownSize()
	{
		return isInputStreamBacked();
	}


	/**
	 * @return the content length is known, otherwise {@code -1}.
	 */
	public long knownSize()
	{
		return isInputStreamBacked() ? -1 : contents.length;
	}


	/**
	 * <p>
	 * Returns the package name (i.e. the FQN as in Java Language).
	 * </p>
	 *
	 * @return the package name for this class, or empty string if not a class.
	 */
	public String pkg()
	{
		int end = className.lastIndexOf('/');
		return ((end == -1) ? "" : className.substring(0, end)).replace('/', '.');
	}


	/**
	 * 
	 * @return {@code true} iff the file suffix is {@code .class} and the package is non-empty
	 * 
	 * @see #isClass()
	 */
	public boolean isQualifiedClass()
	{
		return isClass() && className.indexOf('/') != -1;
	}


	/**
	 * 
	 * @return {@code true} iff the path begins with {@value AbstractAsJarBuilder#MULTI_RELEASE_ROOT_PATH}
	 */
	public boolean isMultiVersion()
	{
		return name.startsWith(MULTI_RELEASE_ROOT_PATH);
	}


	/**
	 * 
	 * @return {@code true} iff the file suffix is {@code .class}
	 * 
	 * @see #isQualifiedClass()
	 */
	public boolean isClass()
	{
		return ".class".equals(extension());
	}


	/**
	 * @return {@code true} iff the path begins with {@value AbstractAsJarBuilder#SPI_ROOT_PATH}
	 */
	public boolean isSpi()
	{
		return name.startsWith(SPI_ROOT_PATH);
	}
}
