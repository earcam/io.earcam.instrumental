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

import static io.earcam.instrumental.module.osgi.ClauseParameters.EMPTY_PARAMETERS;
import static io.earcam.instrumental.module.osgi.ClauseParameters.ClauseParameter.attribute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.junit.jupiter.api.Test;

public class ClauseTest {

	@Test
	void equal()
	{
		Clause a = new Clause("com.acme.santa", attribute("key", "value"));

		SortedSet<String> pkg = new ConcurrentSkipListSet<>();
		pkg.add("com.acme.santa");
		Clause b = new Clause(pkg, attribute("key", "value"));

		assertThat(a, is(equalTo(b)));
		assertThat(a.hashCode(), is(equalTo(b.hashCode())));
	}


	@Test
	void notEqualWhenNamesDiffer()
	{
		Clause a = new Clause("com.acme.santa", attribute("key", "value"));
		Clause b = new Clause("com.acme.teeth.sharp", attribute("key", "value"));

		assertThat(a, is(not(equalTo(b))));
	}


	@Test
	void notEqualWhenParametersDiffer()
	{
		Clause a = new Clause("com.acme.santa", attribute("key", "value"));
		Clause b = new Clause("com.acme.santa", EMPTY_PARAMETERS);

		assertThat(a, is(not(equalTo(b))));
	}


	@Test
	void notEqualWithAdditionalUniqueNames()
	{
		Clause a = new Clause("com.acme.santa", EMPTY_PARAMETERS);
		Clause b = new Clause("com.acme.santa,com.acme.christmas.father", EMPTY_PARAMETERS);

		assertThat(a, is(not(equalTo(b))));
	}


	@Test
	void notEqualToNullInstance()
	{
		Clause a = new Clause("com.acme.santa", attribute("key", "value"));
		Clause b = null;

		assertFalse(a.equals(b));
	}


	@Test
	void notEqualToNullObject()
	{
		Clause a = new Clause("com.acme.santa", attribute("key", "value"));
		Object b = null;

		assertFalse(a.equals(b));
	}
}
