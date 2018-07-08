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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An example of the {@link AbstractAroundInvocationHandler} using SLF4J
 * 
 * Serializable IFF the {@link #logLevel} supplier is
 *
 * @param <T> the proxy's target
 */
public class LoggingInvocationHandler<T> extends AbstractAroundInvocationHandler<Long, T> {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingInvocationHandler.class);

	private Supplier<Level> logLevel;

	/**
	 * Generic log level
	 */
	public enum Level {
		TRACE(LOG::trace), DEBUG(LOG::debug), INFO(LOG::info), WARN(LOG::warn), ERROR(LOG::error);

		private final BiConsumer<String, Object[]> logCall;


		Level(BiConsumer<String, Object[]> logCall)
		{
			this.logCall = logCall;
		}


		public void log(String message, Object... parameters)
		{
			logCall.accept(message, parameters);
		}
	}


	/**
	 * @param proxied underlying target
	 * @param logLevelSupplier allows logging level to be varied
	 */
	public LoggingInvocationHandler(T proxied, Supplier<Level> logLevelSupplier)
	{
		super(proxied);
		this.logLevel = logLevelSupplier;
	}


	@Override
	protected Long preInvoke(Invocation invocation)
	{
		return System.nanoTime();
	}


	@Override
	protected void postInvoke(Invocation invocation, Long durationNs)
	{
		logLevel.get().log("invoked {} with args {}, {} took {}ns", methodToString(invocation), argsToString(invocation), resultToString(invocation.result),
				durationNs);
		if(invocation.result.wasThrown) {
			logException((Throwable) invocation.result.value);
		}
	}


	protected String methodToString(Invocation invocation)
	{
		Method method = invocation.method;
		return method.getDeclaringClass().getName() + "#" + method.getName() + "(" + Arrays.toString(method.getParameterTypes()) + ")";
	}


	protected String argsToString(Invocation invocation)
	{
		return Arrays.toString(invocation.args);
	}


	private String resultToString(InvocationResult result)
	{
		return result.wasThrown ? "threw " + result.value.getClass().getCanonicalName() : "returned " + result.value;
	}


	private void logException(Throwable thrown)
	{
		LOG.warn("proxy logging", (thrown instanceof InvocationTargetException) ? ((InvocationTargetException) thrown).getTargetException() : thrown);
	}
}
