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

import static io.earcam.utilitarian.log.slf4j.Level.INFO;
import static io.earcam.utilitarian.log.slf4j.Level.TRACE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.earcam.instrumental.proxy.Proxies;
import io.earcam.instrumental.proxy.handler.AroundInvocationHandler.Invocation;
import io.earcam.utilitarian.log.slf4j.Logging;

@NotThreadSafe
public class LoggingInvocationHandlerTest {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingInvocationHandlerTest.class);

	private static interface Api {
		String something(int number);
	}

	private static class Imp implements Api {

		public String something(int number)
		{
			return Integer.toString(number);
		}
	}

	private static class BadImp implements Api {

		public String something(int number)
		{
			throw new IllegalArgumentException(generateMessage(number));
		}


		public static String generateMessage(int number)
		{
			return "I don't like " + number;
		}
	}


	@BeforeAll
	public static void setUp()
	{
		Logging.logging().defaultLevel(INFO)
				.log(LoggingInvocationHandler.class).at(TRACE);

		LOG.trace("need to seed logging - but you won't see this");  // FIXME move necessity to seed logging into io.earcam.utilitarian.log.slf4j.Logging
	}


	@Test
	public void throughLogCapture()
	{
		String captured = Logging.capture(() -> {

			Imp proxied = new Imp();
			LoggingInvocationHandler<Api> handler = new LoggingInvocationHandler<>(proxied, () -> LoggingInvocationHandler.Level.TRACE);

			Api proxy = Proxies.proxy(handler, Api.class);
			assertThat(proxy.something(42), is(equalTo("42")));
		});

		assertThat(captured.trim(), allOf(
				containsString("TRACE"),
				containsString("something"),
				containsString("42"),
				endsWith("ns"),

				not(containsString("ERROR")),
				not(containsString("WARN"))));
	}


	@Test
	public void durationInNanosecondsPassedThroughAsContext() throws Exception
	{
		Imp proxied = new Imp();
		long durationNs = 123459876L;
		Invocation invocation = new Invocation(proxied, Imp.class.getMethod("something", int.class), 101);
		String captured = Logging.capture(() -> {

			LoggingInvocationHandler<Api> handler = new LoggingInvocationHandler<>(proxied, () -> LoggingInvocationHandler.Level.DEBUG);
			handler.postInvoke(invocation, durationNs);
		});

		assertThat(captured.trim(), allOf(
				containsString("DEBUG"),
				containsString("something"),
				containsString("101"),
				endsWith(durationNs + "ns"),

				not(containsString("ERROR")),
				not(containsString("WARN"))));
	}


	@Test
	public void logsThrowAndPropagates() throws Exception
	{
		String captured = Logging.capture(() -> {
			try {
				BadImp proxied = new BadImp();
				LoggingInvocationHandler<Api> handler = new LoggingInvocationHandler<>(proxied, () -> LoggingInvocationHandler.Level.INFO);

				Api proxy = Proxies.proxy(handler, Api.class);
				proxy.something(42);
				fail();
			} catch(RuntimeException e) {
				assertThat(e.getCause(), instanceOf(InvocationTargetException.class));
				assertThat(((InvocationTargetException) e.getCause()).getTargetException(), instanceOf(IllegalArgumentException.class));
			}
		});

		assertThat(captured.trim(), allOf(
				containsString("INFO"),
				containsString("something"),
				containsString("[42]"),

				not(containsString("ERROR")),
				containsString("WARN"),
				containsString(BadImp.generateMessage(42))));
	}


	@Test
	public void logsProxyInvocationOnNullTargetAttempt() throws Exception
	{
		String captured = Logging.capture(() -> {
			try {
				Api proxied = null;
				LoggingInvocationHandler<Api> handler = new LoggingInvocationHandler<>(proxied, () -> LoggingInvocationHandler.Level.INFO);

				Api proxy = Proxies.proxy(handler, Api.class);
				proxy.something(42);
				fail();
			} catch(RuntimeException e) {
				assertThat(e, instanceOf(NullPointerException.class));
			}
		});
		assertThat(captured.trim(), allOf(
				containsString("something"),
				containsString("[42]"),

				not(containsString("ERROR")),
				containsString("WARN"),
				containsString(NullPointerException.class.getCanonicalName())));
	}
}
