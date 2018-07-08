/*-
 * #%L
 * io.earcam.instrumental.module.osgi
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
package io.earcam.instrumental.module.osgi.parser;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.earcam.instrumental.module.osgi.BundleManifestHeaders;
import io.earcam.instrumental.module.osgi.parser.ManifestLexer;
import io.earcam.instrumental.module.osgi.parser.ManifestParser;
import io.earcam.unexceptional.Exceptional;
import io.earcam.utilitarian.io.ReplaceAllInputStream;

final class Parsing {

	static ManifestParser parserFor(BundleManifestHeaders header, String value)
	{
		return parserFor(header.header() + ": " + value + "\n");
	}


	static ManifestParser parserFor(String value)
	{
		ManifestParser parser = parser(CharStreams.fromString(strip72s(value)));
		// Otherwise this prints the error to STDERR
		parser.removeErrorListeners();
		return parser;
	}


	private static String strip72s(String value)
	{
		return value.replaceAll("\r?\n ", "");
	}


	static ManifestParser parserFor(InputStream value)
	{
		ManifestParser parser = parser(Exceptional.apply(CharStreams::fromStream, strip72s(value), UTF_8));
		// Otherwise this prints the error to STDERR
		parser.removeErrorListeners();
		return parser;
	}


	private static InputStream strip72s(InputStream input)
	{
		return new ReplaceAllInputStream(bytes("\n "), new byte[0], new ReplaceAllInputStream(bytes("\r\n "), new byte[0], input));
	}


	private static byte[] bytes(String string)
	{
		return string.getBytes(UTF_8);
	}


	static ManifestParser parser(CharStream input)
	{
		ManifestLexer lexer = new ManifestLexer(input);
		TokenStream tokens = new CommonTokenStream(lexer);
		return new ManifestParser(tokens);
	}


	static ManifestParser failFastParserFor(String value)
	{
		ManifestParser parser = parser(CharStreams.fromString(strip72s(value)));
		parser.setErrorHandler(new BailErrorStrategy());
		return parser;
	}


	static ManifestParser failFastParserFor(InputStream value)
	{
		ManifestParser parser = parser(Exceptional.apply(CharStreams::fromStream, strip72s(value), UTF_8));
		parser.setErrorHandler(new BailErrorStrategy());
		return parser;
	}


	static void walk(ParseTree tree, ParseTreeListener listener)
	{
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);
	}


	private Parsing()
	{}
}
