/*-
 * #%L
 * io.earcam.instrumental.uber
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
package io.earcam.instrumental.uber;

// @formatter:off
import static io.earcam.instrumental.archive.glue.ArchiveFileObjectProvider.fromArchive;
import static io.earcam.instrumental.archive.jpms.AsJpmsModule.asJpmsModule;
import static io.earcam.instrumental.archive.jpms.auto.ArchivePackageModuleMapper.fromArchives;
import static io.earcam.instrumental.archive.osgi.AsOsgiBundle.asOsgiBundle;
import static io.earcam.instrumental.archive.osgi.auto.ArchivePackageBundleMapper.byMappingBundleArchives;
import static io.earcam.instrumental.archive.sign.StandardDigestAlgorithms.SHA512;
import static io.earcam.instrumental.archive.sign.StandardSignatureAlgorithms.SHA256_WITH_RSA;
import static io.earcam.instrumental.archive.sign.WithSignature.withSignature;
import static io.earcam.instrumental.compile.Compiler.compiling;
import static io.earcam.instrumental.compile.SourceSource.from;
import static io.earcam.instrumental.compile.glue.CompileToArchive.toArchive;
import static io.earcam.instrumental.module.osgi.ClauseParameters.ClauseParameter.attribute;
import static io.earcam.utilitarian.security.Certificates.certificate;
import static io.earcam.utilitarian.security.KeyStores.keyStore;
import static java.io.File.pathSeparatorChar;
import static java.io.File.separatorChar;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;
import static javax.lang.model.SourceVersion.latestSupported;
import static org.apache.felix.connect.launch.PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS;

import java.io.PrintWriter;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.felix.connect.launch.BundleDescriptor;
import org.apache.felix.connect.launch.ClasspathScanner;
import org.apache.felix.connect.launch.PojoServiceRegistry;
import org.apache.felix.connect.launch.PojoServiceRegistryFactory;
import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.Archive;
import io.earcam.instrumental.lade.ClassLoaders;
import io.earcam.instrumental.lade.Handler;
import io.earcam.instrumental.lade.InMemoryClassLoader;
import io.earcam.instrumental.lade.jpms.InMemoryModuleFinder;
import io.earcam.instrumental.reflect.Methods;
import io.earcam.unexceptional.Exceptional;
import io.earcam.utilitarian.io.IoStreams;
import io.earcam.utilitarian.security.Keys;


//@Disabled
public class ExampleTest {
	// @formatter:off

	private final KeyPair keys = Keys.rsa();
	private final String alias = "alias";
	private final char[] password = "password".toCharArray();
	private final String SUBJECT = "subject";
	private final KeyStore keyStore = keyStore(alias, password, keys, certificate(keys, SUBJECT).toX509());


	@Test
	void chunkyExample() throws Exception
	{

		String author = "com.acme";
		
		Archive apiJar;
		
		// EARCAM_SNIPPET_BEGIN: api-jar
		String moduleNameApi = "com.acme.api";	
		String versionApi = "1.0.0";	
		
		apiJar = compiling()
			.versionAt(latestSupported())
			.source(
				from(
					"package com.acme.api;                           \n" +

					"public interface Greet {                        \n" +

					"   public abstract String greeting(String to);  \n" +

					"}                                               \n")
			)
			.compile(
				toArchive()
			)
			.configured(
				asOsgiBundle()
					.named(moduleNameApi)
					.exporting(s -> true, attribute("version", versionApi))
			)
			.configured(
				asJpmsModule()
					.named(moduleNameApi)
					.exporting(s -> true)
					.autoRequiring()
			)
			.configured(
				withSignature()
					.digestedBy(SHA512)
					.store(keyStore)
					.alias(alias)
					.password(password)
					.signatureAlgorithm(SHA256_WITH_RSA)
					.createdBy(author)
			)
			.toObjectModel();

		// EARCAM_SNIPPET_END: api-jar

		String versionImp = "1.0.42";
		// EARCAM_SNIPPET_BEGIN: imp-jar
		String moduleNameImp = "com.acme.imp";	
		
		Archive impJar = compiling()
			.versionAt(latestSupported())
			.source(from(
				"package com.acme.imp;                              \n" +

				"import com.acme.api.Greet;                         \n" +

				"public class HelloService implements Greet {       \n" +

				"   @Override                                       \n" +
				"   public String greeting(String to)               \n" +
				"   {                                               \n" +
				"      return \"Hello \" + to;                      \n" +
				"   }                                               \n" +
				"}                                                  \n")
			)
			.source(from(
				"package com.acme.imp;                                     \n" +

				"import com.acme.api.Greet;                                \n" +
				"import org.osgi.framework.BundleActivator;                \n" + 
				"import org.osgi.framework.BundleContext;                  \n" + 
				"import org.osgi.framework.ServiceRegistration;            \n" + 

				"public class Activator implements BundleActivator {       \n" + 

				"   private ServiceRegistration<Greet> registration;       \n" + 

				"   @Override                                              \n" + 
				"   public void start(BundleContext ctx)                   \n" + 
				"   {                                                      \n" + 
				"      HelloService hello = new HelloService();            \n" + 
				"      registration =                                      \n" +
				"         ctx.registerService(Greet.class, hello, null);   \n" + 
				"   }                                                      \n" + 

				"   @Override                                              \n" + 
				"   public void stop(BundleContext ctx)                    \n" + 
				"   {                                                      \n" + 
				"      if(registration != null) {                          \n" + 
				"         registration.unregister();                       \n" + 
				"      }                                                   \n" + 
				"   }                                                      \n" + 
				"}                                                         \n")
			)
			.withDependencies(fromArchive(apiJar))
			.compile(
				toArchive()
			)
			.configured(
				asOsgiBundle()
					.named(moduleNameImp)
					.autoImporting()
					.autoImporting(byMappingBundleArchives(apiJar))
					.withActivator(moduleNameImp + ".Activator")
			)
			.configured(
				asJpmsModule()
					.named(moduleNameImp)
					.autoRequiring()
					.autoRequiring(fromArchives(apiJar))
					.providing(moduleNameApi + ".Greet", 
							singleton(moduleNameImp + ".HelloService")))
			.toObjectModel();
		// EARCAM_SNIPPET_END: imp-jar
		
		Archive appJar;
		String versionApp = "1.39.0";
		
		// EARCAM_SNIPPET_BEGIN: app-jar		
		String moduleNameApp = "com.acme.app";	
		
		appJar = compiling()
			.versionAt(latestSupported())
			.source(from(
				"package com.acme.app;                                    \n" +

				"import com.acme.api.Greet;                               \n" +

				"import java.lang.Module;                                 \n" +
				"import java.lang.ModuleLayer;                            \n" +
				
				"import java.util.ServiceLoader;                          \n" + 
				"import java.util.stream.StreamSupport;                   \n" + 

				"import org.osgi.service.component.annotations.Activate;  \n" +
				"import org.osgi.service.component.annotations.Component; \n" +
				"import org.osgi.service.component.annotations.Reference; \n" +
				
				"@Component(immediate = true)                             \n" +
				"public class App {                                       \n" +
				
				"   @Reference                                            \n" +
				"   Greet greet;                                          \n" +
				
				"   @Activate                                             \n" +
				"   void activate()                                       \n" +
				"   {                                                     \n" +
				"      String greeting = greet.greeting(\"Modularity\");  \n" + 
				"      System.out.println(\"OSGi: \" + greeting);         \n" + 
				"   }                                                     \n" + 
				
				"   public static void main(String[] args)                \n" + 
				"   {                                                     \n" + 
				"      Class<Greet> srv = Greet.class;                    \n" +
				
				"      Module module = App.class.getModule();             \n" +
				"      ModuleLayer layer = module.getLayer();             \n" +
			
				"      ServiceLoader<Greet> services = (layer == null) ?  \n" +
				"               ServiceLoader.load(srv)                   \n" +
				"               :                                         \n" +
				"               ServiceLoader.load(layer, srv);           \n" +
				
				"      Greet implementation = StreamSupport               \n" +
				"               .stream(services.spliterator(), false)    \n" + 
				"               .findFirst()                              \n" + 
				"               .orElseThrow();                           \n" + 

				"      String msg = implementation.greeting(args[0]);     \n" + 
				"      System.out.println(module + \": \" + msg);         \n" + 
				"   }                                                     \n" + 
				"}                                                        \n")
			)
			.withDependencies(fromArchive(apiJar))
			.loggingTo(new PrintWriter(System.err))
			.consumingDiagnositicMessages(System.err::println)
			.compile(
				toArchive()
			)
			.configured(
				asJpmsModule()
					.named(moduleNameApp)
					.exporting(s -> s.startsWith("com.acme"))   // launch main
					.autoRequiring()
					.autoRequiring(fromArchives(apiJar, impJar))
					.launching("com.acme.app.App")
					.using("com.acme.api.Greet")
			)
			.configured(
				asOsgiBundle()
					.named(moduleNameApp)
					.autoImporting()
					.autoImporting(byMappingBundleArchives(apiJar, impJar))
					.withManifestHeader("Service-Component", "/OSGI-INF/scr.xml")
			)
			.with("/OSGI-INF/scr.xml", bytes(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>               \n" + 
				"<scr:component name=\"example.app\"                      \n" + 
				"    xmlns:scr=\"http://www.osgi.org/xmlns/scr/v1.4.0\">  \n" +
				
				"    <implementation class=\"com.acme.app.App\" />        \n" +
				"    <reference                                           \n" +
				"    name=\"greet\"                                       \n" +
				"    interface=\"com.acme.api.Greet\"                     \n" +
				"    field=\"greet\" />                                   \n" +
				"</scr:component>")
			)
			.toObjectModel();
		// EARCAM_SNIPPET_END: app-jar

		
		
		// EARCAM_SNIPPET_BEGIN: load-inmemory
		InMemoryClassLoader loader = ClassLoaders.inMemoryClassLoader()
				.jar(apiJar.toByteArray())
				.jar(impJar.toByteArray())
				.jar(appJar.toByteArray());

		Handler.addProtocolHandlerSystemProperty();

		// EARCAM_SNIPPET_END: load-inmemory

		
		
		
		// EARCAM_SNIPPET_BEGIN: run-vanilla
		Class<?> main = loader.loadClass("com.acme.app.App");
		Method method = Methods.getMethod(main, "main", String[].class).orElseThrow();

		Runnable run = Exceptional.uncheckRunnable(
				() -> method.invoke(null, new Object[] {new String[] {"Vanilla"}}));
		
		ClassLoaders.runInContext(loader, run);
		// EARCAM_SNIPPET_END: run-vanilla
		
		

		// EARCAM_SNIPPET_BEGIN: run-jpms-inmemory
		
		ModuleFinder inMemoryFinder = new InMemoryModuleFinder(loader);
		ModuleLayer parent = ModuleLayer.boot();
		
		Configuration cf = parent.configuration().resolveAndBind(
				ModuleFinder.of(), 
				inMemoryFinder, 
				Set.of(moduleNameApp));
		
		ModuleLayer layer = parent.defineModulesWithOneLoader(cf, loader);

		Class<?> mainClass = layer.findLoader(moduleNameApp).loadClass("com.acme.app.App");

		Method mainMethod = Methods.getMethod(mainClass, "main", String[].class).orElseThrow();
		mainMethod.invoke(null, new Object[] {new String[] {"In-memory"}});

		// EARCAM_SNIPPET_END: run-jpms-inmemory
		

		
		//System.setProperty("ds.showtrace", "true");  //uncomment to enable DS/SCR logging
		
		// EARCAM_SNIPPET_BEGIN: run-osgi
		
		List<BundleDescriptor> bundles = new ClasspathScanner().scanForBundles(loader);
		Map<String, Object> config = Collections.singletonMap(BUNDLE_DESCRIPTORS, bundles);

		PojoServiceRegistry registry = ServiceLoader.load(PojoServiceRegistryFactory.class).iterator()
				.next()
				.newPojoServiceRegistry(config);
		// EARCAM_SNIPPET_END: run-osgi
		
		// System.out.println("BUNDLES:   "+ Arrays.stream(registry.getBundleContext().getBundles()).map(b -> b.getSymbolicName() + ":" + b.getState()).collect(Collectors.joining("\n")));
		
		
		// EARCAM_SNIPPET_BEGIN: filesystem
		Path apiPath = apiJar.to(Paths.get(".", "target", "example", "api.jar"));
		Path impPath = impJar.to(Paths.get(".", "target", "example", "imp.jar"));
		Path appPath = appJar.to(Paths.get(".", "target", "example", "app.jar"));
		// EARCAM_SNIPPET_END: filesystem

		// EARCAM_SNIPPET_BEGIN: run-jpms-filesystem
		char colon = pathSeparatorChar;
		String home = System.getProperty("java.home");
		String javaBin = home + separatorChar + "bin" + separatorChar + "java";

		ProcessBuilder processBuilder = new ProcessBuilder(javaBin,
				"--module-path", 
				apiPath.toString() + colon + impPath + colon + appPath,
				"--module", 
				moduleNameApp + '/' + "com.acme.app.App",
				"FileSystem");
		
		Process process = processBuilder.start();
		String output = 
				new String(IoStreams.readAllBytes(process.getInputStream()), UTF_8) +
				new String(IoStreams.readAllBytes(process.getErrorStream()), UTF_8);
		System.out.print(output);
		// EARCAM_SNIPPET_END: run-jpms-filesystem
	}


	private static byte[] bytes(String text)
	{
		return text.getBytes(UTF_8);
	}
}
// @formatter:on
