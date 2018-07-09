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
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class ClauseParametersTest {

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