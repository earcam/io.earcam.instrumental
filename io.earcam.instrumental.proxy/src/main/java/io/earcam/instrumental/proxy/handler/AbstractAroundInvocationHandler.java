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

/**
 * Simple around invoke proxy invocation handler.
 *
 * See io.earcam.instrumental.proxy.handler.LoggingInvocationHandler in the test sources for example usage.
 *
 * @param <X> the context object, passed between pre an post invoke method
 * @param <T> the type of the proxied target
 */
public abstract class AbstractAroundInvocationHandler<X, T> extends AroundInvocationHandler<X, T> {

	/**
	 * <p>
	 * Constructor for AbstractAroundInvocationHandler.
	 * </p>
	 *
	 * @param proxied a T object.
	 */
	public AbstractAroundInvocationHandler(T proxied)
	{
		super(proxied);
		pre = this::preInvoke;
		post = this::postInvoke;
	}


	/**
	 * <p>
	 * preInvoke.
	 * </p>
	 *
	 * @param invocation a Invocation object.
	 * @return a X object.
	 */
	protected abstract X preInvoke(Invocation invocation);


	/**
	 * <p>
	 * postInvoke.
	 * </p>
	 *
	 * @param invocation a Invocation object.
	 * @param context a X object.
	 */
	protected abstract void postInvoke(Invocation invocation, X context);
}
