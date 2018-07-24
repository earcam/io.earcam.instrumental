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

import static io.earcam.instrumental.archive.AsAgentJar.*;
import static io.earcam.instrumental.archive.DefaultAsAgentJar.*;
import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.reflect.Names.typeToResourceName;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.earcam.acme.DummyAgent;
import io.earcam.acme.DummyAgentInvalidAgentMainArguments;
import io.earcam.acme.DummyAgentInvalidAgentMainNotPublicStatic;
import io.earcam.acme.DummyAgentNoPreMain;

public class AsAgentJarTest {

	@Test
	void validAgentClass()
	{
		Archive archive = validAgentArchive();

		String agentClassName = archive.manifest().get().getMainAttributes().getValue(AGENT_CLASS);

		assertThat(agentClassName, is(equalTo(DummyAgent.class.getCanonicalName())));

		assertThat(archive.contents(), contains(new ArchiveResource(typeToResourceName(DummyAgent.class), new byte[0])));
	}


	@Test
	void premainAddedIfPresent()
	{
		Archive archive = validAgentArchive();

		String agentClassName = archive.manifest().get().getMainAttributes().getValue(PREMAIN_CLASS);

		assertThat(agentClassName, is(equalTo(DummyAgent.class.getCanonicalName())));
	}


	@Test
	void premainNotAddedWhenNotPresent()
	{
		Archive archive = archive()
				.configured(asAgentJar()
						.withAgentClass(DummyAgentNoPreMain.class))
				.toObjectModel();

		String agentClassName = archive.manifest().get().getMainAttributes().getValue(AGENT_CLASS);
		String premainClass = archive.manifest().get().getMainAttributes().getValue(PREMAIN_CLASS);

		assertThat(agentClassName, is(equalTo(DummyAgentNoPreMain.class.getCanonicalName())));
		assertThat(premainClass, is(nullValue()));
	}


	private Archive validAgentArchive()
	{
		return validAgentArchiveBuilder()
				.toObjectModel();
	}


	private ArchiveConfiguration validAgentArchiveBuilder()
	{
		return archive()
				.configured(asAgentJar()
						.withAgentClass(DummyAgent.class));
	}


	@Test
	void invalidAgentClassMethodsNotStatic()
	{
		try {
			asAgentJar()
					.withAgentClass(DummyAgentInvalidAgentMainNotPublicStatic.class);
			Assertions.fail();
		} catch(IllegalArgumentException e) {}
	}


	@Test
	void invalidAgentClassMethodsWrongParameters()
	{
		try {
			asAgentJar()
					.withAgentClass(DummyAgentInvalidAgentMainArguments.class);
			Assertions.fail();
		} catch(IllegalArgumentException e) {}
	}


	@Test
	void canRedefineDefaultsToFalse()
	{
		Archive archive = validAgentArchive();

		String can = archive.manifest().get().getMainAttributes().getValue(CAN_REDEFINE_CLASSES);

		assertThat(can, is(equalTo(FALSE.toString())));
	}


	@Test
	void canRetransformDefaultsToFalse()
	{
		Archive archive = validAgentArchive();

		String can = archive.manifest().get().getMainAttributes().getValue(CAN_RETRANSFORM_CLASSES);

		assertThat(can, is(equalTo(FALSE.toString())));
	}


	@Test
	void canRedefine()
	{
		Archive archive = archive()
				.configured(asAgentJar()
						.withAgentClass(DummyAgent.class)
						.canRedefineClasses())
				.toObjectModel();

		String can = archive.manifest().get().getMainAttributes().getValue(CAN_REDEFINE_CLASSES);

		assertThat(can, is(equalTo(TRUE.toString())));
	}


	@Test
	void canRetransform()
	{
		Archive archive = archive()
				.configured(asAgentJar()
						.withAgentClass(DummyAgent.class)
						.canRetransformClasses())
				.toObjectModel();

		String can = archive.manifest().get().getMainAttributes().getValue(CAN_RETRANSFORM_CLASSES);

		assertThat(can, is(equalTo(TRUE.toString())));
	}


	@Test
	void explicitCanNotRedefine()
	{
		Archive archive = archive()
				.configured(
						asAgentJar()
								.withAgentClass(DummyAgent.class)
								.canNotRedefineClasses())
				.toObjectModel();

		String can = archive.manifest().get().getMainAttributes().getValue(CAN_REDEFINE_CLASSES);

		assertThat(can, is(equalTo(FALSE.toString())));
	}


	@Test
	void explicitCanNotRetransform()
	{
		Archive archive = archive()
				.configured(
						asAgentJar()
								.withAgentClass(DummyAgent.class)
								.canNotRetransformClasses())
				.toObjectModel();

		String can = archive.manifest().get().getMainAttributes().getValue(CAN_RETRANSFORM_CLASSES);

		assertThat(can, is(equalTo(FALSE.toString())));
	}
}
