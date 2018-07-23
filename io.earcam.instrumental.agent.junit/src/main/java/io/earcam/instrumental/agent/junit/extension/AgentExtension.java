/*-
 * #%L
 * io.earcam.instrumental.agent.junit
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
package io.earcam.instrumental.agent.junit.extension;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import io.earcam.instrumental.agent.DynamicAgent;
import io.earcam.unexceptional.Exceptional;

/**
 * JUnit 5 extension allowing dynamic use of Java instrumentation agent in unit tests
 *
 */
public class AgentExtension implements BeforeEachCallback, BeforeAllCallback {

	Store store;


	@Override
	public void beforeEach(ExtensionContext context) throws Exception
	{
		before(context);
	}


	private void before(ExtensionContext context)
	{
		store = context.getStore(Namespace.create(AgentExtension.class.getCanonicalName()));
		AnnotatedElement element = context.getElement().orElseThrow(() -> new ExtensionConfigurationException("Unable to access annotated element"));
		agents(element.getAnnotationsByType(AgentClass.class));
		jars(element.getAnnotationsByType(AgentJar.class));
	}


	private void agents(AgentClass[] agents)
	{
		Arrays.stream(agents).forEach(this::agent);
	}


	private void agent(AgentClass agent)
	{
		if(store.get(agent.value()) == null) {
			DynamicAgent.loadAgent(agent.value(), agent.arguments());
			store.put(agent.value(), agent.arguments());
		}
	}


	private void jars(AgentJar[] jars)
	{
		Arrays.stream(jars).forEach(this::jar);
	}


	private void jar(AgentJar jar)
	{
		if(store.get(jar.value()) == null) {
			DynamicAgent.loadAgent(Exceptional.uri(jar.value()), jar.arguments());
			store.put(jar.value(), jar.arguments());
		}
	}


	@Override
	public void beforeAll(ExtensionContext context) throws Exception
	{
		before(context);
	}
}
