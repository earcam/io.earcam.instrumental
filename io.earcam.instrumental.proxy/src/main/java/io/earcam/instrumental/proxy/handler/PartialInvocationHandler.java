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

import static io.earcam.unexceptional.Exceptional.throwAsUnchecked;
import static io.earcam.unexceptional.Exceptional.unwrap;
import static java.lang.reflect.Modifier.PUBLIC;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Predicate;

import io.earcam.instrumental.proxy.Proxies;
import io.earcam.instrumental.reflect.Methods;

/**
 * Suffers from http://www.javaspecialists.eu/archive/Issue117.html - so don't try extending this as an anonymous
 * class..
 *
 *
 * Methods not defined on the concrete subclass class of
 * {@link io.earcam.instrumental.proxy.handler.PartialInvocationHandler}
 * will be invoked on the delegate.
 * Enforce type safety through common interfaces honouring ISP.
 *
 */
public abstract class PartialInvocationHandler<T> implements InvocationHandler {

	private final Method[] methods;
	protected final T delegate;


	/**
	 * <p>
	 * Constructor for PartialInvocationHandler.
	 * </p>
	 *
	 * @param delegate a T object.
	 */
	public PartialInvocationHandler(T delegate)
	{
		this.delegate = delegate;
		this.methods = allPublicMethods(getClass());
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		return Arrays.stream(methods)
				.filter(m -> matches(m, method.getName(), method.getReturnType(), method.getParameterTypes()))
				.findFirst()
				.map(m -> invoke(m, this, args))
				.orElseGet(() -> invoke(method, delegate, args));
	}

	private static class Pfft extends PartialInvocationHandler<Object> {

		public Pfft(Object delegate)
		{
			super(delegate);
		}
	}


	private Object invoke(Method method, Object target, Object[] args)
	{
		try {
			if(target == delegate && method.isDefault() && !this.getClass().isAssignableFrom(Pfft.class)) {
				return wrapDefaultInPfft(method, args);
			}

			if(this.getClass().isAssignableFrom(Pfft.class)) {
				return invokePfft(method, args);
			} else {
				return method.invoke(target, args);
			}
		} catch(InvocationTargetException e) {
			throw throwAsUnchecked(unwrap(e));
		} catch(Throwable e) {
			throw throwAsUnchecked(e);
		}
	}


	/**
	 * <p>
	 * wrapDefaultInPfft.
	 * </p>
	 *
	 * @param method a {@link java.lang.reflect.Method} object.
	 * @param args an array of {@link java.lang.Object} objects.
	 * @return a {@link java.lang.Object} object.
	 * @throws java.lang.Throwable if any.
	 */
	protected Object wrapDefaultInPfft(Method method, Object[] args) throws Throwable
	{
		MethodHandle methodHandle = Methods.handleFor(method);
		Object implementingProxy = Proxies.proxy(new Pfft(this), method.getDeclaringClass());
		return methodHandle.bindTo(implementingProxy).invokeWithArguments(args);
	}


	@SuppressWarnings("unchecked")
	private Object invokePfft(Method method, Object[] args) throws Throwable
	{
		return ((PartialInvocationHandler<Object>) delegate).invoke(void.class, method, args);
	}


	private static Method[] allPublicMethods(Class<?> type)
	{
		Predicate<? super Method> invocationHandlerInvokeMethod = m -> matches(m, "invoke", Object.class, Object.class, Method.class, Object[].class);

		return Methods.methodsOf(type)
				.filter(Methods.hasModifiers(PUBLIC))
				.filter(invocationHandlerInvokeMethod.negate())
				.toArray(s -> new Method[s]);
	}


	private static boolean matches(Method m, String name, Class<?> returnType, Class<?>... argumentTypes)
	{
		return m.getName().equals(name)
				&& m.getReturnType().equals(returnType)
				&& Arrays.equals(m.getParameterTypes(), argumentTypes);
	}
}
