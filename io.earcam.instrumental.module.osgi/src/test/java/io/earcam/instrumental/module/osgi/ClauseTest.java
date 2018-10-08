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
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.module.osgi.ClauseParameters.ClauseParameter;

public class ClauseTest {

	@Test
	public void equal()
	{
		Clause a = new Clause("com.acme.santa", attribute("key", "value"));

		SortedSet<String> pkg = new ConcurrentSkipListSet<>();
		pkg.add("com.acme.santa");
		Clause b = new Clause(pkg, attribute("key", "value"));

		assertThat(a, is(equalTo(b)));
		assertThat(a.hashCode(), is(equalTo(b.hashCode())));
	}


	@Test
	public void notEqualWhenNamesDiffer()
	{
		Clause a = new Clause("com.acme.santa", attribute("key", "value"));
		Clause b = new Clause("com.acme.teeth.sharp", attribute("key", "value"));

		assertThat(a, is(not(equalTo(b))));
	}


	@Test
	public void notEqualWhenParametersDiffer()
	{
		Clause a = new Clause("com.acme.santa", attribute("key", "value"));
		Clause b = new Clause("com.acme.santa", EMPTY_PARAMETERS);

		assertThat(a, is(not(equalTo(b))));
	}


	@Test
	public void notEqualWithAdditionalUniqueNames()
	{
		SortedSet<String> pkg = new ConcurrentSkipListSet<>();
		pkg.add("com.acme.santa");
		pkg.add("com.acme.christmas.father");

		Clause a = new Clause("com.acme.santa", EMPTY_PARAMETERS);
		Clause b = new Clause(pkg, EMPTY_PARAMETERS);

		assertThat(a, is(not(equalTo(b))));
	}


	@Test
	public void notEqualToNullInstance()
	{
		Clause a = new Clause("com.acme.santa", attribute("key", "value"));
		Clause b = null;

		assertFalse(a.equals(b));
	}


	@Test
	public void notEqualToNullObject()
	{
		Clause a = new Clause("com.acme.santa", attribute("key", "value"));
		Object b = null;

		assertFalse(a.equals(b));
	}


	@Test
	public void singleToString()
	{
		Clause clause = new Clause(Clause.sortedSet("x.a", "x.b", "x.c"), ClauseParameter.attribute("at", "rib").directive("dire", "ct"));

		assertThat(clause, hasToString("x.a;x.b;x.c;at=rib;dire:=ct"));
	}


	@Test
	public void allToString()
	{
		List<Clause> clauses = Arrays.asList(
				new Clause(Clause.sortedSet("x.a", "x.b", "x.c"), ClauseParameter.attribute("at", "rib").directive("dire", "ct")),
				new Clause(Clause.sortedSet("y.a"), ClauseParameter.attribute("why", "rib").directive("why", "ct")),
				new Clause(Clause.sortedSet("z.a", "z.b", "z.c"), ClauseParameter.attribute("zzz", "well").directive("zzz", "nightynight")));

		String all = Clause.allToString(clauses);

		assertThat(all, is(equalTo("x.a;x.b;x.c;at=rib;dire:=ct,y.a;why=rib;why:=ct,z.a;z.b;z.c;zzz=well;zzz:=nightynight")));
	}
}
