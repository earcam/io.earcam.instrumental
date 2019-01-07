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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MavenArtifactTest {

	@Nested
	public class Equality {

		@Nested
		public class Equal {

			MavenArtifact a = new MavenArtifact("groupId", "artifactId", null, "", null);


			@Test
			void regardlessOfNullOrEmptyStringProperty()
			{
				MavenArtifact b = new MavenArtifact("groupId", "artifactId", "", null, "");

				assertThat(a, is(equalTo(b)));
				assertThat(a.hashCode(), is(equalTo(b.hashCode())));
			}


			@Test
			void whenCast()
			{
				Object b = new MavenArtifact("groupId", "artifactId", "", null, "");

				assertTrue(a.equals(b));
				assertThat(a.hashCode(), is(equalTo(b.hashCode())));
			}
		}

		@Nested
		public class NotEqualWhen {

			MavenArtifact a = new MavenArtifact("groupId", "artifactId", "baseVersion", "extension", "classifier");


			@Test
			void groupIdDiffers()
			{
				MavenArtifact b = new MavenArtifact("different", "artifactId", "baseVersion", "extension", "classifier");

				assertThat(a, is(not(equalTo(b))));
			}


			@Test
			void artifactIdDiffers()
			{
				MavenArtifact b = new MavenArtifact("groupId", "different", "baseVersion", "extension", "classifier");

				assertThat(a, is(not(equalTo(b))));
			}


			@Test
			void baseVersionDiffers()
			{
				MavenArtifact b = new MavenArtifact("groupId", "artifactId", "different", "extension", "classifier");

				assertThat(a, is(not(equalTo(b))));
			}


			@Test
			void extensionDiffers()
			{
				MavenArtifact b = new MavenArtifact("groupId", "artifactId", "baseVersion", "different", "classifier");

				assertThat(a, is(not(equalTo(b))));
			}


			@Test
			void classifierDiffers()
			{
				MavenArtifact b = new MavenArtifact("groupId", "artifactId", "baseVersion", "extension", "different");

				assertThat(a, is(not(equalTo(b))));
			}
		}

		@Nested
		public class NotEqualToNull {

			MavenArtifact a = new MavenArtifact("groupId", "artifactId", "baseVersion", "extension", "classifier");


			@Test
			void object()
			{
				Object b = null;

				Assertions.assertFalse(a.equals(b));
			}


			@Test
			void type()
			{
				MavenArtifact b = null;

				Assertions.assertFalse(a.equals(b));
			}
		}
	}

	@Nested
	public class Filename {

		@Test
		void gav() throws Exception
		{
			MavenArtifact mavenArtifact = new MavenArtifact("gid", "art", "1.2.3", "tgz", null);

			String filename = mavenArtifact.filename();

			assertThat(filename, is(equalTo("art-1.2.3.tgz")));
		}


		@Test
		void gavc() throws Exception
		{
			MavenArtifact mavenArtifact = new MavenArtifact("groupie", "aid", "0.24.7", "ear", "classifier");

			String filename = mavenArtifact.filename();

			assertThat(filename, is(equalTo("aid-0.24.7-classifier.ear")));
		}
	}
}