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
package io.earcam.instrumental.module.jpms.parser;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.earcam.unexceptional.Exceptional;

final class Parsing {

	private Parsing()
	{}


	private static Java9Parser parser(CharStream input)
	{
		Java9Lexer lexer = new Java9Lexer(input);
		TokenStream tokens = new CommonTokenStream(lexer);
		return new Java9Parser(tokens);
	}


	static Java9Parser failFastParserFor(String value)
	{
		Java9Parser parser = parser(CharStreams.fromString(value));
		parser.setErrorHandler(new BailErrorStrategy());
		return parser;
	}


	static Java9Parser failFastParserFor(InputStream value, Charset charset)
	{
		Java9Parser parser = parser(Exceptional.apply(CharStreams::fromStream, value, charset));
		parser.setErrorHandler(new BailErrorStrategy());
		return parser;
	}


	static void walk(ParseTree tree, AntlrParser listener)
	{
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);
	}
}
