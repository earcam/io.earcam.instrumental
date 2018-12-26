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

import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.AsJar.asJar;
import static io.earcam.instrumental.archive.maven.Maven.CENTRAL_ID;
import static io.earcam.instrumental.archive.maven.Maven.CENTRAL_URL;
import static io.earcam.instrumental.archive.maven.Maven.DEFAULT_TYPE;
import static io.earcam.instrumental.archive.maven.Maven.ID_LOCAL_AS_REMOTE;
import static io.earcam.instrumental.archive.maven.Maven.maven;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.aFileNamed;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.eclipse.aether.repository.RemoteRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.Archive;
import io.earcam.unexceptional.Exceptional;

public class MavenTest {

	@Nested
	public class UsingCustomLocalRepository {

		@Test
		public void givenExistingDirectory()
		{
			Path path = Paths.get(".", "src", "test", "resources");

			Maven maven = maven().usingLocal(path);

			assertThat(maven.local.getBasedir(), is(equalTo(path.toAbsolutePath().toFile())));
		}


		@Test
		public void givenNonExistentDirectory()
		{
			Path path = Paths.get(".", "src", "test", "resources", "THIS_DOES_NOT_EXIST");

			Maven maven = maven().usingLocal(path);

			assertThat(maven.local.getBasedir(), is(equalTo(path.toAbsolutePath().toFile())));
		}


		@Test
		public void throwsWhenNotADirectory()
		{
			Path path = Paths.get(".", "pom.xml");
			try {
				maven().usingLocal(path);
				fail();
			} catch(IllegalArgumentException e) {}
		}
	}


	private void runWithFakeUserHome(String fakeHome, Runnable runnable)
	{
		String realHome = System.getProperty("user.home");
		try {
			System.setProperty("user.home", fakeHome);
			runnable.run();
		} finally {
			System.setProperty("user.home", realHome);
		}
	}

	@Nested
	public class UsingDefaultLocalRepository {

		@Test
		public void customLocalRepositoryFoundInSettings()
		{
			String fake = Paths.get(".", "src", "test", "resources", "fake.user.home", "local.in.settings").toAbsolutePath().toString();
			runWithFakeUserHome(fake, () -> {
				Maven maven = maven()
						.usingDefaultLocal();

				assertThat(maven.local.getBasedir(), aFileNamed(endsWith("nonstandard.repository")));
			});
		}


		@Test
		public void givenCustomLocalRepositoryFoundInSettingsWhenNotADirectoryThenThrows()
		{
			String fake = Paths.get(".", "src", "test", "resources", "fake.user.home", "bad.local.in.settings").toAbsolutePath().toString();
			runWithFakeUserHome(fake, () -> {
				try {
					maven().usingDefaultLocal();
					fail();
				} catch(IllegalStateException e) {}
			});
		}


		@Test
		public void givenAbsolutePathForCustomLocalRepositoryFoundInSettingsWhenNotADirectoryThenThrows()
		{
			String fake = Paths.get(".", "src", "test", "resources", "fake.user.home", "bad.abs.local.in.settings").toAbsolutePath().toString();
			runWithFakeUserHome(fake, () -> {
				try {
					maven().usingDefaultLocal();
					fail();
				} catch(IllegalStateException e) {}
			});
		}


		@Test
		public void givenNoSettingsAndNoDefaultLocalRepositoryThenThrows()
		{
			String fake = Paths.get(".", "src", "test", "resources", "fake.user.home", "no.local.no.settings").toAbsolutePath().toString();
			runWithFakeUserHome(fake, () -> {
				try {
					maven().usingDefaultLocal();
					fail();
				} catch(IllegalStateException e) {}
			});
		}


		@Test
		public void givenNoLocalRepositoryFoundSettingsWhenDefaultLocalIsPresentThenDefaultUsed()
		{
			Path path = Paths.get(".", "src", "test", "resources", "fake.user.home", "no.local.in.settings").toAbsolutePath();
			String fake = path.toString();
			File expected = path.resolve(Paths.get(".m2", "repository")).toFile();
			runWithFakeUserHome(fake, () -> {
				Maven maven = maven().usingDefaultLocal();

				assertThat(maven.local.getBasedir(), is(equalTo(expected)));
			});
		}


		@Test
		public void givenNoSettingsWhenDefaultRepositoryPresentThenUsed()
		{
			Path path = Paths.get(".", "src", "test", "resources", "fake.user.home", "local.no.settings").toAbsolutePath();

			File expected = path.resolve(Paths.get(".m2", "repository")).toFile();
			String fake = path.toString();
			runWithFakeUserHome(fake, () -> {
				Maven maven = maven()
						.usingDefaultLocal();

				assertThat(maven.local.getBasedir(), is(equalTo(expected)));
			});
		}
	}

	@Nested
	public class UsingRemoteRepository {

		@Test
		public void usingCentral()
		{
			Maven maven = maven().usingCentral();

			RemoteRepository expected = new RemoteRepository.Builder(CENTRAL_ID, DEFAULT_TYPE, CENTRAL_URL).build();

			assertThat(maven.remotes, contains(expected));
		}


		@Test
		public void usingCustom()
		{
			Maven maven = maven().usingRemote("id", DEFAULT_TYPE, "http://repo.acme.corp/maven");

			RemoteRepository expected = new RemoteRepository.Builder("id", DEFAULT_TYPE, "http://repo.acme.corp/maven").build();

			assertThat(maven.remotes, contains(expected));
		}


		@Test
		public void usingMultiple()
		{
			Maven maven = maven()
					.usingCentral()
					.usingRemote("a", DEFAULT_TYPE, "http://repo.acme.corp/a")
					.usingRemote("b", DEFAULT_TYPE, "http://repo.acme.corp/b");

			RemoteRepository a = new RemoteRepository.Builder("a", DEFAULT_TYPE, "http://repo.acme.corp/a").build();
			RemoteRepository b = new RemoteRepository.Builder("b", DEFAULT_TYPE, "http://repo.acme.corp/b").build();
			RemoteRepository c = new RemoteRepository.Builder(CENTRAL_ID, DEFAULT_TYPE, CENTRAL_URL).build();

			assertThat(maven.remotes, containsInAnyOrder(a, b, c));
		}

	}


	@Test
	void useALocalAsARemote() throws Exception
	{
		Path fakeHome = Paths.get(".", "target", "local-as-remote-fake-home").toAbsolutePath();
		fakeHome.resolve(Paths.get(".m2", "repository")).toFile().mkdirs();

		runWithFakeUserHome(fakeHome.toString(), () -> {
			maven()
					.usingCentral()
					.usingDefaultLocal()
					.dependencies("junit:junit:4.12");

			Path localPath = Paths.get(".", "target", "repo-local-from-local-as-remote");

			Map<MavenArtifact, Archive> dependencies = maven()
					.usingDefaultLocalAsARemote()
					.usingLocal(localPath)
					.dependencies("junit:junit:4.12");

			MavenArtifact junit = new MavenArtifact("junit", "junit", "4.12", "jar", "");
			MavenArtifact hamcrest = new MavenArtifact("org.hamcrest", "hamcrest-core", "1.3", "jar", "");

			assertThat(dependencies, allOf(
					aMapWithSize(2),
					hasKey(junit),
					hasKey(hamcrest)));

			Path remotesIndex = localPath.resolve(Paths.get("junit", "junit", "4.12", "_remote.repositories"));
			String meta = new String(Exceptional.apply(Files::readAllBytes, remotesIndex), UTF_8);

			assertThat(meta, allOf(
					containsString(ID_LOCAL_AS_REMOTE),
					not(containsString("central"))));
		});
	}


	@Test
	void dependenciesToArchives() throws Exception
	{
		// EARCAM_SNIPPET_BEGIN: resolve-dependencies
		Map<MavenArtifact, Archive> dependencies = maven()
				.usingCentral()
				.usingLocal(Paths.get(".", "target", "repo-resolve-depgraph"))
				.dependencies("junit:junit:4.12");

		MavenArtifact junit = new MavenArtifact("junit", "junit", "4.12", "jar", "");
		MavenArtifact hamcrest = new MavenArtifact("org.hamcrest", "hamcrest-core", "1.3", "jar", "");

		assertThat(dependencies, allOf(
				aMapWithSize(2),
				hasKey(junit),
				hasKey(hamcrest)));
		// EARCAM_SNIPPET_END: resolve-dependencies
	}


	@Test
	void dependencyFiles() throws Exception
	{
		Map<MavenArtifact, Path> dependencies = maven()
				.usingCentral()
				.usingLocal(Paths.get(".", "target", "repo-resolve-files"))
				.dependencyFiles("junit:junit:4.12");

		MavenArtifact junit = new MavenArtifact("junit", "junit", "4.12", "jar", "");
		MavenArtifact hamcrest = new MavenArtifact("org.hamcrest", "hamcrest-core", "1.3", "jar", "");

		assertThat(dependencies, allOf(
				aMapWithSize(2),
				hasKey(junit),
				hasKey(hamcrest)));
	}


	@SuppressWarnings("unchecked")
	@Test
	void dependenciesToClassPath() throws Exception
	{
		// EARCAM_SNIPPET_BEGIN: classpath
		List<String> classpath = maven()
				.usingCentral()
				.usingLocal(Paths.get(".", "target", "repo-resolve-classpath"))
				.classpath("junit:junit:4.12")
				.stream()
				.map(Object::toString)
				.collect(toList());

		assertThat(classpath, containsInAnyOrder(
				endsWith("junit-4.12.jar"),
				endsWith("hamcrest-core-1.3.jar")));

		// EARCAM_SNIPPET_END: classpath
	}


	@Test
	void installArchiveWithZeroDependenciesToLocal()
	{
		Archive archive = archive()
				.configured(asJar())
				.with(MavenTest.class)
				.toObjectModel();

		Maven maven = maven()
				.usingCentral()
				.usingLocal(Paths.get(".", "target", "repo-install"));

		MavenArtifact artifact = new MavenArtifact("com.acme", "com.acme.api", "0.0.1", "jar", "");
		maven.install(artifact, archive);

		Map<MavenArtifact, Archive> dependencies = maven.dependencies("com.acme:com.acme.api:0.0.1");

		assertThat(dependencies, hasKey(artifact));
	}
}
