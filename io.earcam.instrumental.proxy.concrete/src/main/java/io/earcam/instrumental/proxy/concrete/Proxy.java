/*-
 * #%L
 * io.earcam.instrumental.proxy.concrete
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
package io.earcam.instrumental.proxy.concrete;

import java.lang.reflect.InvocationHandler;

import org.objenesis.ObjenesisStd;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.NoOp;

/**
 * Using Objenesis to avoid cgib invoking parent constructors
 *
 * see http://objenesis.org
 * see https://github.com/cglib/cglib
 *
 */
public final class Proxy {

	private static final ObjenesisStd OBJENESIS = new ObjenesisStd();


	private Proxy()
	{}


	/**
	 * <p>
	 * createProxy.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @param handler a {@link java.lang.reflect.InvocationHandler} object.
	 * @param <T> a T object.
	 * @return a T object.
	 */
	public static <T> T createProxy(Class<T> type, InvocationHandler handler)
	{
		T proxy = newInstanceOf(proxyClassFrom(type));
		setCallbacks(proxy, handler);
		return proxy;
	}


	private static <T> Class<? extends T> proxyClassFrom(Class<T> type)
	{
		try {
			return createEnhancer(type).createClass();
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("Cannot create proxy for " + type.getName(), e);
		}
	}


	private static <T> ConstructorIgnorantEnhancer<T> createEnhancer(Class<T> type)
	{
		ConstructorIgnorantEnhancer<T> enhancer = new ConstructorIgnorantEnhancer<>();
		enhancer.setSuperclass(type);
		enhancer.setCallbackType(net.sf.cglib.proxy.InvocationHandler.class);
		SignedPackageRenamingPolicy.apply(enhancer, type);
		return enhancer;
	}


	private static <T> T newInstanceOf(Class<T> type)
	{
		return OBJENESIS.newInstance(type);
	}


	private static <T> void setCallbacks(T proxy, InvocationHandler handler)
	{
		net.sf.cglib.proxy.InvocationHandler cglibHandler = handler::invoke;
		((Factory) proxy).setCallbacks(new Callback[] { cglibHandler, NoOp.INSTANCE });
	}
}
