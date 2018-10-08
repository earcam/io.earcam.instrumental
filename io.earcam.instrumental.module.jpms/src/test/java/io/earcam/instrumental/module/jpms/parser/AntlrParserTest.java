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

import static io.earcam.instrumental.module.jpms.RequireModifier.TRANSITIVE;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.earcam.instrumental.module.jpms.Export;
import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.instrumental.module.jpms.Require;

@SuppressWarnings("squid:S2187") // SonarQube false-positive
public class AntlrParserTest {

	@Nested
	class ModuleDeclarationName {

		@Test
		void nameOnly()
		{
			String name = "com.acme.mod.a";
			// @formatter:off
			String source =
					"module " + name + " { \n" +
					"}                     \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.name(), is(equalTo(name)));
		}


		@Test
		void singleAnnotation()
		{
			String name = "com.acme.mod.b";
			// @formatter:off
			String source = 
					"@SuppressWarnings(\"some\")  \n" +
					"module " + name + " {        \n" +
					"}                            \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.name(), is(equalTo(name)));
		}


		@Test
		void multipleAnnotations()
		{
			String name = "com.acme.mod.c";
			// @formatter:off
			String source = 
					"@SuppressWarnings(\"some\")  \n" +
					"@Deprecated                  \n" +
					"module " + name + " {        \n" +
					"}                            \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.name(), is(equalTo(name)));
		}


		@Test
		void open()
		{
			String name = "com.acme.mod.d";
			// @formatter:off
			String source = 
					"open module " + name + " {  \n" +
					"}                           \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.name(), is(equalTo(name)));
		}


		@Test
		void openWithMultipleAnnotations()
		{
			String name = "com.acme.mod.e";
			// @formatter:off
			String source = 
					"@SuppressWarnings(\"some\")  \n" +
					"@Deprecated                  \n" +
					"open module " + name + " {   \n" +
					"}                            \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.name(), is(equalTo(name)));
		}
	}

	@Nested
	class Required {

		@Test
		void singleRequire()
		{
			// @formatter:off
			String source =
					"module com.acme.mod {   \n" +
					"   requires java.sql;   \n" +
					"}                       \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.requires(), contains(new Require("java.sql", 0, null)));
		}


		@Test
		void singleRequireWithTransitiveModifier()
		{
			// @formatter:off
			String source =
					"module com.acme.mod.yule {         \n" +
					"   requires transitive java.xml;   \n" +
					"}                                  \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.requires(), contains(new Require("java.xml", TRANSITIVE.access(), null)));
		}


		@Test
		void multipleRequires()
		{
			// @formatter:off
			String source =
					"module com.acme.mod.yule {         \n" +
					"   requires java.sql;              \n" +
					"   requires transitive java.xml;   \n" +
					"}                                  \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.requires(), containsInAnyOrder(
					new Require("java.sql", 0, null),
					new Require("java.xml", TRANSITIVE.access(), null)));
		}
	}

	@Nested
	class Exported {

		@Test
		void singleUnqalifiedExport()
		{
			// @formatter:off
			String source =
					"module com.acme.base {     \n" +
					"   exports com.acme.api;   \n" +
					"}                          \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.exports(), contains(new Export("com.acme.api")));
		}


		@Test
		void singleQalifiedExport()
		{
			// @formatter:off
			String source =
					"module com.acme.base {                        \n" +
					"   exports com.acme.guts to com.acme.grunt;   \n" +
					"}                                             \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.exports(), contains(new Export("com.acme.guts", "com.acme.grunt")));
		}


		@Test
		void multipleExports()
		{
			// @formatter:off
			String source =
					"module com.acme.base {                        \n" +
					"   exports com.acme.api;                      \n" +
					"   exports com.acme.guts to com.acme.grunt;   \n" +
					"}                                             \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.exports(), containsInAnyOrder(
					new Export("com.acme.api"),
					new Export("com.acme.guts", "com.acme.grunt")));
		}


		@Test
		void multipleExportsViaInputStream()
		{
			// @formatter:off
			String source =
					"module com.acme.base {                        \n" +
					"   exports com.acme.api;                      \n" +
					"   exports com.acme.guts to com.acme.grunt;   \n" +
					"}                                             \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(new ByteArrayInputStream(source.getBytes(UTF_8)), UTF_8);

			assertThat(moduleInfo.exports(), containsInAnyOrder(
					new Export("com.acme.api"),
					new Export("com.acme.guts", "com.acme.grunt")));
		}
	}

	@Nested
	class Opened {

		@Test
		void singleUnqualifiedOpen()
		{
			// @formatter:off
			String source =
					"module com.acme.base {           \n" +
					"   opens com.acme.every.thing;   \n" +
					"}                                \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.opens(), contains(new Export("com.acme.every.thing")));
		}


		@Test
		void singleQualifiedOpen()
		{
			// @formatter:off
			String source =
					"module com.acme.base {                          \n" +
					"   opens com.acme.up to com.acme.friend.sssh;   \n" +
					"}                                               \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.opens(), contains(new Export("com.acme.up", "com.acme.friend.sssh")));
		}


		@Test
		void mutlipleOpens()
		{
			// @formatter:off
			String source =
					"module com.acme.base {                          \n" +
					"   opens com.acme.every.thing;                  \n" +
					"   opens com.acme.up to com.acme.friend.sssh;   \n" +
					"}                                               \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.opens(), contains(
					new Export("com.acme.every.thing"),
					new Export("com.acme.up", "com.acme.friend.sssh")));
		}

	}

	@Nested
	class Used {

		@Test
		void singleUse()
		{
			// @formatter:off
			String source =
					"module com.acme.app {           \n" +
					"   uses com.acme.base.Boost;    \n" +
					"}                               \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.uses(), contains("com.acme.base.Boost"));
		}


		@Test
		void multipleUses()
		{
			// @formatter:off
			String source =
					"module com.acme.app {           \n" +
					"   uses com.acme.base.Boost;    \n" +
					"   uses com.acme.spi.Service;   \n" +
					"   uses com.acme.spi.EventBus;  \n" +
					"}                               \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.uses(), containsInAnyOrder(
					"com.acme.base.Boost",
					"com.acme.spi.Service",
					"com.acme.spi.EventBus"));
		}

	}

	@Nested
	class Provided {

		@Test
		void singleProvisionOfSingleService()
		{
			// @formatter:off
			String source =
					"module com.acme.xyz.service {                 \n" +
					"   provides com.acme.spi.Service              \n" +
					"         with com.acme.imp.DefaultService;    \n" +
					"}                                             \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.provides(), allOf(
					is(aMapWithSize(1)),
					hasEntry(equalTo("com.acme.spi.Service"), arrayContaining("com.acme.imp.DefaultService"))));
		}


		@Test
		void multipleProvisionsOfSingleService()
		{
			// @formatter:off
			String source =
					"module com.acme.xyz.service {        \n" +
					"   provides com.acme.spi.Greet with  \n" +
					"         com.acme.imp.Hello,         \n" +
					"         com.acme.imp.Hey,           \n" +
					"         com.acme.imp.Howdy;         \n" +
					"}                                    \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.provides(), allOf(
					is(aMapWithSize(1)),
					hasEntry(equalTo("com.acme.spi.Greet"), arrayContainingInAnyOrder(
							"com.acme.imp.Hello",
							"com.acme.imp.Hey",
							"com.acme.imp.Howdy"))));
		}


		@Test
		void multipleProvisionsOfMultipleServices()
		{
			// @formatter:off
			String source =
					"module com.acme.xyz.service {        \n" +
					"                                     \n" +
					"   provides com.acme.spi.Funk with   \n" +
					"         com.acme.imp.Funky;         \n" +
					"                                     \n" +
					"   provides com.acme.spi.Greet with  \n" +
					"         com.acme.imp.Hello,         \n" +
					"         com.acme.imp.Hey,           \n" +
					"         com.acme.imp.Howdy;         \n" +
					"}                                    \n";
			// @formatter:on

			ModuleInfo moduleInfo = ModuleInfoParser.parse(source);

			assertThat(moduleInfo.provides(), allOf(
					is(aMapWithSize(2)),
					hasEntry(equalTo("com.acme.spi.Funk"), arrayContaining("com.acme.imp.Funky")),
					hasEntry(equalTo("com.acme.spi.Greet"), arrayContainingInAnyOrder(
							"com.acme.imp.Hello",
							"com.acme.imp.Hey",
							"com.acme.imp.Howdy"))));
		}

	}

	@Nested
	class LargeExamples {

		// Note; java.base's module-info.java had missing semi-colons on some qualified exports (lines 306-309), these
		// were added as otherwise invalid
		@Test
		void javaBase() throws IOException
		{
			String source = new String(Files.readAllBytes(Paths.get(".", "src", "test", "resources", "java.base_module-info.source")), defaultCharset());
			ModuleInfoParser.parse(source);
		}
	}
}