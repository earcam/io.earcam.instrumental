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

import static java.util.Collections.unmodifiableMap;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.earcam.instrumental.proxy.Proxies;

/**
 * Use the static instance {@link io.earcam.instrumental.proxy.handler.NoopInvocationHandler#NOOP_INVOCATION_HANDLER}.
 * Create instances only where you
 * want/need
 * the {@link java.lang.Object#toString()} to be distinct.
 *
 * If near-NOOP functionality required, then compose
 * {@link io.earcam.instrumental.proxy.handler.NoopInvocationHandler#NOOP_INVOCATION_HANDLER} as
 * the {@code delegate} for {@link io.earcam.instrumental.proxy.handler.PartialInvocationHandler}.
 *
 * Not quite NOOP:
 * 1. honours equals by reference of proxy
 * 2. honours hashCode as system identity of proxy
 * 3. toString outputs system identity of proxy and this instance of
 * {@link io.earcam.instrumental.proxy.handler.NoopInvocationHandler}
 * 4. returns default values for primitives and <code>null</code> for {@link java.lang.Object}s
 * 5. Does NOT honour default methods on interfaces - this is achievable via composition with the
 * {@link io.earcam.instrumental.proxy.handler.PartialInvocationHandler}
 *
 */
public class NoopInvocationHandler implements InvocationHandler, Serializable {

	private static final long serialVersionUID = 3305923532694020975L;

	/** Constant <code>NOOP_INVOCATION_HANDLER</code> */
	public static final NoopInvocationHandler NOOP_INVOCATION_HANDLER = new NoopInvocationHandler();

	private static final Map<Class<?>, Object> PRIMITIVE_TO_DEFAULT_VALUE;
	static {
		Map<Class<?>, Object> tmp = new HashMap<>();
		tmp.put(int.class, 0);
		tmp.put(char.class, '\0');
		tmp.put(short.class, (short) 0);
		tmp.put(long.class, 0L);
		tmp.put(double.class, 0D);
		tmp.put(byte.class, (byte) 0);
		tmp.put(boolean.class, false);

		PRIMITIVE_TO_DEFAULT_VALUE = unmodifiableMap(tmp);
	}


	/** {@inheritDoc} */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if(isEquals(method)) {
			return proxy == args[0];
		}
		if(isHashCode(method)) {
			return System.identityHashCode(proxy);
		}
		if(isToString(method)) {
			return proxiedToString(proxy);
		}
		return safeReturn(method.getReturnType());
	}


	private boolean isEquals(Method method)
	{
		return matches(method, "equals", boolean.class, Object.class);
	}


	private boolean matches(Method method, String name, Class<?> returnType, Class<?>... parameters)
	{
		return method.getName().equals(name)
				&& method.getReturnType().equals(returnType)
				&& Arrays.equals(method.getParameterTypes(), parameters);
	}


	private boolean isHashCode(Method method)
	{
		return matches(method, "hashCode", int.class);
	}


	private boolean isToString(Method method)
	{
		return matches(method, "toString", String.class);
	}


	private String proxiedToString(Object proxy)
	{
		return new StringBuilder()
				.append(Proxy.class.getName())
				.append('(')
				.append(toString())
				.append(")@")
				.append(Proxies.identityToStringOf(proxy))
				.toString();
	}


	private Object safeReturn(Class<?> returnType)
	{
		return PRIMITIVE_TO_DEFAULT_VALUE.getOrDefault(returnType, null);
	}
}
