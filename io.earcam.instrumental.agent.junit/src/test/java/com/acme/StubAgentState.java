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
package com.acme;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public final class StubAgentState {

	static volatile Instrumentation instrumentation;
	static final List<String> arguments = new ArrayList<>();
	static volatile int preMainInvocations;
	static volatile int agentMainInvocations;


	private StubAgentState()
	{}


	public static void initialize()
	{
		agentMainInvocations = preMainInvocations = 0;
		instrumentation = null;
		arguments.clear();
	}


	public static Instrumentation instrumentation()
	{
		return instrumentation;
	}


	public static List<String> arguments()
	{
		return arguments;
	}


	public static boolean isPreMainInvoked()
	{
		return preMainInvocations > 0;
	}


	public static boolean isAgentMainInvoked()
	{
		return agentMainInvocations > 0;
	}


	public static int preMainInvocations()
	{
		return preMainInvocations;
	}


	public static int agentMainInvocations()
	{
		return agentMainInvocations;
	}
}
