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

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.Objects;

import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * InstrumentationServiceProvider class.
 * </p>
 *
 */
public final class InstrumentationServiceProvider extends InstrumentationWrapper {

	static volatile Instrumentation instance;


	/**
	 * <p>
	 * Constructor for InstrumentationServiceProvider.
	 * </p>
	 */
	public InstrumentationServiceProvider()
	{
		attach();
		setDelegate(instance);
	}


	private static void attach()
	{
		if(instance == null) {
			Path agentJar = StubAgentJar.stubAgentJar("fake-agent-", FakeAgent.class);
			Exceptional.accept(Attach::attach, agentJar.toUri(), "");

			Objects.requireNonNull(instance);
		}
	}
}
