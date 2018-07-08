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

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * An instrumentation agent that removes final modifiers from classes, fields and methods
 * for classes matching the regex provided as agent argument "{@value Agent#PREFIXES_ARGUMENT}".
 *
 */
public final class Agent {

	/** Constant <code>PREFIXES_ARGUMENT="prefixes"</code> */
	public static final String PREFIXES_ARGUMENT = "prefixes";

	static Instrumentation instrumentation;


	private Agent()
	{}


	/**
	 * <p>
	 * premain.
	 * </p>
	 *
	 * @param agentArgs a {@link java.lang.String} object.
	 * @param instrumentation a {@link java.lang.instrument.Instrumentation} object.
	 */
	public static void premain(String agentArgs, Instrumentation instrumentation)
	{
		agentmain(agentArgs, instrumentation);
	}


	/**
	 * <p>
	 * agentmain.
	 * </p>
	 *
	 * @param agentArgs a {@link java.lang.String} object.
	 * @param instrumentation a {@link java.lang.instrument.Instrumentation} object.
	 */
	public static void agentmain(String agentArgs, Instrumentation instrumentation)
	{
		Agent.instrumentation = instrumentation;
		instrumentation.addTransformer(new RemoveFinalTransformer(extractClassNameRegexArg(agentArgs)));
	}


	private static String extractClassNameRegexArg(String agentArgs)
	{
		checkArgumentStringForPrefixes(agentArgs);
		return agentArgs.replaceAll(".*" + PREFIXES_ARGUMENT + "=([^,]+).*", "$1");
	}


	private static void checkArgumentStringForPrefixes(String agentArgs)
	{
		if(agentArgs == null || !agentArgs.contains(PREFIXES_ARGUMENT + "=")) {
			throw new IllegalArgumentException(Agent.class.getCanonicalName() + " requires " + PREFIXES_ARGUMENT + "agent argument");
		}
	}


	/**
	 * <p>
	 * reinstrument.
	 * </p>
	 *
	 * @param classes a {@link java.lang.Class} object.
	 * @throws java.lang.instrument.UnmodifiableClassException if any.
	 */
	public static void reinstrument(Class<?>... classes) throws UnmodifiableClassException
	{
		instrumentation.retransformClasses(classes);
	}
}
