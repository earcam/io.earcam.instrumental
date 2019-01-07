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
package io.earcam.instrumental.archive.jpms.auto;

import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.jpms.AsJpmsModule.asJpmsModule;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import java.util.Set;

import javax.lang.model.SourceVersion;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.acme.DummyIntComparator;

import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.instrumental.module.jpms.Require;

public class AsJpmsModuleAutoRequireIntegrationTest {

	@Test
	void anEmptyModuleStillRequiresJavaBase()
	{
		Archive archive = archive()
				.configured(
						asJpmsModule()
								.named("empty")
								.autoRequiringJdkModules(9))
				.toObjectModel();

		Set<String> required = moduleInfoFrom(archive).requires().stream()
				.map(Require::module)
				.collect(toSet());

		assertThat(required, hasItem(equalTo("java.base")));
	}


	@Test
	void aSimpleModuleRequiresJavaBase()
	{
		Archive archive = archive()
				.configured(
						asJpmsModule()
								.named("foo")
								.exporting(x -> true)
								.autoRequiringJdkModules(9))
				.with(DummyIntComparator.class)
				.toObjectModel();

		Set<String> required = moduleInfoFrom(archive).requires().stream()
				.map(Require::module)
				.collect(toSet());

		assertThat(required, hasItem(equalTo("java.base")));
	}


	private ModuleInfo moduleInfoFrom(Archive archive)
	{
		ModuleInfo moduleInfo = archive
				.content("module-info.class")
				.map(ArchiveResource::bytes)
				.map(ModuleInfo::read)
				.orElseThrow(AssertionError::new);
		return moduleInfo;
	}

	@Nested
	public class UsingLatestSourceVersion {

		@Test
		void aSimpleModuleRequiresJavaBase()
		{
			Assumptions.assumeTrue(SourceVersion.latest().ordinal() > 8, "Test requires JDK > 8");

			Archive archive = archive()
					.configured(
							asJpmsModule()
									.named("foo")
									.exporting(x -> true)
									.autoRequiringJdkModules(SourceVersion.latest()))
					.with(DummyIntComparator.class)
					.toObjectModel();

			Set<String> required = moduleInfoFrom(archive).requires().stream()
					.map(Require::module)
					.collect(toSet());

			assertThat(required, hasItem(equalTo("java.base")));
		}
	}
}
