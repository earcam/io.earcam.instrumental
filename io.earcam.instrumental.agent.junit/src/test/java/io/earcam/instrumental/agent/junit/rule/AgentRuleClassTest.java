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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.instrument.Instrumentation;

import org.junit.Rule;
import org.junit.Test;

import com.acme.StubAgent;
import com.acme.StubAgentState;

import io.earcam.instrumental.agent.junit.AbstractAgentJarTest;

public class AgentRuleClassTest extends AbstractAgentJarTest {

	// EARCAM_SNIPPET_BEGIN: class
	@Rule
	public AgentRule rule = AgentRule.agent(StubAgent.class, AGENT_ARGUMENTS);
	// EARCAM_SNIPPET_END: class


	@Test
	public void test()
	{
		assertThat(StubAgentState.isAgentMainInvoked(), is(true));
		assertThat(StubAgentState.isPreMainInvoked(), is(false));
		assertThat(StubAgentState.instrumentation(), is(instanceOf(Instrumentation.class)));
		assertThat(StubAgentState.arguments(), contains(AGENT_ARGUMENTS));
	}
}
