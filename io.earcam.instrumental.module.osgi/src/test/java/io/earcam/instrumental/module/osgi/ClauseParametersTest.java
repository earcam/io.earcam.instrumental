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
package io.earcam.instrumental.module.osgi;

import static io.earcam.instrumental.module.osgi.ClauseParameters.ClauseParameter.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ClauseParametersTest {

	@Nested
	public class Equality {

		@Test
		void singleAttributeEqual()
		{
			ClauseParameters a = attribute("key", "value");
			ClauseParameters b = attribute("key", "value");

			assertThat(a, is(equalTo(b)));
			assertThat(a.hashCode(), is(equalTo(b.hashCode())));
		}


		@Test
		void singleAttributeNotEqualWhenValuesDiffer()
		{
			ClauseParameters a = attribute("key", "X");
			ClauseParameters b = attribute("key", "O");

			assertThat(a, is(not(equalTo(b))));
		}


		@Test
		void singleDirectiveEqual()
		{
			ClauseParameters a = directive("key", "value");
			ClauseParameters b = directive("key", "value");

			assertThat(a, is(equalTo(b)));
			assertThat(a.hashCode(), is(equalTo(b.hashCode())));
		}


		@Test
		void singleDirectiveNotEqualWhenKeysDiffer()
		{
			ClauseParameters a = directive("key", "value");
			ClauseParameters b = directive("yek", "value");

			assertThat(a, is(not(equalTo(b))));
		}


		@Test
		void mixedAttributeAndDirectiveAreEqual()
		{
			ClauseParameters a = directive("level", "42").attribute("key", "value");
			ClauseParameters b = attribute("key", "value").directive("level", "42");

			assertThat(a, is(equalTo(b)));
			assertThat(a.hashCode(), is(equalTo(b.hashCode())));
		}


		@Test
		void notEqualToNullInstance()
		{
			ClauseParameters a = directive("key", "value");
			ClauseParameters b = null;

			assertFalse(a.equals(b));
		}


		@Test
		void notEqualToNullObject()
		{
			ClauseParameters a = directive("key", "value");
			Object b = null;

			assertFalse(a.equals(b));
		}
	}

	@Nested
	public class Defaults {

		@Test
		void attributeOrDefaultReturnsValue()
		{
			String value = attribute("key", "value").attributeOrDefault("key", "invalid");

			assertThat(value, is(equalTo("value")));
		}


		@Test
		void attributeOrDefaultReturnsDefault()
		{
			String value = attribute("key", "invalid").attributeOrDefault("unknown", "value");

			assertThat(value, is(equalTo("value")));
		}
	}

	@Nested
	public class ToString {

		@Test
		void simpleAttributeToString() throws Exception
		{
			assertThat(attribute("key", "value"), hasToString(";key=value"));
		}


		@Test
		void quotedAttributeToString() throws Exception
		{
			assertThat(attribute("key", "\"value\""), hasToString(";key=\"value\""));
		}


		@Test
		void givenAttributeWithPeriodWhenToStringThenQuoted() throws Exception
		{
			assertThat(attribute("key", "val.ue"), hasToString(";key=\"val.ue\""));
		}


		@Test
		void givenDirectiveWithCommaWhenToStringThenQuoted() throws Exception
		{
			assertThat(directive("key", "val,ue"), hasToString(";key:=\"val,ue\""));
		}


		@Test
		void simpleDirectiveToString() throws Exception
		{
			assertThat(directive("key", "value"), hasToString(";key:=value"));
		}


		@Test
		void whenDirectiveValueContainsPeriodThenRequiresQuotesForToString() throws Exception
		{
			assertThat(directive("key", "val.ue"), hasToString(";key:=\"val.ue\""));
		}


		@Test
		void whenAttributeValueContainsCommaThenRequiresQuotesForToString() throws Exception
		{
			assertThat(attribute("key", "val,ue"), hasToString(";key=\"val,ue\""));
		}
	}

	@Nested
	public class Quoted {

		@Test
		void isAQuotedStringWhenStartsAndEndsWithQuote()
		{
			assertThat(ClauseParameters.isQuoted("\"fully quoted\""), is(true));
		}


		@Test
		void isNotAQuotedStringWhenDoesNotEndWithQuote()
		{
			assertThat(ClauseParameters.isQuoted("\"not fully quoted"), is(false));
		}


		@Test
		void isNotAQuoteStringWhenDoesNotStartWithQuote()
		{
			assertThat(ClauseParameters.isQuoted("not fully quoted\""), is(false));
		}
	}


	@Test
	void isInitiallyEmpty()
	{
		assertThat(new ClauseParameters().isEmpty(), is(true));
	}


	@Test
	void notEmptyWhenAttributePresent()
	{
		assertThat(new ClauseParameters().attribute("attr", "ibute").isEmpty(), is(false));
	}


	@Test
	void notEmptyWhenDirectivePresent()
	{
		assertThat(new ClauseParameters().directive("dir", "ective").isEmpty(), is(false));
	}
}