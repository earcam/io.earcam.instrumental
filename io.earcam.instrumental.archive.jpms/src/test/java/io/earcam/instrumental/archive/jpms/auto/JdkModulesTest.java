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

import static io.earcam.instrumental.archive.jpms.auto.JdkModules.DEFAULT_DIRECTORY;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.module.jpms.ModuleInfo;

public class JdkModulesTest {

	@Test
	public void mainMethodCreatesCache() throws Exception
	{
		Path directory = Paths.get("target", JdkModulesTest.class.getSimpleName(), Long.toString(System.currentTimeMillis()));

		JdkModules.main(new String[] { directory.toString() });

		Path base = directory.resolve("11");

		Path cache = base.resolve("index.txt");

		assertThat("expecting " + cache, cache.toFile(), is(anExistingFile()));

		List<ModuleInfo> modules = JdkModules.load(base.toString());

		// completely arbitrary, previous observed JDK9 module counts have been 96 and 99
		assertThat(modules, hasSize(greaterThan(50)));

		String jdkVersion = modules.get(0).version();   // makes the test less fragile ... and less accurate

		ModuleInfo jdeps = ModuleInfo.moduleInfo().named("jdk.jdeps")
				.versioned(jdkVersion)
				.requiring("java.base", 32768, null)
				.requiring("java.compiler", 0, null)
				.requiring("jdk.compiler", 0, null)

				.exporting("com.sun.tools.classfile", 32768, "jdk.jlink")
				.providing("java.util.spi.ToolProvider", "com.sun.tools.javap.Main$JavapToolProvider", "com.sun.tools.jdeps.Main$JDepsToolProvider")
				.packaging(
						new TreeSet<>(Arrays.asList(
								"com.sun.tools.classfile",
								"com.sun.tools.jdeps",
								"com.sun.tools.jdeps.resources",
								"com.sun.tools.jdeprscan",
								"com.sun.tools.jdeprscan.scan",
								"com.sun.tools.jdeprscan.resources",
								"com.sun.tools.javap",
								"com.sun.tools.javap.resources")))
				.construct();

		assertThat(modules, hasItem(equalTo(jdeps)));
	}


	@Test
	void doesNotThrowWhenScriptHasZeroExitCodeAndOutputContainsDone() throws IOException, InterruptedException
	{
		Process process = mock(Process.class);
		when(process.getInputStream()).thenReturn(new ByteArrayInputStream("DONE".getBytes(UTF_8)));
		when(process.exitValue()).thenReturn(0);

		JdkModules.processOutput(process);

		verify(process, atLeastOnce()).exitValue();
		verify(process, atLeastOnce()).getInputStream();
	}


	@Test
	void throwsWhenScriptHasNonZeroExitCode() throws IOException, InterruptedException
	{
		Process process = mock(Process.class);
		when(process.getInputStream()).thenReturn(new ByteArrayInputStream("DONE".getBytes(UTF_8)));
		when(process.getErrorStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
		when(process.exitValue()).thenReturn(101);

		try {
			JdkModules.processOutput(process);
			fail();
		} catch(IOException e) {}
	}


	@Test
	void throwsWhenScriptOutputDoesNotContainDone() throws IOException, InterruptedException
	{
		Process process = mock(Process.class);
		when(process.getInputStream()).thenReturn(new ByteArrayInputStream("OH NOES".getBytes(UTF_8)));
		when(process.getErrorStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
		when(process.exitValue()).thenReturn(0);

		try {
			JdkModules.processOutput(process);
			fail();
		} catch(IOException e) {}
	}


	@Test
	public void defaultOutputDirectory()
	{
		assertThat(JdkModules.outputDirectory(new String[0]), is(equalTo(DEFAULT_DIRECTORY)));
	}


	@Test
	public void throwsWhenCannotFindIndex()
	{
		try {
			JdkModules.load("/dev/null");
			fail();
		} catch(UncheckedIOException e) {}
	}
}
