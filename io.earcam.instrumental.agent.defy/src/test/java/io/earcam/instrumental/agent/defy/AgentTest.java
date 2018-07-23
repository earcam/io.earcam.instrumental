/*-
 * #%L
 * io.earcam.instrumental.agent.defy
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
package io.earcam.instrumental.agent.defy;

import static io.earcam.instrumental.proxy.handler.NoopInvocationHandler.NOOP_INVOCATION_HANDLER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import io.earcam.acme.a.FinalClass;
import io.earcam.acme.a.InterfaceWithFinalField;
import io.earcam.acme.b.FinalClassB;
import io.earcam.acme.c.FinalClassC;
import io.earcam.instrumental.agent.junit.extension.AgentClass;
import io.earcam.instrumental.proxy.Proxies;
import io.earcam.unexceptional.Exceptional;

@AgentClass(value = Agent.class, arguments = "io.earcam.acme.a,io.earcam.acme.c")
public final class AgentTest {

	public static final Instrumentation MOCK_INSTRUMENTATION = Proxies.proxy(NOOP_INVOCATION_HANDLER, Instrumentation.class);


	@Test
	public void finalClassShouldNotBeFinal()
	{
		Class<?> clazz = loadClass("io.earcam.acme.a.FinalClass");
		assertFalse(Modifier.isFinal(clazz.getModifiers()));
	}


	@Test
	public void loadedFinalClassShouldNotBeFinal()
	{
		Class<?> clazz = FinalClass.class;
		assertFalse(Modifier.isFinal(clazz.getModifiers()));
	}


	private Class<?> loadClass(String name)
	{
		return Exceptional.apply(ClassLoader::loadClass, AgentTest.class.getClassLoader(), name);
	}


	@Test
	public void finalMethodShouldNotBeFinal() throws NoSuchMethodException, SecurityException
	{
		Class<?> clazz = loadClass("io.earcam.acme.a.FinalClass");
		assertFalse(Modifier.isFinal(clazz.getMethod("sayFoo").getModifiers()));
	}


	@Test
	public void finalFieldShouldNotBeFinal() throws NoSuchFieldException, SecurityException
	{
		Class<?> clazz = loadClass("io.earcam.acme.a.FinalClass");
		assertFalse(Modifier.isFinal(clazz.getField("field").getModifiers()));
	}


	@Test
	public void doesNotProduceInvalidInterfaces() throws NoSuchFieldException, SecurityException
	{
		assertTrue(Modifier.isFinal(InterfaceWithFinalField.class.getField("field").getModifiers()));
	}


	@Test
	public void finalSystemClassWillStillBeFinal()
	{
		Class<?> clazz = loadClass("java.lang.String");
		assertTrue(Modifier.isFinal(clazz.getModifiers()));
	}


	@Test
	public void otherFinalClassMatchedByPatternIsDefinalised()
	{
		Class<?> clazz = FinalClassC.class;
		assertFalse(Modifier.isFinal(clazz.getModifiers()));
	}


	@Test
	public void otherFinalClassNotMatchedByPatternIsStillFinal()
	{
		Class<?> clazz = FinalClassB.class;
		assertTrue(Modifier.isFinal(clazz.getModifiers()));
	}


	@Test
	public void agentThrowsIfArgumentIsNull()
	{
		try {
			Agent.premain(null, MOCK_INSTRUMENTATION);
			fail();
		} catch(IllegalArgumentException e) {}
	}


	@Test
	public void agentThrowsIfArgumentContainIllegalCharacters()
	{
		try {
			Agent.premain("com.acme.*", MOCK_INSTRUMENTATION);
			fail();
		} catch(IllegalArgumentException e) {}
	}


	@Test
	public void agentHoldsReferenceToInstrumentation()
	{
		Agent.premain("com.acme.boo", MOCK_INSTRUMENTATION);

		assertThat(Agent.instrumentation, is(equalTo(MOCK_INSTRUMENTATION)));
	}
}
