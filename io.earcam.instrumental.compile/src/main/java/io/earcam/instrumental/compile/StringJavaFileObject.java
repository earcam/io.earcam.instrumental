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

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static javax.tools.JavaFileObject.Kind.SOURCE;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.SimpleJavaFileObject;

class StringJavaFileObject extends SimpleJavaFileObject {

	private static final Pattern MODULE_PATTERN = Pattern.compile("(?s:^"
			+ "(?:(?s:\\s*/\\*(.*?)\\*/\\s*)|//.*|\\s*)*"
			+ "\\s*\\bmodule\\s+(\\w[\\w\\d_\\.\\$]+).*)", MULTILINE | DOTALL);

	private static final Pattern SIMPLE_NAME_PATTERN = Pattern.compile("(?s:.*?\\b(enum|interface|class)\\s+(\\w[\\w\\d_\\$]+).*)", MULTILINE | DOTALL);

	private static final Pattern PACKAGE_PATTERN = Pattern.compile("(?s:^"
			+ "(?:(?s:\\s*/\\*(.*?)\\*/\\s*)|//.*|\\s*)*"
			+ "\\s*package\\s+([^;\\s]+).*)", MULTILINE | DOTALL);

	private String source;


	/**
	 * <p>
	 * Constructor for StringJavaFileObject.
	 * </p>
	 *
	 * @param source a {@link java.lang.String} object.
	 */
	protected StringJavaFileObject(String source)
	{
		super(URI.create("string:///" + extractName(source) + SOURCE.extension), SOURCE);
		this.source = source;
	}


	static String extractName(String source)
	{
		if(MODULE_PATTERN.matcher(source).matches()) {
			return "/module-info";
		}
		Matcher matched = PACKAGE_PATTERN.matcher(source);
		String pkg = matched.matches() ? matched.replaceAll("$2").replace('.', '/') : "";
		return pkg + '/' + SIMPLE_NAME_PATTERN.matcher(source).replaceAll("$2");
	}


	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
	{
		return source;
	}

	// for ECJ? or remove
	/*
	 * @Override
	 * public OutputStream openOutputStream() throws IOException
	 * {
	 * return new ByteArrayOutputStream() {
	 * 
	 * @Override
	 * public void close() throws IOException
	 * {
	 * super.close();
	 * source = new String(toByteArray(), UTF_8);
	 * }
	 * };
	 * }
	 * 
	 * 
	 * @Override
	 * public InputStream openInputStream() throws IOException
	 * {
	 * return new ByteArrayInputStream(source.getBytes(UTF_8)); //note: charset must be taken from that passed into
	 * FileManager
	 * }
	 */
}
