/*-
 * #%L
 * io.earcam.instrumental.archive
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
package io.earcam.instrumental.archive;

import static io.earcam.instrumental.archive.AbstractAsJarBuilder.MULTI_RELEASE_ROOT_PATH;
import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.INITIAL;
import static java.lang.Boolean.TRUE;

import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import javax.lang.model.SourceVersion;

final class DefaultAsMultiReleaseJar implements AsMultiReleaseJar, AsReleaseJar, ArchiveResourceSource, ManifestProcessor {

	private static final Name HEADER_MULTI_RELEASE = new Name("Multi-Release");
	private Stream<ArchiveResource> resources;

	private static class ReleasePathFilter implements ArchiveResourceFilter {

		private final String pathPrefix;


		public ReleasePathFilter(int version)
		{
			this.pathPrefix = pathPrefixFor(version);
		}


		private static String pathPrefixFor(int version)
		{
			return MULTI_RELEASE_ROOT_PATH + version + "/";
		}


		@Override
		public ArchiveResource apply(ArchiveResource resource)
		{
			return ArchiveResource.rename(pathPrefix + resource.name(), resource);
		}

	}


	@Override
	public void attach(ArchiveRegistrar core)
	{
		core.registerResourceSource(this);
		core.registerManifestProcessor(this);
	}


	@Override
	public AsReleaseJar base(SourceVersion version, ArchiveConstruction archive)
	{
		resources = streamResources(archive);
		return this;
	}


	private Stream<ArchiveResource> streamResources(ArchiveConstruction archive)
	{
		return archive.toObjectModel().contents().stream();
	}


	@Override
	public AsReleaseJar release(int version, ArchiveConstruction archive)
	{
		resources = Stream.concat(
				resources,
				streamResources(archive).map(new ReleasePathFilter(version)));
		return this;
	}


	@Override
	public Stream<ArchiveResource> drain(ResourceSourceLifecycle stage)
	{
		return stage == INITIAL ? resources : Stream.empty();
	}


	@Override
	public void process(Manifest manifest)
	{
		manifest.getMainAttributes().put(HEADER_MULTI_RELEASE, TRUE.toString());
	}
}
