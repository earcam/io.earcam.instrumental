/*-
 * #%L
 * io.earcam.instrumental.module.jpms
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

import io.earcam.instrumental.module.jpms.ModuleInfo;

public final class ModuleInfoParser {

	private ModuleInfoParser()
	{}


	public static ModuleInfo parse(String source)
	{
		Java9Parser parser = Parsing.failFastParserFor(source);
		return parse(parser);
	}


	private static ModuleInfo parse(Java9Parser parser)
	{
		AntlrParser listener = new AntlrParser();
		Parsing.walk(parser.modularCompilation(), listener);
		return listener.builder().construct();
	}


	public static ModuleInfo parse(InputStream source, Charset charset)
	{
		Java9Parser parser = Parsing.failFastParserFor(source, charset);
		return parse(parser);
	}
}
