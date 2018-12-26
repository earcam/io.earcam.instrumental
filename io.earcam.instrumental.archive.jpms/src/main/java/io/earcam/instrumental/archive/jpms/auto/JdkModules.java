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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import javax.annotation.WillClose;
import javax.lang.model.SourceVersion;

import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.unexceptional.Closing;

/**
 * <p>
 * This is <b>not</b> a public API, subject to change without notice.
 * </p>
 * <p>
 * Future versions will most likely generate cache in e.g. JSON, which
 * will be version-controlled; avoiding the necessity of access to
 * all older JDK versions to build the project.
 * </p>
 * 
 */
public final class JdkModules extends AbstractPackageModuleMapper {

	private static final String META_INF = "META-INF";

	private static final String VERSION_PLACEHOLDER = "$$$";
	public static final String CACHE_FILENAME = "$$$.jpms.cache";

	/**
	 * A path to a JDK >= 9, where '$$$' will be replaced by '9', '10', '11', ...
	 */
	public static final String PROPERTY_JDK_HOME_PATTERN = "instrumental.jdk.home";

	static final Path DEFAULT_DIRECTORY = Paths.get("target", "classes", META_INF);
	static final String JDK_HOME = "/usr/lib/jvm/java-" + VERSION_PLACEHOLDER + "-oracle/";

	private static List<ModuleInfo> modules;

	private final int version;


	public JdkModules(int version)
	{
		this.version = version;
	}


	/**
	 * @deprecated stoopid idea to have this as a default
	 */
	@Deprecated
	public JdkModules()
	{
		this(Math.max(SourceVersion.latest().ordinal(), 9));
	}


	/**
	 * <p>
	 * Generate the necessary cache files.
	 * </p>
	 *
	 * @param args single optional argument; the output directory
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException
	{
		int maxVersion = Math.max(20, SourceVersion.latest().ordinal());

		for(int version = 9; version < maxVersion; version++) {
			Path outputFile = outputFile(args, version);
			Path jdkHome = jdkHome(version);
			if(jdkHome.toFile().isDirectory()) {
				String script = runnableScript(outputFile);
				execute(jdkHome, script);
			}
		}
	}


	private static Path outputFile(String[] args, int version)
	{
		Path output = outputDirectory(args);
		output.toFile().mkdirs();
		return output.resolve(substituteVersion(CACHE_FILENAME, version));
	}


	private static Path outputDirectory(String[] args)
	{
		return (args.length == 0) ? DEFAULT_DIRECTORY : Paths.get(args[0]);
	}


	private static Path jdkHome(int version)
	{
		String jdkPattern = System.getProperty(PROPERTY_JDK_HOME_PATTERN, JDK_HOME);
		return Paths.get(substituteVersion(jdkPattern, version));
	}


	static String substituteVersion(String property, int version)
	{
		return property.replace(VERSION_PLACEHOLDER, Integer.toString(version));
	}


	private static String runnableScript(Path outputFile)
	{
		return script(outputFile).replace('\n', ' ') + '\n';
	}


	@SuppressWarnings("squid:S1192")
	private static String script(Path output)
	{
		return "import static java.util.stream.Collectors.toSet; \n" +
				"\n" +
				"import java.io.File; \n" +
				"import java.io.FileOutputStream; \n" +
				"import java.io.IOException; \n" +
				"import java.io.ObjectOutputStream; \n" +
				"import java.io.UncheckedIOException; \n" +
				"import java.lang.module.ModuleFinder; \n" +
				"import java.util.List; \n" +
				"import java.util.Set; \n" +
				"import java.util.TreeSet; \n" +
				"\n" +
				"import io.earcam.instrumental.module.jpms.ExportModifier; \n" +
				"import io.earcam.instrumental.module.jpms.ModuleInfo; \n" +
				"import io.earcam.instrumental.module.jpms.ModuleInfoBuilder; \n" +
				"import io.earcam.instrumental.module.jpms.ModuleModifier; \n" +
				"import io.earcam.instrumental.module.jpms.RequireModifier; \n" +
				"\n" +
				"\n" +
				"	List<ModuleInfo> l = ModuleFinder.ofSystem().findAll().stream() \n" +
				"		    .map(java.lang.module.ModuleReference::descriptor)  \n" +
				"		    .map(d -> { \n" +
				"				ModuleInfoBuilder builder = ModuleInfo.moduleInfo() \n" +
				"						.named(d.name()) \n" +
				"						.withAccess(d.modifiers().stream() \n" +
				"								.map(Enum::name) \n" +
				"								.map(e -> Enum.valueOf(ModuleModifier.class, e)) \n" +
				"								.collect(toSet())) \n" +
				"						.packaging(new TreeSet<>(d.packages())) \n" +
				"						.using(new TreeSet<>(d.uses())); \n" +
				"\n" +
				"				d.rawVersion().ifPresent(builder::versioned); \n" +
				"				d.mainClass().ifPresent(builder::launching); \n" +
				"\n" +
				"				d.requires().forEach(r -> { \n" +
				"					Set<RequireModifier> modifiers = r.modifiers().stream() \n" +
				"							.map(Enum::name) \n" +
				"							.map(e -> Enum.valueOf(RequireModifier.class, e)) \n" +
				"							.collect(toSet()); \n" +
				"					builder.requiring(r.name(), modifiers, r.rawCompiledVersion().orElse(null)); \n" +
				"				}); \n" +
				"\n" +
				"				d.exports().forEach(e -> { \n" +
				"					Set<ExportModifier> modifiers = e.modifiers().stream() \n" +
				"							.map(Enum::name) \n" +
				"							.map(m -> Enum.valueOf(ExportModifier.class, m)) \n" +
				"							.collect(toSet()); \n" +
				"					builder.exporting(e.source(), modifiers, new TreeSet<>(e.targets())); \n" +
				"				}); \n" +
				"\n" +
				"				d.opens().forEach(e -> { \n" +
				"					Set<ExportModifier> modifiers = e.modifiers().stream() \n" +
				"							.map(Enum::name) \n" +
				"							.map(m -> Enum.valueOf(ExportModifier.class, m)) \n" +
				"							.collect(toSet()); \n" +
				"					builder.opening(e.source(), modifiers, new TreeSet<>(e.targets())); \n" +
				"				}); \n" +
				"\n" +
				"				d.provides().forEach(p -> builder.providing(p.service(), new TreeSet<>(p.providers()))); \n" +
				"\n" +
				"				return builder.construct(); \n" +
				"			}) \n" +
				"		    .collect(java.util.stream.Collectors.toList()); \n" +
				"\n" +
				"	try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(\"" + output.toAbsolutePath() + "\")))) { \n" +
				"	    oos.writeObject(l); \n" +
				"	} catch(IOException ioe) { \n" +
				"	    throw new UncheckedIOException(ioe); \n" +
				"	} \n" +
				// printed as individual chars concatenated, in case script error causes dumping
				// of script source (and we're checking output for "DONE" aside from exit code)
				"	System.out.println(\"D\" + \"O\" + \"N\" + \"E\"); \n" +
				"";
	}


	private static void execute(Path jdkHome, String script) throws IOException, InterruptedException
	{
		Process process = launchJShell(jdkHome);
		executeScript(script, process);
		processOutput(process);
	}


	@SuppressWarnings("squid:S4721")
	private static Process launchJShell(Path jdk9Home) throws IOException
	{
		Path jshell = jdk9Home.resolve(Paths.get("bin", "jshell"));
		String classpath = System.getProperty("java.class.path");
		return new ProcessBuilder(jshell.toString(), "--class-path", classpath).start();
	}


	private static void executeScript(String script, Process process) throws IOException
	{
		try(OutputStream os = process.getOutputStream()) {
			os.write(script.getBytes(StandardCharsets.UTF_8));
		}
	}


	static void processOutput(Process process) throws IOException, InterruptedException
	{
		process.waitFor();
		String output = new String(inputStreamToBytes(process.getInputStream()), UTF_8);
		if(process.exitValue() != 0 || !output.contains("DONE")) {
			String error = new String(inputStreamToBytes(process.getErrorStream()), UTF_8);
			throw new IOException("output: " + output + "\nerror: " + error);
		}
	}


	private static byte[] inputStreamToBytes(InputStream input) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int read = input.read();
		while(read != -1) {
			baos.write(read);
			read = input.read();
		}
		return baos.toByteArray();
	}


	@Override
	protected List<ModuleInfo> modules()
	{
		loadCache();
		return modules;
	}


	private synchronized void loadCache()
	{
		if(modules == null) {
			String resource = META_INF + '/' + substituteVersion(CACHE_FILENAME, version);
			InputStream serial = JdkModules.class.getClassLoader().getResourceAsStream(resource);
			Objects.requireNonNull(serial, "Unable to load " + resource);
			modules = deserialize(serial);
		}
	}


	@SuppressWarnings("unchecked")
	static List<ModuleInfo> deserialize(@WillClose InputStream in)
	{
		return List.class.cast(Closing.closeAfterApplying(ObjectInputStream::new, in, ObjectInputStream::readObject));
	}
}
