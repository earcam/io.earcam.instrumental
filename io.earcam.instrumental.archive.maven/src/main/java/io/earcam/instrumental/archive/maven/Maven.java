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
import static io.earcam.instrumental.archive.ArchiveConstruction.contentFrom;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.stream.Collectors.toList;
import static org.eclipse.aether.util.artifact.JavaScopes.COMPILE;
import static org.eclipse.aether.util.filter.DependencyFilterUtils.classpathFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.SubArtifact;

import io.earcam.instrumental.archive.Archive;
import io.earcam.unexceptional.Closing;
import io.earcam.unexceptional.Exceptional;

public final class Maven {

	static final String CENTRAL_URL = "http://central.maven.org/maven2/";
	static final String DEFAULT_TYPE = "default";
	static final String CENTRAL_ID = "central";

	private static final Pattern LOCAL_IN_SETTINGS = Pattern.compile(".*<localRepository>(.*?)</localRepository>.*", MULTILINE | DOTALL);
	private final RepositorySystem system;
	final List<RemoteRepository> remotes = new ArrayList<>();
	LocalRepository local;


	private Maven(RepositorySystem system)
	{
		this.system = system;
	}


	public static Maven maven()
	{
		return new Maven(createRepositorySystem());
	}


	public Maven usingCentral()
	{
		return usingRemote(CENTRAL_ID, DEFAULT_TYPE, CENTRAL_URL);
	}


	public Maven usingRemote(String id, String type, String url)
	{
		remotes.add(new RemoteRepository.Builder(id, type, url).build());
		return this;
	}


	public Maven usingDefaultLocal()
	{
		return usingLocal(findLocalRepoPath());
	}


	private Path findLocalRepoPath()
	{
		Path m2 = Paths.get(System.getProperty("user.home")).resolve(".m2");
		Path settingsXml = m2.resolve("settings.xml");
		if(settingsXml.toFile().isFile()) {
			Matcher matcher = localInSettingsMatcher(settingsXml);
			if(matcher.matches()) {
				Path localPath = Paths.get(matcher.group(1));
				if(!localPath.isAbsolute()) {
					localPath = m2.resolve(localPath);
				}
				return checkLocalPath(localPath, "<localRepository> in " + settingsXml + " is not an existing directory");
			}
		}
		Path localPath = m2.resolve("repository");
		return checkLocalPath(localPath, "No localRepository at " + localPath);
	}


	private Matcher localInSettingsMatcher(Path settingsXml)
	{
		String contents = new String(Exceptional.apply(Files::readAllBytes, settingsXml), UTF_8);
		return LOCAL_IN_SETTINGS.matcher(contents);
	}


	private Path checkLocalPath(Path localPath, String message)
	{
		if(!localPath.toFile().isDirectory()) {
			throw new IllegalStateException(message);
		}
		return localPath.toAbsolutePath();
	}


	public Maven usingLocal(Path path)
	{
		if(path.toFile().isFile()) {
			throw new IllegalArgumentException("local repository '" + path.toAbsolutePath() + "' exists and is not a directory");
		}
		local = new LocalRepository(path.toAbsolutePath().toString());
		return this;
	}


	private static RepositorySystem createRepositorySystem()
	{
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService(TransporterFactory.class, FileTransporterFactory.class);
		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
		return locator.getService(RepositorySystem.class);
	}


	private DefaultRepositorySystemSession createSession(RepositorySystem system)
	{
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, local));

		// TODO wrap up TransferListener and RepositoryListener so can be easily be
		// exposed for a Consumer<String> (client debug logging etc)
		return session;
	}


	/**
	 * Resolves compile-scoped dependencies (including transitive)
	 * 
	 * @param gavs the groupId:artifactId:version:type:classifier coordinates for Maven artifacts
	 * @return the dependencies converted to {@link Archive} instances
	 */
	public Map<MavenArtifact, Archive> dependencies(String... gavs)
	{
		List<ArtifactResult> response = resolve(gavs);

		Map<MavenArtifact, Archive> archives = new HashMap<>();

		for(ArtifactResult result : response) {
			File artifactFile = result.getArtifact().getFile();
			Archive archive = archive().sourcing(contentFrom(artifactFile)).toObjectModel();
			archives.put(new MavenArtifact(result.getArtifact()), archive);
		}
		return archives;
	}


	private List<ArtifactResult> resolve(String... gavs)
	{
		DefaultRepositorySystemSession session = createSession(system);

		List<Dependency> dependencies = Arrays.stream(gavs)
				.map(DefaultArtifact::new)
				.map(a -> new Dependency(a, COMPILE))
				.collect(toList());

		CollectRequest collect = new CollectRequest();
		collect.setDependencies(dependencies);

		collect.setRepositories(remotes);

		DependencyRequest request = new DependencyRequest(collect, classpathFilter(COMPILE));

		return Exceptional.apply(system::resolveDependencies, session, request).getArtifactResults();
	}


	public List<Path> classpath(String... gavs)
	{
		return resolve(gavs).stream()
				.map(ArtifactResult::getArtifact)
				.map(Artifact::getFile)
				.map(File::toPath)
				.collect(Collectors.toList());
	}


	public void install(MavenArtifact artifact, Archive archive, MavenArtifact... dependencies)
	{
		Path artifactPath = local.getBasedir().toPath()
				.resolve(Paths.get(".", artifact.groupId().split("\\.")))
				.resolve(Paths.get(artifact.artifactId(), artifact.baseVersion()));

		String fileName = artifact.artifactId() + '-' + artifact.baseVersion();

		// We get away with writing JAR directly into local repo,
		// but attempting same trick with POM results in overwrite of zero bytes

		Path jar = artifactPath.resolve(fileName + ".jar");
		Path pom = artifactPath.resolve(fileName + ".pom.X");

		archive.to(jar);

		Closing.closeAfterAccepting(FileOutputStream::new, pom.toFile(),
				o -> FakePom.createPom(artifact, dependencies, o));

		Artifact aether = artifact.toAether()
				.setFile(jar.toFile());

		Artifact pomAether = new SubArtifact(aether, "", "pom")
				.setFile(pom.toFile());

		InstallRequest installRequest = new InstallRequest();
		installRequest.addArtifact(aether).addArtifact(pomAether);

		DefaultRepositorySystemSession session = createSession(system);

		Exceptional.accept(system::install, session, installRequest);
	}
}
