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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

public final class MavenArtifact {

	private final String groupId;
	private final String artifactId;
	private final String baseVersion;
	private final String extension;
	private final String classifier;


	MavenArtifact(Artifact aetherArtifact)
	{
		this(
				aetherArtifact.getGroupId(),
				aetherArtifact.getArtifactId(),
				aetherArtifact.getBaseVersion(),
				aetherArtifact.getExtension(),
				aetherArtifact.getClassifier());
	}


	public MavenArtifact(String groupId, String artifactId, String baseVersion, String extension, String classifier)
	{
		this.groupId = emptify(groupId);
		this.artifactId = emptify(artifactId);
		this.baseVersion = emptify(baseVersion);
		this.extension = emptify(extension);
		this.classifier = emptify(classifier);
	}


	private static String emptify(String text)
	{
		return (text == null) ? "" : text;
	}


	Artifact toAether()
	{
		return new DefaultArtifact(groupId, artifactId, classifier, extension, baseVersion);
	}


	public String groupId()
	{
		return groupId;
	}


	public String artifactId()
	{
		return artifactId;
	}


	public String baseVersion()
	{
		return baseVersion;
	}


	public String extension()
	{
		return extension;
	}


	public String classifier()
	{
		return classifier;
	}


	@Override
	public boolean equals(Object other)
	{
		return other instanceof MavenArtifact && equals((MavenArtifact) other);
	}


	public boolean equals(MavenArtifact that)
	{
		return that != null
				&& Objects.equals(that.groupId(), this.groupId)
				&& Objects.equals(that.artifactId(), this.artifactId)
				&& Objects.equals(that.baseVersion(), this.baseVersion)
				&& Objects.equals(that.extension(), this.extension)
				&& Objects.equals(that.classifier(), this.classifier);
	}


	@Override
	public int hashCode()
	{
		return Objects.hash(groupId, artifactId, baseVersion, extension, classifier);
	}


	/**
	 * @return the relative path that this artifact would occupy in a repository
	 * 
	 * @see #filename()
	 * @see #pomFilename()
	 */
	public Path relativeRepositoryDirectory()
	{
		return Paths.get(".", groupId().split("\\.")).resolve(Paths.get(artifactId(), baseVersion()));
	}


	/**
	 * @return the filename of this artifact (as it would appear in a repository)
	 * 
	 * @see #relativeRepositoryDirectory()
	 * @see #pomFilename()
	 */
	public String filename()
	{
		return artifactId() + '-' + baseVersion() + ("".equals(classifier()) ? "" : '-' + classifier()) + '.' + extension();
	}


	/**
	 * @return the POM filename of this artifact (as it would appear in a repository, <br/>
	 * i.e {@code artifactId + "-" + version + ".pom"})
	 * 
	 * @see #relativeRepositoryDirectory()
	 * @see #filename()
	 */
	public String pomFilename()
	{
		return artifactId() + '-' + baseVersion() + ".pom";
	}
}
