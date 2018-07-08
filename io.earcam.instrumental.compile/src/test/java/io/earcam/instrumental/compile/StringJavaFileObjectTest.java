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

import static io.earcam.instrumental.compile.StringJavaFileObject.extractName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

public class StringJavaFileObjectTest {

	@Test
	public void extractNameOfSimpleClass()
	{
		assertThat(extractName("public final class FooBar {}"), is(equalTo("/FooBar")));
	}


	@Test
	public void extractNameOfEnumWithPackage()
	{
		String source = line() +
				line("package com.acme;") +
				line() +
				line("import org.junit.Test;") +
				line() +
				line("public enum BeanStalk {") +
				line(tab("FEE")) +
				line(tab("FI")) +
				line(tab("FOE")) +
				line(tab("THUMB")) +
				line("}");

		assertThat(extractName(source), is(equalTo("com/acme/BeanStalk")));
	}


	private String tab(String text)
	{
		return '\t' + text;
	}


	private static final String line(String text)
	{
		return text + line();
	}


	private static String line()
	{
		return "\r\n";
	}


	@Test
	public void extractNameOfClassWithPackageWhenMultilineCommentHeaderPresent()
	{
		String source = line() +
				line("/**") +
				line("  * @author A.N.Other") +
				line("  */") +
				line("package com.acme.bing;") +
				line() +
				line("import org.junit.Test;") +
				line() +
				line("public class Bong {}");
		assertThat(extractName(source), is(equalTo("com/acme/bing/Bong")));
	}


	@Test
	public void extractNameOfClassWithPackageWhenSingleLineCommentHeaderPresent()
	{
		String source = line() +
				line("//Just a single line comment") +
				line() +
				line("package com.acme.pow;") +
				line() +
				line("public interface Wow {}");
		assertThat(extractName(source), is(equalTo("com/acme/pow/Wow")));
	}


	@Test
	public void extractNameOfClassWithPackageWhenDistractingMultilineCommentHeaderPresent()
	{
		String source = line("/*package of.lies*/package com.acme.badda;") +
				line("import org.junit.Test;") +
				line("public class Bing {}");
		assertThat(extractName(source), is(equalTo("com/acme/badda/Bing")));
	}


	@Test
	public void extractNameOfClassWithPackageWhenClassAnnotationsPresent()
	{
		String source = line("package com.acme.ka;") +
				line() +
				line("@ParametersAreNonNullByDefault") +
				line("@AntiIf(All.class)") +
				line("public class Boom {") +
				line(tab("private String mineField;")) +
				line("}");
		assertThat(extractName(source), is(equalTo("com/acme/ka/Boom")));
	}


	@Test
	public void extractNameOfOuterClass()
	{
		String source = line() +
				line("package com.acme.nested;") +
				line() +
				line("import the.most.amazing.DependencyEver;") +
				line() +
				line("public class Outer {") +
				line() +
				line(tab("public static class Inner {")) +
				line(tab(tab("private int someField;"))) +
				line(tab("}")) +
				line() +
				line("}");

		assertThat(extractName(source), is(equalTo("com/acme/nested/Outer")));
	}


	@Test
	public void extractNameOfJpmsModule()
	{
		String source = line() +
				line("module mod.yule {") +
				line() +
				line(tab("requires java.base;")) +
				line() +
				line(tab("exports com.acme.foo;")) +
				line() +
				line("}");

		assertThat(extractName(source), is(equalTo("/module-info")));
	}
}
