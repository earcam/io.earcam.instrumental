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

import static io.earcam.instrumental.agent.StubAgentJar.JAR_EXTENSION;
import static io.earcam.instrumental.agent.StubAgentJar.JAR_PREFIX;
import static io.earcam.instrumental.archive.AsAgentJar.asAgentJar;
import static io.earcam.instrumental.archive.Archive.archive;
import static java.nio.file.Files.isSameFile;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.earcam.instrumental.reflect.Resources;
import io.earcam.unexceptional.Exceptional;

public class DynamicAgentTest {

	private Path jarFile;


	@BeforeEach
	public void before() throws IOException
	{
		jarFile = Paths.get(".", "target", DynamicAgentTest.class.getCanonicalName(), randomUUID().toString(), "dynamic-agent-test_stub-agent.jar");
		jarFile.getParent().toFile().mkdirs();

		createStubAgentJar();
		StubAgentState.initialize();
	}


	private void createStubAgentJar()
	{
		archive()
				.configured(asAgentJar()
						.withAgentClass(StubAgent.class))
				.to(jarFile);
	}


	public static URI urlOfJarFile(Path file) throws IOException
	{
		return Exceptional.uri(new URL("jar:" + file.toUri().toURL().toExternalForm() + "!/").toExternalForm());
	}


	@Test
	public void loadAgentFromJar() throws IOException
	{
		URI jarUrl = urlOfJarFile(jarFile);
		// EARCAM_SNIPPET_BEGIN: agent-jar
		String agentArguments = "arbitrary.arguments=go.here";
		DynamicAgent.loadAgent(jarUrl, agentArguments);
		// EARCAM_SNIPPET_END: agent-jar
		assertThat(StubAgentState.agentMainInvoked, is(true));
		assertThat(StubAgentState.instrumentation, is(not(nullValue())));
		assertThat(StubAgentState.arguments, is(equalTo(agentArguments)));
	}


	@Test
	public void sourceOfResourceFromJar() throws ClassNotFoundException, IOException
	{
		try(URLClassLoader classLoader = new URLClassLoader(new URL[] { urlOfJarFile(jarFile).toURL() }, null)) {
			Class<?> loadedFromJar = classLoader.loadClass(StubAgent.class.getCanonicalName());

			String jarLocation = Resources.sourceOfResource(loadedFromJar, classLoader);

			assertThat(jarLocation, is(equalTo(jarFile.toAbsolutePath().toString())));
		}
	}


	@Test
	public void nullAgentClassThrowsRuntimeException()
	{
		try {
			Class<?> agentClass = null;
			DynamicAgent.loadAgent(agentClass, "");
			fail();
		} catch(RuntimeException e) {}
	}


	@Test
	public void nullAgentJarTriggersRuntimeException()
	{
		try {
			URI agentJar = null;
			DynamicAgent.loadAgent(agentJar, "");
			fail();
		} catch(RuntimeException e) {}
	}


	@Test
	public void loadAgentFromClassByCreatingStubJar() throws IOException
	{
		// EARCAM_SNIPPET_BEGIN: agent-class
		
		// Typically you'd need to launch the JVM with "-javaagent:<jarpath>[=<options>]"
		String agentArguments = "arbitrary.arguments=go.here";
		Path stubJar = DynamicAgent.loadAgent(StubAgent.class, agentArguments);
		// EARCAM_SNIPPET_END: agent-class

		assertThat(StubAgentState.agentMainInvoked, is(true));
		assertThat(StubAgentState.instrumentation, is(not(nullValue())));
		assertThat(StubAgentState.arguments, is(equalTo(agentArguments)));

		assertThat(stubJar.toString(), matchesPattern(".*" + JAR_PREFIX + "[A-Fa-f0-9\\-]+" + JAR_EXTENSION));
	}


	@Test
	public void loadAgentFromClassLoadedFromExistingJar() throws IOException, ClassNotFoundException
	{
		String agentArguments = "arbitrary.arguments=go.here";

		try(URLClassLoader classLoader = new URLClassLoader(new URL[] { urlOfJarFile(jarFile).toURL() }, null)) {

			Class<?> loadedFromJar = classLoader.loadClass(StubAgent.class.getCanonicalName());
			Path jarLocation = DynamicAgent.loadAgent(loadedFromJar, agentArguments);

			// FAILS ON OSX DUE TO SYMLINK: assertThat(jarLocation, is(equalTo(jarFile)));
			assertThat(isSameFile(jarLocation, jarFile), is(true));

			assertThat(StubAgentState.agentMainInvoked, is(true));
			assertThat(StubAgentState.instrumentation, is(not(nullValue())));
			assertThat(StubAgentState.arguments, is(equalTo(agentArguments)));
		}
	}
}
