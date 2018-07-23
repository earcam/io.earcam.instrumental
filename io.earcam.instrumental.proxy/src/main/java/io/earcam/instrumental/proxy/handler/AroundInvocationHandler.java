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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * AroundInvocationHandler class.
 * </p>
 *
 */
public class AroundInvocationHandler<X, T> implements InvocationHandler {

	/**
	 * Can change method and/or args or throw, returns a context object
	 * 
	 * @param <C> arbitrary context object
	 */
	public static interface PreInvoke<C> {
		/**
		 * @param invocation the pending invocation
		 * @return context the context object
		 */
		C preInvoke(Invocation invocation);
	}

	/**
	 * Can change return or throw, and consumes context object
	 * 
	 * @param <C> arbitrary context object
	 */
	public static interface PostInvoke<C> {
		/**
		 * @param invocation the invocation and result execution
		 * @param context the context object
		 */
		void postInvoke(Invocation invocation, C context);
	}

	private T proxied;

	PreInvoke<X> pre;
	PostInvoke<X> post;

	static class InvocationResult {
		Object value;
		boolean wasThrown;


		public void setReturn(Object result)
		{
			this.value = result;
			wasThrown = false;
		}


		public void setThrown(Throwable thrown)
		{
			value = thrown;
			wasThrown = true;
		}
	}

	public static class Invocation {
		Object proxy;
		Method method;
		Object[] args;
		InvocationResult result = new InvocationResult();


		public Invocation(Object proxy, Method method, Object... args)
		{
			this.proxy = proxy;
			this.method = method;
			this.args = args;
		}
	}


	/**
	 * <p>
	 * Constructor for AroundInvocationHandler.
	 * </p>
	 *
	 * @param pre the pre-invocation handler, see
	 * {@link io.earcam.instrumental.proxy.handler.AroundInvocationHandler.PreInvoke}
	 * @param proxied the underlying object to wrap around
	 * @param post the post-invocation handler, see
	 * {@link io.earcam.instrumental.proxy.handler.AroundInvocationHandler.PostInvoke}
	 */
	public AroundInvocationHandler(PreInvoke<X> pre, T proxied, PostInvoke<X> post)
	{
		this.pre = pre;
		this.post = post;
		this.proxied = proxied;
	}


	AroundInvocationHandler(T proxied)
	{
		this.proxied = proxied;
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		Invocation invocation = new Invocation(proxy, method, args);
		X context = null;
		try {
			context = pre.preInvoke(invocation);
			invocation.result.setReturn(invocation.method.invoke(proxied, invocation.args));
		} catch(Throwable thrown) {  // NOSONAR
			invocation.result.setThrown(thrown);
		}
		post.postInvoke(invocation, context);
		return handleResult(invocation);
	}


	private Object handleResult(Invocation invocation)
	{
		if(invocation.result.wasThrown) {
			throw Exceptional.throwAsUnchecked((Throwable) invocation.result.value);
		}
		return invocation.result.value;
	}
}
