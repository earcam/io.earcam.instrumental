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

import static io.earcam.instrumental.archive.jpms.auto.JdkModules.CACHE_FILENAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.module.jpms.ModuleInfo;

public class JdkModulesTest {

	@Test
	public void mainMethodCreatesCache() throws Exception
	{
		Path directory = Paths.get("target/" + JdkModulesTest.class.getSimpleName() + '/' + UUID.randomUUID());

		JdkModules.main(new String[] { directory.toString() });

		Path cache = directory.resolve(CACHE_FILENAME);

		assertThat("expecting " + cache, cache.toFile(), is(anExistingFile()));

		List<ModuleInfo> modules = JdkModules.deserialize(new FileInputStream(cache.toFile()));

		assertThat(modules, hasSize(greaterThan(50)));  // completely arbitrary, previous observed JDK9 module counts have been 96 and 99

		String jdkVersion = modules.get(0).version();   // makes the test less fragile

		ModuleInfo jdeps = ModuleInfo.moduleInfo().named("jdk.jdeps")
				.versioned(jdkVersion)
				.requiring("jdk.compiler", 0, null)
				.requiring("java.base", 32768, null)
				.requiring("java.compiler", 0, null)
				.exporting("com.sun.tools.jdeps", 0, "jdk.packager")
				.exporting("com.sun.tools.classfile", 0, "jdk.jlink")
				.providing("java.util.spi.ToolProvider", "com.sun.tools.javap.Main$JavapToolProvider", "com.sun.tools.jdeps.Main$JDepsToolProvider")
				.packaging(
						new TreeSet<>(Arrays.asList(
								"com.sun.tools.classfile",
								"com.sun.tools.jdeprscan",
								"com.sun.tools.jdeps",
								"com.sun.tools.jdeps.resources",
								"com.sun.tools.jdeprscan.scan",
								"com.sun.tools.jdeprscan.resources",
								"com.sun.tools.javap.resources",
								"com.sun.tools.javap")))
				.construct();

		assertThat(modules, hasItem(equalTo(jdeps)));
	}

}
