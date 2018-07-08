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
package io.earcam.instrumental.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * A façade for a less ugly {@link java.lang.reflect.Proxy}
 *
 */
public final class Proxies {

	private Proxies()
	{}


	/**
	 * <p>
	 * proxy.
	 * </p>
	 *
	 * @see #proxy(ClassLoader, InvocationHandler, Class, Class...) uses
	 * {@link java.lang.ClassLoader#getSystemClassLoader()}
	 * @param handler the {@link java.lang.reflect.InvocationHandler}
	 * @param firstInterface <code>return</code> will be cast to this interface
	 * @param otherInterfaces optional additional interfaces
	 * @param <T> a T object.
	 * @return a T object.
	 */
	public static <T> T proxy(InvocationHandler handler, Class<T> firstInterface, Class<?>... otherInterfaces)
	{
		return proxy(ClassLoader.getSystemClassLoader(), handler, firstInterface, otherInterfaces);
	}


	/**
	 * Neater form of {@link java.lang.reflect.Proxy#newProxyInstance(ClassLoader, Class[], InvocationHandler)}
	 *
	 * @param loader the {@link java.lang.ClassLoader} to use
	 * @param handler the {@link java.lang.reflect.InvocationHandler}
	 * @param firstInterface <code>return</code> will be cast to this interface
	 * @param otherInterfaces optional additional interfaces
	 * @param <T> a T object.
	 * @return a T object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T proxy(ClassLoader loader, InvocationHandler handler, Class<T> firstInterface, Class<?>... otherInterfaces)
	{
		Class<?>[] interfaces = Arrays.copyOf(otherInterfaces, otherInterfaces.length + 1);
		interfaces[interfaces.length - 1] = firstInterface;
		return (T) Proxy.newProxyInstance(loader, interfaces, handler);
	}


	/**
	 * <p>
	 * identityToStringOf.
	 * </p>
	 *
	 * @param target a {@link java.lang.Object} object.
	 * @return the hex encoded system identity hash code
	 */
	public static String identityToStringOf(Object target)
	{
		return Integer.toHexString(System.identityHashCode(target));
	}
}
