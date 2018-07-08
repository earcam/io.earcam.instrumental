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
package io.earcam.instrumental.agent.junit.rule;

import java.nio.file.Path;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import io.earcam.instrumental.agent.DynamicAgent;

/**
 * A JUnit rule capable of adding a Java Agent dynamically.
 *
 * If the agent class provided is determined to have been loaded from a directory, then the assumption
 * is that you're testing an agent. In which case an temporary JAR is created and populated with
 * you're agent class and necessary MANIFEST.MF entries.
 *
 * Otherwise the agent class is considered to be loaded from an existing JAR. In which case this is
 * simply added to the running JVM.
 *
 * Notes:
 * <ul>
 *
 * <li>You should not reference any class intended to be instrumented as a static field or as argument to
 * the {@link io.earcam.instrumental.agent.junit.rule.AgentRule}. If this is unavoidable then you must invoke
 * reinstrumentation of the relevant classes.
 * However, it should be noted that, reinstrumentation would not be able to remove any class's final modifier</li>
 *
 * <li>Entirely dependent on the <a href=
 * "https://docs.oracle.com/javase/8/docs/jdk/api/attach/spec/com/sun/tools/attach/package-summary.html">
 * com.sun.tools.attach API</a></li>
 *
 * <li>Once loaded the agent is not unloaded, worth bearing in mind when reusing JVMs across tests</li>
 *
 * <li>Specifying the Agent class via the rule is nothing more than a convenience - the JVM will not
 * hold the same loaded definition of the Agent class as used in the tests, therefore attempts to share
 * state (e.g. via statics) will be in vain</li>
 *
 * <li>Dependencies for the Agent class should be provided at test scope or alternatively at runtime
 * scope with optional=true, if not already present</li>
 *
 * <li>This does not support native JVM TI agents</li>
 *
 * </ul>
 *
 */
public class AgentRule implements TestRule {

	private final Runnable attach;


	private AgentRule(Runnable attach)
	{
		this.attach = attach;
	}


	/**
	 * <p>
	 * Dynamically load an agent from the supplied jar file.
	 * </p>
	 *
	 * @param agentJarFile a {@link java.nio.file.Path} object.
	 * @param agentArguments a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.agent.junit.rule.AgentRule} object.
	 */
	public static AgentRule agent(Path agentJarFile, String agentArguments)
	{
		return new AgentRule(() -> DynamicAgent.loadAgent(agentJarFile.toUri(), agentArguments));
	}


	/**
	 * <p>
	 * Dynamically load an agent from the supplied class (created a JAR behind the scenes).
	 * </p>
	 *
	 * @param agentClass a {@link java.lang.Class} object.
	 * @param agentArguments a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.agent.junit.rule.AgentRule} object.
	 */
	public static AgentRule agent(Class<?> agentClass, String agentArguments)
	{
		return new AgentRule(() -> DynamicAgent.loadAgent(agentClass, agentArguments));
	}


	/** {@inheritDoc} */
	@Override
	public Statement apply(Statement base, Description description)
	{
		attach.run();
		return base;
	}
}
