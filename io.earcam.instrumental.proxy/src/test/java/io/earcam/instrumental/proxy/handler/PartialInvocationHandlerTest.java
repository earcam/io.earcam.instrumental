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

import static io.earcam.instrumental.proxy.handler.NoopInvocationHandler.NOOP_INVOCATION_HANDLER;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.proxy.Proxies;

@SuppressWarnings("unused")
public class PartialInvocationHandlerTest {

	private class SizeInterceptor extends PartialInvocationHandler<List<Object>> {

		private final int fixedSize;


		SizeInterceptor(List<Object> delegate, int fixedSize)
		{
			super(delegate);
			this.fixedSize = fixedSize;
		}


		public int size()
		{
			return fixedSize;
		}
	}


	@Test
	public void interceptSingleMethodWithFixedValue()
	{
		List<Object> delegate = new ArrayList<>();
		int fixedSize = 42;
		SizeInterceptor interceptor = new SizeInterceptor(delegate, fixedSize);
		List<?> proxy = Proxies.proxy(interceptor, List.class);

		assertThat(proxy, is(empty()));
		assertThat(proxy, hasSize(fixedSize));
	}


	@Test
	public void inheritance()
	{
		class GetInterceptor extends SizeInterceptor {

			private Object getValue;


			GetInterceptor(List<Object> delegate, int fixedSize, Object getValue)
			{
				super(delegate, fixedSize);
				this.getValue = getValue;
			}


			public Object get(int index)
			{
				return getValue;
			}
		}

		List<Object> delegate = new ArrayList<>();
		int fixedSize = 42;
		String value = "Weee!";
		GetInterceptor interceptor = new GetInterceptor(delegate, fixedSize, value);
		List<?> proxy = Proxies.proxy(interceptor, List.class);

		assertThat(proxy, is(empty()));
		assertThat(proxy, hasSize(fixedSize));
		assertThat(proxy.get(404), is(equalTo(value)));
	}

	// EARCAM_SNIPPET_BEGIN: default-methods
	public interface WithDefaultMethod {

		default int returnOne()
		{
			return 1;
		}


		default int returnTwo()
		{
			return returnOne() + returnOne();
		}
	}
	// EARCAM_SNIPPET_END: default-methods

	public static class OverridesDefault extends PartialInvocationHandler<WithDefaultMethod> implements WithDefaultMethod {
		OverridesDefault(WithDefaultMethod target)
		{
			super(target);
		}


		@Override
		public int returnOne()
		{
			return 100;
		}
	}


	@Test
	public void invokesOverridenDefaultMethodWherePresent()
	{
		final WithDefaultMethod noopTarget = Proxies.proxy(NOOP_INVOCATION_HANDLER, WithDefaultMethod.class);

		PartialInvocationHandler<WithDefaultMethod> handler = new OverridesDefault(noopTarget);

		WithDefaultMethod proxy = Proxies.proxy(handler, WithDefaultMethod.class);

		assertThat(proxy.returnOne(), is(100));
		assertThat(proxy.returnTwo(), is(200));
	}


	@Test
	public void invokesDefaultMethodWhereOverrideIsNotPresent()
	{
		WithDefaultMethod noopTarget = Proxies.proxy(NOOP_INVOCATION_HANDLER, WithDefaultMethod.class);

		PartialInvocationHandler<WithDefaultMethod> handler = new PartialInvocationHandler<WithDefaultMethod>(noopTarget) {};

		WithDefaultMethod proxy = Proxies.proxy(handler, WithDefaultMethod.class);

		assertThat(proxy.returnOne(), is(1));
		assertThat(proxy.returnTwo(), is(2));
	}


	@Test
	public void invokesDefaultMethodWhereOverrideIsNotPresent2()
	{
		// EARCAM_SNIPPET_BEGIN: partial-handler
		WithDefaultMethod noop = Proxies.proxy(NOOP_INVOCATION_HANDLER, WithDefaultMethod.class);

		PartialInvocationHandler<WithDefaultMethod> handler;
		handler = new PartialInvocationHandler<WithDefaultMethod>(noop) {
			public int returnTwo()
			{
				return -2;
			}
		};
		// EARCAM_SNIPPET_END: partial-handler

		// EARCAM_SNIPPET_BEGIN: partial-invocation
		WithDefaultMethod proxy = Proxies.proxy(handler, WithDefaultMethod.class);

		assertThat(proxy.returnOne(), is(1));
		assertThat(proxy.returnTwo(), is(-2));
		// EARCAM_SNIPPET_END: partial-invocation
	}


	@Test
	public void invokesDefaultMethodWhereOverrideIsNotPresent3()
	{
		WithDefaultMethod noopTarget = Proxies.proxy(NOOP_INVOCATION_HANDLER, WithDefaultMethod.class);

		PartialInvocationHandler<WithDefaultMethod> handler = new PartialInvocationHandler<WithDefaultMethod>(noopTarget) {
			public int returnOne()
			{
				return 999;
			}
		};

		WithDefaultMethod proxy = Proxies.proxy(handler, WithDefaultMethod.class);

		assertThat(proxy.returnOne(), is(999));
		assertThat(proxy.returnTwo(), is(999 + 999));
	}
}
