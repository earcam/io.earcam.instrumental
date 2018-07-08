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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.agent.StubAgentJar;

public class StubAgentJarTest {

	@Test
	public void stubAgentJarDoesNotCreateTempJarIfJarExists()
	{
		Path jarForJunit = StubAgentJar.jarForAgentClass(Test.class);

		assertThat(jarForJunit, hasToString(matchesPattern(".*junit\\-jupiter\\-api\\-[0-9\\.]+(\\-[a-zA-Z0-9]+)?\\.jar")));
	}


	@Test
	public void stubAgentJarCreatesTempJarIfJarDoesNotExist() throws Exception
	{
		try(URLClassLoader loader = new URLClassLoader(new URL[] { Paths.get("src", "test").toUri().toURL() })) {

			Class<?> stubAgentClass = loader.loadClass(StubAgent.class.getCanonicalName());
			Path jarForJunit = StubAgentJar.jarForAgentClass(stubAgentClass);

			assertThat(jarForJunit, hasToString(matchesPattern(".*" + StubAgentJar.JAR_PREFIX + "[A-Fa-f0-9\\-]+\\.jar")));
		}
	}
}
