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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.earcam.instrumental.proxy.concrete.Proxy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;

public class CglibLazyLoaderTest {

	public static class DummyInstance {
		private static final ThreadLocal<AtomicInteger> constructorInvocations = ThreadLocal
				.withInitial(AtomicInteger::new);


		public DummyInstance()
		{
			constructorInvocations.get().incrementAndGet();
		}


		public static int getConstructorInvocations()
		{
			return constructorInvocations.get().get();
		}


		public static void resetConstructorInvocations()
		{
			constructorInvocations.get().set(0);
		}
	}


	@BeforeEach
	public void before()
	{
		DummyInstance.resetConstructorInvocations();
	}


	@Test
	public void testCgLibLazyLoaderWithObjenesis()
	{
		InvocationHandler handler = new InvocationHandler() {

			private volatile DummyInstance actualInstance;


			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				load();
				return method.invoke(actualInstance, args);
			}


			private void load()
			{
				if(actualInstance == null) {
					actualInstance = new DummyInstance() {
						{
							// System.out.println("CglibLazyLoaderTest.testCgLibLazyLoaderWithObjenesis()");
							// System.out.println("Genuine constructor called above..., incs: " + getConstructorInvocations());
						}
					};
				}
			}
		};
		assertThat(DummyInstance.getConstructorInvocations(), is(0));

		DummyInstance lazy = Proxy.createProxy(DummyInstance.class, handler);

		assertThat(DummyInstance.getConstructorInvocations(), is(0)); // <<<---- still zero!

		lazy.toString();

		assertThat(DummyInstance.getConstructorInvocations(), is(1));

		lazy.toString();

		assertThat(DummyInstance.getConstructorInvocations(), is(1));
	}


	@Test
	public void testCgLibLazyLoaderInvokesParentConstructor()
	{
		LazyLoader lazyLoader = new LazyLoader() {

			private volatile DummyInstance actualInstance;


			@Override
			public Object loadObject() throws Exception
			{
				// System.out.println("CglibLazyLoaderTest.testCgLibLazyLoaderInvokesParentConstructor().new LazyLoader()
				// {...}.loadObject()");
				load();
				return actualInstance;
			}


			private void load()
			{
				if(actualInstance == null) {
					actualInstance = new DummyInstance() {
						{
							// System.out.println("CglibLazyLoaderTest.testCgLibLazyLoaderInvokesParentConstructor()");
							// System.out.println("Genuine constructor called above..., incs: " + getConstructorInvocations());
						}
					};
				}
			}
		};
		assertThat(DummyInstance.getConstructorInvocations(), is(0));

		DummyInstance lazy = (DummyInstance) Enhancer.create(DummyInstance.class, lazyLoader);

		assertThat(DummyInstance.getConstructorInvocations(), is(1));  // <<<<---- oh noes, parent called

		lazy.toString();

		assertThat(DummyInstance.getConstructorInvocations(), is(2));

		lazy.toString();

		assertThat(DummyInstance.getConstructorInvocations(), is(2));
	}
}
