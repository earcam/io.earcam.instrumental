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
import java.util.Objects;
import java.util.function.IntPredicate;

/**
 * <p>
 * An instrumentation agent that removes final modifiers from classes, fields and methods
 * for classes matching the agent's argument.
 * </p>
 * <p>
 * The value supplied to as the agent argument is a comma-separated list
 * of package names or qualified class names.
 * These are matched by invocations to {@link String#startsWith(String)}
 * </p>
 *
 */
public final class Agent {

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
		instrumentation.addTransformer(new RemoveFinalTransformer(extractPrefixes(agentArgs)));
	}


	private static String[] extractPrefixes(String agentArgs)
	{
		IntPredicate invalid = Character::isJavaIdentifierPart;
		invalid = invalid.or(c -> c == '.').or(c -> c == ',').negate();

		boolean illegalChars = agentArgs == null || agentArgs.codePoints()
				.anyMatch(invalid);

		if(illegalChars) {
			throw new IllegalArgumentException("Agent args must only contain valid Java identifier chars and comma, recieved: " + Objects.toString(agentArgs));
		}

		return agentArgs.replace('.', '/').split(",");
	}
}
