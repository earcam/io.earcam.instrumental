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

import static io.earcam.instrumental.archive.AsJar.asJar;
import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.sign.StandardDigestAlgorithms.MD5;
import static io.earcam.instrumental.archive.sign.WithSignature.withSignature;
import static io.earcam.utilitarian.security.Certificates.certificate;
import static io.earcam.utilitarian.security.KeyStores.keyStore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;

import org.junit.jupiter.api.Test;

import io.earcam.acme.FortyTwo;
import io.earcam.instrumental.lade.ClassLoaders;
import io.earcam.instrumental.proxy.handler.NoopInvocationHandler;
import io.earcam.instrumental.proxy.handler.PartialInvocationHandler;
import io.earcam.utilitarian.security.Keys;

@SuppressWarnings("serial")
public class ProxyTest {

	// EARCAM_SNIPPET_BEGIN: awkward-proxy-target
	public static class Foo {

		public Foo()
		{
			throw new IllegalStateException();
		}


		public Foo(int unused)
		{}


		public String hello()
		{
			return "hello";
		}


		public String goodbye()
		{
			return "goodbye";
		}
	}
	// EARCAM_SNIPPET_END: awkward-proxy-target

	// EARCAM_SNIPPET_BEGIN: simple-proxy-handler
	public static class Handler extends PartialInvocationHandler<Foo> {

		public Handler(Foo delegate)
		{
			super(delegate);
		}


		public String goodbye()
		{
			return "not " + delegate.goodbye() + ", but farewell";
		}
	}
	// EARCAM_SNIPPET_END: simple-proxy-handler


	@Test
	public void partial()
	{
		// EARCAM_SNIPPET_BEGIN: simple-proxy-invocation
		Foo proxy = Proxy.createProxy(Foo.class, new Handler(new Foo(42)));

		assertThat(proxy.hello(), is(equalTo("hello")));
		assertThat(proxy.goodbye(), is(equalTo("not goodbye, but farewell")));
		// EARCAM_SNIPPET_END: simple-proxy-invocation
	}


	@Test
	public void meh()
	{
		ProxyTest proxy = Proxy.createProxy(ProxyTest.class, new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				return "toString".equals(method.getName()) ? "hello" : method.invoke(ProxyTest.this, args);
			}

		});

		assertThat(proxy.toString(), is(equalTo("hello")));
		assertThat(proxy.something(), is(equalTo("something")));
	}


	public String something()
	{
		return "something";
	}


	@Test
	public void cannotProxyFinalClassFromRT()
	{
		try {
			Proxy.createProxy(String.class, new NoopInvocationHandler());
			fail();
		} catch(IllegalArgumentException e) {

		}
	}


	@Test
	public void proxyAbstractClass()
	{
		// EARCAM_SNIPPET_BEGIN: proxy-abstract-class
		Number proxy = Proxy.createProxy(Number.class, new NoopInvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				return "doubleValue".equals(method.getName()) ? 42D : super.invoke(proxy, method, args);
			}
		});

		assertThat(proxy.doubleValue(), is(42D));
		// EARCAM_SNIPPET_END: proxy-abstract-class
	}


	@Test
	public void proxyClassForSignedPackage() throws SecurityException, ReflectiveOperationException, IOException
	{
		KeyPair keys = Keys.rsa();
		String alias = "alias";
		char[] password = "password".toCharArray();
		KeyStore keyStore = keyStore(alias, password, keys, certificate(keys, "subject").toX509());

		Path jar = archive()
				.configured(asJar()
						.sealing(p -> true))
				.configured(withSignature()
						.digestedBy(MD5)
						.store(keyStore)
						.alias(alias)
						.password(password))
				.with(FortyTwo.class)
				.to(Paths.get(".", "target", "proxyClassForSignedPackage.jar"));

		try(URLClassLoader loader = ClassLoaders.selfFirstClassLoader(jar)) {
			Class<?> loaded = loader.loadClass(FortyTwo.class.getCanonicalName());

			Object proxy = Proxy.createProxy(loaded, new NoopInvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
				{
					return "fortyTwo".equals(method.getName()) ? -1 : super.invoke(proxy, method, args);
				}
			});
			Method method = loaded.getMethod("fortyTwo");

			int returned = (int) method.invoke(proxy);

			assertThat(returned, is(equalTo(-1)));
		}
	}
}
