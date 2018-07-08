/*-
 * #%L
 * io.earcam.instrumental.agent
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
package io.earcam.instrumental.agent;

import java.net.URI;
import java.nio.file.Path;

import io.earcam.unexceptional.Exceptional;

/**
 * Utility to load a java agent at runtime
 *
 * TODO could use a wrapper and allow load/unload, then have in junit teardown..
 *
 */
public final class DynamicAgent {

	private DynamicAgent()
	{}


	/**
	 * If the <code>agentClass</code> is located on the file system then a stub jar is created,
	 * otherwise the jar from which <code>agentClass</code> was loaded is used
	 *
	 * @param agentClass the agent class with <code>agentmain</code>, <code>premain</code> methods
	 * @param agentArguments the '-A' prefixed whole argument string
	 * @return The jar resource, which may have been created
	 */
	public static Path loadAgent(Class<?> agentClass, String agentArguments)
	{
		Path sourceOfResource = StubAgentJar.jarForAgentClass(agentClass);
		loadAgent(sourceOfResource.toUri(), agentArguments);
		return sourceOfResource;
	}


	/**
	 * Load an existing agent jar file
	 *
	 * @param jar location of jar file containing agent
	 * @param agentArguments argument string to be supplied to agent
	 */
	public static void loadAgent(URI jar, String agentArguments)
	{
		Exceptional.accept(Attach::attach, jar, agentArguments);
	}
}
