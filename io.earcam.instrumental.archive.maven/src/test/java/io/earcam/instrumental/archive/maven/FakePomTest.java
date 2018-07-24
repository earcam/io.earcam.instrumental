/*-
 * #%L
 * io.earcam.instrumental.archive.jpms
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
package io.earcam.instrumental.archive.maven;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class FakePomTest {

	@Test
	void withOneDependency() throws IOException
	{
		MavenArtifact artifact = new MavenArtifact("com.acme", "com.acme.api", "0.0.1", "jar", "");
		MavenArtifact[] dependencies = {
				new MavenArtifact("com.acme", "com.acme.annotation", "10.0.1", "jar", ""),
				new MavenArtifact("com.acme", "com.acme.regulation", "0.42.1", "jar", "")
		};
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		FakePom.createPom(artifact, dependencies, os);

		String pom = new String(os.toByteArray(), UTF_8);

		// @formatter:off
		String expected = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" + 
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" + 
				"	<modelVersion>4.0.0</modelVersion>\n" + 
				"\n" + 
				"	<groupId>com.acme</groupId>\n" + 
				"	<artifactId>com.acme.api</artifactId>\n" + 
				"	<version>0.0.1</version>\n" + 
				"	<packaging>jar</packaging>\n" + 
				"\n" + 
				"	<dependencies>\n" + 
				"		<dependency>\n" + 
				"			<groupId>com.acme</groupId>\n" + 
				"			<artifactId>com.acme.annotation</artifactId>\n" + 
				"			<version>10.0.1</version>\n" + 
				"		</dependency>\n" + 
				"		<dependency>\n" + 
				"			<groupId>com.acme</groupId>\n" + 
				"			<artifactId>com.acme.regulation</artifactId>\n" + 
				"			<version>0.42.1</version>\n" + 
				"		</dependency>\n" + 
				"	</dependencies>\n" + 
				"</project>\n";
		
		// @formatter:on

		assertThat(pom, is(equalToIgnoringWhiteSpace(expected)));
	}

}
