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

import static io.earcam.instrumental.archive.AsAgentJar.asAgentJar;
import static io.earcam.instrumental.archive.Archive.archive;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import io.earcam.instrumental.reflect.Resources;
import io.earcam.unexceptional.Exceptional;

final class StubAgentJar {

	static final String JAR_PREFIX = "stub-agent-";
	static final String JAR_EXTENSION = ".jar";


	private StubAgentJar()
	{}


	static Path jarForAgentClass(Class<?> agentClass)
	{
		Path sourceOfResource = sourceOfResource(agentClass);
		if(!sourceOfResource.toFile().isFile()) {
			Path stubJar = Exceptional.apply(StubAgentJar::stubAgentJar, agentClass);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> StubAgentJar.removeStubAgentJar(stubJar)));
			sourceOfResource = stubJar;
		}
		return sourceOfResource;
	}


	private static Path sourceOfResource(Class<?> agentClass)
	{
		return Exceptional.apply(Path::toRealPath, Paths.get(Resources.sourceOfResource(agentClass)));
	}


	private static void removeStubAgentJar(Path sourceOfResource)
	{
		Exceptional.accept(Files::delete, sourceOfResource);
	}


	private static Path stubAgentJar(Class<?> agentClass)
	{
		return stubAgentJar(JAR_PREFIX, agentClass);
	}


	static Path stubAgentJar(String jarPrefix, Class<?> agentClass)
	{
		Path jar = Exceptional.apply(File::createTempFile, jarPrefix + UUID.randomUUID(), JAR_EXTENSION).toPath();
		return archive()
				.configured(
						asAgentJar()
								.withAgentClass(agentClass)
								.canRedefineClasses()
								.canRetransformClasses())
				.to(jar);
	}
}
