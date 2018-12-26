/*-
 * #%L
 * io.earcam.instrumental.lade.jpms
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
package io.earcam.instrumental.lade.jpms;

import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.jpms.AsJpmsModule.asJpmsModule;
import static io.earcam.instrumental.archive.jpms.auto.ArchivePackageModuleMapper.fromArchives;
import static io.earcam.instrumental.lade.ClassLoaders.inMemoryClassLoader;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.earcam.acme.api.AcmeApi;
import io.earcam.acme.app.AcmeApp;
import io.earcam.acme.imp.AcmeImp;
import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.archive.AsJar;
import io.earcam.instrumental.lade.Handler;
import io.earcam.instrumental.lade.InMemoryClassLoader;
import io.earcam.instrumental.reflect.Methods;
import io.earcam.unexceptional.CheckedRunnable;
import io.earcam.unexceptional.Exceptional;

public class InMemoryModuleFinderTest {

	@Test
	void findsAllModules()
	{
		InMemoryClassLoader loader = inMemoryClassLoader()
				.jar(archive()
						.configured(
								asJpmsModule()
										.named("a")
										.autoRequiring(9))
						.toByteArray())
				.jar(archive()
						.configured(
								AsJar.asJar()
										.withManifestHeader("Name", "B"))
						.toByteArray())
				.jar(archive()
						.configured(
								asJpmsModule()
										.named("c")
										.autoRequiring(9))
						.toByteArray());

		InMemoryModuleFinder finder = new InMemoryModuleFinder(loader);

		Set<String> names = finder.findAll().stream()
				.map(ModuleReference::descriptor)
				.map(ModuleDescriptor::name)
				.collect(toSet());

		assertThat(names, contains("a", "c"));
	}


	@Test
	void wiresUpAndRuns() throws Exception
	{
		// @formatter:off
		// EARCAM_SNIPPET_BEGIN: setup-three-jars
		
		Archive api = archive()
			.configured(
				asJpmsModule()
					.named("api")
					.exporting(s -> true)
					.autoRequiring(9)
			)
			.with(AcmeApi.class)
			.toObjectModel();
		
		Archive app = archive()
				.configured(
					asJpmsModule()
						.named("app")
						.autoRequiring(9)
						.autoRequiring(fromArchives(api))
						.exporting(s -> true)
						.using(AcmeApi.class)
						.launching(AcmeApp.class)
				)
				.toObjectModel();

		Archive imp = archive()
				.configured(
					asJpmsModule()
						.named("imp")
						.autoRequiring(9)
						.autoRequiring(fromArchives(api))
						.providing(AcmeApi.class, AcmeImp.class)
				).toObjectModel();

		// EARCAM_SNIPPET_END: setup-three-jars

		// EARCAM_SNIPPET_BEGIN: create-loader
		
		Handler.addProtocolHandlerSystemProperty();
		
		InMemoryClassLoader loader = inMemoryClassLoader()
			.jar(api.toByteArray())
			.jar(imp.toByteArray())
			.jar(app.toByteArray());

		// EARCAM_SNIPPET_END: create-loader

		// EARCAM_SNIPPET_BEGIN: create-layer
		
		ModuleFinder finder = new InMemoryModuleFinder(loader);
		ModuleLayer parent = ModuleLayer.boot();
		
		Configuration configuration = parent.configuration().resolveAndBind(
				ModuleFinder.of(), 
				finder, 
				Set.of("app"));
		
		ModuleLayer layer = parent.defineModulesWithOneLoader(configuration, loader);

		// EARCAM_SNIPPET_END: create-layer

		// EARCAM_SNIPPET_BEGIN: execute-app

		Class<?> mainClass = layer.findLoader("app").loadClass(AcmeApp.class.getCanonicalName());
		Method main = Methods.getMethod(mainClass, "main", String[].class)
				.orElseThrow(NoSuchMethodError::new);
		
		String you = "Dynamic & In-Mem";
		String stdout = captureStdout(() -> main.invoke(null, new Object[] {new String[] {you}}));
		
		String expectedResponse = 
				"module app: Acme Corp would like to say \"Hello\" to \"" + 
				you + 
				"\" (Terms and conditions apply)\n";

		assertThat(stdout, is(equalTo(expectedResponse)));
		// EARCAM_SNIPPET_END: execute-app
		// @formatter:on
	}


	private static String captureStdout(CheckedRunnable<ReflectiveOperationException> runnable)
	{
		PrintStream old = System.out;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			System.setOut(new PrintStream(baos));

			Exceptional.run(runnable);
			return new String(baos.toByteArray(), UTF_8);
		} finally {
			System.setOut(old);
		}
	}


	@Test
	void closeDoesNothing() throws IOException
	{
		InMemoryClassLoader loader = inMemoryClassLoader()
				.jar(archive()
						.configured(
								asJpmsModule()
										.named("a")
										.autoRequiring(9))
						.toByteArray());

		InMemoryModuleFinder finder = new InMemoryModuleFinder(loader);

		ModuleReader reader = finder.findAll().stream()
				.map(Exceptional.uncheckFunction(ModuleReference::open))
				.findFirst()
				.orElseThrow(NoSuchElementException::new);

		reader.close();

		assertThat(reader.find("module-info.class").isPresent(), is(true));
	}
}
