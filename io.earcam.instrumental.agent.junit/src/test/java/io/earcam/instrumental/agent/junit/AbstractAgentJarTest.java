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
package io.earcam.instrumental.agent.junit;

import static io.earcam.instrumental.archive.AsAgentJar.asAgentJar;
import static io.earcam.instrumental.archive.Archive.archive;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.acme.StubAgent;
import com.acme.StubAgentState;

// FIXME this is dodgy - fails in parallel exec of surefire
public abstract class AbstractAgentJarTest {

	// EARCAM_SNIPPET_BEGIN: setup
	protected static final String DIR = "target";
	protected static final String FILE = "AbstractAgentJarTest-stubagent.jar";

	public static final String JAR_NAME = DIR + '/' + FILE;
	public static final Path JAR_PATH = Paths.get(DIR, FILE);

	public static final String AGENT_ARGUMENTS = "-Awinter=socks";
	// EARCAM_SNIPPET_END: setup


	@BeforeClass
	@BeforeAll
	public static void begin()
	{
		archive()
				.configured(asAgentJar().withAgentClass(StubAgent.class))
				.to(JAR_PATH);
	}


	@AfterClass
	@AfterAll
	public static void reset()
	{
		StubAgentState.initialize();
	}
}
