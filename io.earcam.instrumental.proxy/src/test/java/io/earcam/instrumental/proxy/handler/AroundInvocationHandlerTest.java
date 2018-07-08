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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.proxy.Proxies;
import io.earcam.instrumental.proxy.handler.AroundInvocationHandler.PostInvoke;
import io.earcam.instrumental.proxy.handler.AroundInvocationHandler.PreInvoke;

public class AroundInvocationHandlerTest implements Serializable {

	private static final long serialVersionUID = -5116320370968985277L;

	private boolean preInvoked, postInvoked;


	@Test
	public void test()
	{
		PreInvoke<String> pre = i -> {
			preInvoked = true;
			return "Hello";
		};
		PostInvoke<String> post = (i, s) -> {
			postInvoked = true;
			i.result.value = s;
		};

		AroundInvocationHandler<String, AroundInvocationHandlerTest> handler = new AroundInvocationHandler<>(pre, this, post);
		Serializable proxy = Proxies.proxy(handler, Serializable.class);

		assertThat(proxy, hasToString(equalTo("Hello")));
		assertThat(preInvoked, is(true));
		assertThat(postInvoked, is(true));
	}


	@Override
	public String toString()
	{
		return "Good bye";
	}
}
