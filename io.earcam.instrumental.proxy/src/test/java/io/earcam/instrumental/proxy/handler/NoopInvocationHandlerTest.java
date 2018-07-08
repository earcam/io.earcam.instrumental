/*-
 * #%L
 * io.earcam.instrumental.proxy
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
package io.earcam.instrumental.proxy.handler;

import static io.earcam.instrumental.proxy.Proxies.proxy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.ObjectInput;
import java.lang.reflect.Proxy;
import java.util.Formattable;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.proxy.Proxies;

public class NoopInvocationHandlerTest {

	private final NoopInvocationHandler handler = new NoopInvocationHandler();


	@Test
	public void equal()
	{
		Formattable proxy = proxy(handler, Formattable.class);

		assertThat(proxy, is(equalTo(proxy)));
	}


	@Test
	public void notEqual()
	{
		Formattable proxy1 = proxy(handler, Formattable.class);
		Formattable proxy2 = proxy(handler, Formattable.class);

		assertThat(proxy1, is(not(equalTo(proxy2))));
	}


	@Test
	public void hashCodeIsSystemIdentityOfProxy()
	{
		Formattable proxy1 = proxy(handler, Formattable.class);

		assertThat(proxy1.hashCode(), is(equalTo(System.identityHashCode(proxy1))));
	}


	@Test
	public void handlersToStringsDiffer()
	{
		NoopInvocationHandler other = new NoopInvocationHandler();

		assertThat(handler.toString(), is(not(equalTo(other.toString()))));
	}


	@Test
	public void proxiedToStringContainsTypeAndSystemIdentityDetails()
	{
		ObjectInput proxy = proxy(handler, ObjectInput.class);

		assertThat(proxy.toString(), allOf(
				startsWith(Proxy.class.getName()),
				containsString(handler.toString()),
				endsWith(Proxies.identityToStringOf(proxy))));
	}

	private static interface Foo {

		public String toString(Object a, Object b);


		public StringBuilder toString(Object a);


		public boolean equals(Object a, Object b);


		public void potatoe(String mash);


		public short theBritishPound();


		public double up();


		public char actorBuilding();
	}


	@Test
	public void givenToStringMethodSignatureWithDifferentSignatureWillReturnNull()
	{
		Foo proxy = proxy(handler, Foo.class);

		String returned = proxy.toString(Integer.MIN_VALUE, Integer.MAX_VALUE);

		assertThat(returned, is(nullValue()));
	}


	@Test
	public void givenMethodSignatureReturningObjectWillReturnNull()
	{
		Foo proxy = proxy(handler, Foo.class);

		StringBuilder returned = proxy.toString(Integer.MIN_VALUE);

		assertThat(returned, is(nullValue()));
	}


	@Test
	public void givenMethodSignatureReturningPrimitiveBooleanWillReturnDefaultFalse()
	{
		Foo proxy = proxy(handler, Foo.class);

		boolean equals = proxy.equals(Integer.MIN_VALUE, Integer.MAX_VALUE);

		assertThat(equals, is(false));
	}


	@Test
	public void safeInvocationOnMethodWithVoidReturn()
	{
		Foo proxy = proxy(handler, Foo.class);

		proxy.potatoe("mash");
	}


	@Test
	public void safeInvocationOnMethodWithCharReturn()
	{
		Foo thisIsChar = proxy(handler, Foo.class);

		assertThat(thisIsChar.actorBuilding(), is('\0'));
	}


	@Test
	public void safeInvocationOnMethodWithShortReturn()
	{
		Foo shorting = proxy(handler, Foo.class);

		assertThat(shorting.theBritishPound(), is((short) 0));
	}


	@Test
	public void safeInvocationOnMethodWithDoubleReturn()
	{
		Foo look = proxy(handler, Foo.class);

		assertThat(look.up(), is(0D));
	}
}
