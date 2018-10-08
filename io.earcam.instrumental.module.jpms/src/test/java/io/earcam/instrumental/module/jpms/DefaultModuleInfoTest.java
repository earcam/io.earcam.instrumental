/*-
 * #%L
 * io.earcam.instrumental.module.jpms
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
package io.earcam.instrumental.module.jpms;

import static io.earcam.instrumental.module.jpms.Access.ACC_MANDATED;
import static io.earcam.instrumental.module.jpms.Access.ACC_STATIC_PHASE;
import static io.earcam.instrumental.module.jpms.Access.ACC_SYNTHETIC;
import static io.earcam.instrumental.module.jpms.Access.ACC_TRANSITIVE;
import static io.earcam.instrumental.module.jpms.ModuleInfo.moduleInfo;
import static io.earcam.instrumental.module.jpms.ModuleModifier.OPEN;
import static io.earcam.instrumental.module.jpms.ModuleModifier.SYNTHETIC;
import static io.earcam.instrumental.module.jpms.RequireModifier.STATIC;
import static io.earcam.instrumental.module.jpms.RequireModifier.TRANSITIVE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneId.systemDefault;
import static java.util.Arrays.asList;
import static java.util.EnumSet.of;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.objectweb.asm.Opcodes.ASM6;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.UUID;
import java.util.jar.JarInputStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.ops4j.pax.tinybundles.core.TinyBundles;

import com.acme.meh.DummyComparator;

import io.earcam.utilitarian.io.IoStreams;

public class DefaultModuleInfoTest {

	DefaultModuleInfo module = (DefaultModuleInfo) createWithPrimitives().construct();


	private static ModuleInfoBuilder createWithPrimitives()
	{
		return create()
				.exporting("com.acme.exporting", ACC_MANDATED, "mod.a", "mod.b")
				.opening("com.acme.opening", ACC_MANDATED, "mod.x", "mod.y")
				.requiring("java.base", ACC_MANDATED, "9");

	}


	private static ModuleInfoBuilder createWithCollections()
	{
		return create()
				.exporting("com.acme.exporting", EnumSet.of(ExportModifier.MANDATED), new TreeSet<>(asList("mod.a", "mod.b")))
				.opening("com.acme.opening", EnumSet.of(ExportModifier.MANDATED), new TreeSet<>(asList("mod.x", "mod.y")))
				.requiring("java.base", EnumSet.of(RequireModifier.MANDATED), "9");
	}


	private static ModuleInfoBuilder create()
	{
		return ModuleInfo.moduleInfo()
				.named("com.acme.module.aye")
				.versioned("42.0.0")
				.withAccess(ACC_MANDATED)
				.providing("com.acme.api.Service", "com.acme.imp.DefaultService")
				.using("com.acme.api.AuthenticationService")
				.launching("main.Clazz")
				.packaging("com.acme.imp.internal");

	}


	@Test
	public void notEqualWhenNameDiffers()
	{
		ModuleInfo other = createWithCollections().named("a.rose.by.any.other").construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenVersionDiffers()
	{
		ModuleInfo other = create().versioned("1001.0.999998").construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenAccessDiffers()
	{
		ModuleInfo other = createWithCollections().withAccess(ACC_SYNTHETIC).construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenExportsDiffer()
	{
		ModuleInfo other = createWithCollections().exporting("some.other", ACC_MANDATED, "mod.z").construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenOpensDiffer()
	{
		ModuleInfo other = createWithPrimitives().opening("some.other", ACC_MANDATED, "mod.z").construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenProvideImplementationsDiffer()
	{
		ModuleInfo other = createWithCollections().providing("com.acme.api.Service", "com.acme.other.imp.NotTheUsualService").construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenProvidesDiffer()
	{
		ModuleInfo other = createWithPrimitives().providing("some.other.api.Funky", "some.other.imp.FunkyChicken").construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenRequiresDiffer()
	{
		ModuleInfo other = createWithCollections().requiring("module.x", ACC_MANDATED, "1001").construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenUseDiffers()
	{
		ModuleInfo other = createWithPrimitives().using("some.other.api.Funky").construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenUsesDiffer()
	{
		TreeSet<String> uses = new TreeSet<>();
		uses.add("some.other.api.Funky");
		uses.add("some.other.api.Chicken");

		ModuleInfo other = createWithPrimitives().using(uses).construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenMainClassDiffers()
	{
		ModuleInfo other = createWithCollections().launching("some.other.imp.Main").construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenPackageDiffers()
	{
		ModuleInfo other = createWithPrimitives().packaging("some.other").construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualWhenPackagesDiffer()
	{
		TreeSet<CharSequence> packages = new TreeSet<>();
		packages.add("some.other");
		packages.add("and.another");
		ModuleInfo other = createWithPrimitives().packaging(packages).construct();

		assertThat(module, is(not(equalTo(other))));
	}


	@Test
	public void notEqualToNullType()
	{
		assertFalse(module.equals((ModuleInfo) null));
	}


	@Test
	public void notEqualToNullObject()
	{
		assertFalse(module.equals((Object) null));
	}


	@Test
	public void hashCodeShouldBeNonZero()
	{
		assertThat(module.hashCode(), is(not(0)));
	}


	@Test
	public void equal()
	{
		ModuleInfo identical = createWithCollections().construct();
		assertThat(module, is(equalTo(identical)));
		assertThat(module.hashCode(), is(equalTo(identical.hashCode())));
	}


	@Test
	public void symmetricBytecode()
	{
		byte[] bytecode = module.toBytecode();

		ModuleInfo rehydrated = ModuleInfo.read(bytecode);

		assertThat(module, is(equalTo(rehydrated)));
	}


	@Test
	public void deconstructReturnSameInstance()
	{
		ModuleInfoBuilder deconstructed = module.deconstruct();

		assertThat(deconstructed, is(sameInstance(module)));
	}


	@Test
	public void bytecodeOnlyContainsInternalNames()
	{
		byte[] bytecode = module.toBytecode();

		List<String> qualifiedNames = new ArrayList<>();

		ClassVisitor classVisitor = new ClassVisitor(ASM6) {
			@Override
			public ModuleVisitor visitModule(String name, int access, String version)
			{
				return new ModuleVisitor(ASM6, super.visitModule(name, access, version)) {

					@Override
					public void visitExport(String packaze, int access, String... modules)
					{
						qualifiedNames.add(packaze);
						super.visitExport(packaze, access, modules);
					}


					@Override
					public void visitMainClass(String mainClass)
					{
						qualifiedNames.add(mainClass);
						super.visitMainClass(mainClass);
					}


					@Override
					public void visitOpen(String packaze, int access, String... modules)
					{
						qualifiedNames.add(packaze);
						super.visitOpen(packaze, access, modules);
					}


					@Override
					public void visitPackage(String packaze)
					{
						qualifiedNames.add(packaze);
						super.visitPackage(packaze);
					}


					@Override
					public void visitProvide(String service, String... providers)
					{
						qualifiedNames.add(service);
						Arrays.stream(providers).forEach(qualifiedNames::add);
						super.visitProvide(service, providers);
					}


					@Override
					public void visitUse(String service)
					{
						qualifiedNames.add(service);
						super.visitUse(service);
					}
				};
			}
		};

		ClassReader reader = new ClassReader(bytecode);
		reader.accept(classVisitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

		Stream<String> stream = Stream.of(BytecodeWriter.internalName(module.mainClass()));

		stream = Stream.concat(
				stream,
				module.packages().stream().map(BytecodeWriter::internalName));

		stream = Stream.concat(
				stream,
				module.exports().stream().map(Export::paquet).map(BytecodeWriter::internalName));

		stream = Stream.concat(
				stream,
				module.opens().stream().map(Export::paquet).map(BytecodeWriter::internalName));

		stream = Stream.concat(
				stream,
				module.uses().stream().map(BytecodeWriter::internalName));

		stream = Stream.concat(
				stream,
				module.provides().entrySet().stream()
						.flatMap(e -> Stream.concat(Stream.of(e.getKey()), Arrays.stream(e.getValue())))
						.map(BytecodeWriter::internalName));

		List<String> expected = stream.collect(toList());

		// note order dependent..
		assertThat(qualifiedNames, equalTo(expected));
	}


	@Test
	public void versionIsOptional()
	{
		byte[] bytecode = module.versioned(null).construct().toBytecode();

		ModuleInfo rehydrated = ModuleInfo.read(bytecode);

		assertThat(module, is(equalTo(rehydrated)));
	}


	@Test
	public void versionIsOptionalFromInputStream() throws IOException
	{
		byte[] bytecode = module.versioned(null).construct().toBytecode();

		ModuleInfo rehydrated = ModuleInfo.read(new ByteArrayInputStream(bytecode));

		assertThat(module, is(equalTo(rehydrated)));
	}


	@Test
	public void modifierIsMappedFromAccess()
	{
		ModuleInfo m = createWithCollections().withAccess(ACC_SYNTHETIC).construct();

		assertThat(m.modifiers(), containsInAnyOrder(SYNTHETIC));
	}


	@Test
	public void hardedCodedToString()
	{
		String expected = "/**\n" +
				" * @version 42.0.0\n" +
				" * @modifiers mandated\n" +
				" * @package com.acme.imp.internal\n" +
				" */\n" +
				"module com.acme.module.aye {\n" +
				"	/**\n" +
				"	 * @version 9\n" +
				"	 * @modifiers mandated\n" +
				"	 */\n" +
				"	requires java.base;\n" +
				"	/**\n" +
				"	 * @modifiers mandated\n" +
				"	 */\n" +
				"	exports com.acme.exporting to \n" +
				"		mod.a,\n" +
				"		mod.b;\n" +
				"	/**\n" +
				"	 * @modifiers mandated\n" +
				"	 */\n" +
				"	opens com.acme.opening to \n" +
				"		mod.x,\n" +
				"		mod.y;\n" +
				"	uses com.acme.api.AuthenticationService;\n" +
				"	provides com.acme.api.Service with \n" +
				"		com.acme.imp.DefaultService;\n" +
				"}\n" +
				"";

		assertThat(module, hasToString(equalToIgnoringWhiteSpace(expected)));
	}


	@Test
	public void openModuleToString()
	{
		// EARCAM_SNIPPET_BEGIN: to-string-is-source
		ModuleInfo openModule = ModuleInfo.moduleInfo()
				.named("com.acme.oh.pan")
				.versioned("99")
				.withAccess(of(OPEN))
				.requiring("java.base", ACC_MANDATED, "9")
				.construct();

		String expected = "/**\n" +
				" * @version 99\n" +
				" * @modifiers open\n" +
				" */\n" +
				"open module com.acme.oh.pan {\n" +
				"	/**\n" +
				"	 * @version 9\n" +
				"	 * @modifiers mandated" +
				"	 */\n" +
				"	requires java.base;\n" +
				"}\n";

		assertThat(openModule, hasToString(equalToIgnoringWhiteSpace(expected)));
		// EARCAM_SNIPPET_END: to-string-is-source
	}


	@Test
	public void hardedCodedToStringForVirtuallyEmptyModule()
	{

		ModuleInfo m = ModuleInfo.moduleInfo()
				.named("com.acme.empty.virtually")
				.requiring("java.base", 0, null)
				.exporting("com.acme.much.not", ACC_MANDATED)
				.opening("com.acme.innards", 0)
				.construct();

		String expected = "module com.acme.empty.virtually {\n" +
				"	requires java.base;\n" +
				"	/**\n" +
				"	 * @modifiers mandated\n" +
				"	 */\n" +
				"	exports com.acme.much.not;\n" +
				"	opens com.acme.innards;\n" +
				"}\n" +
				"";

		assertThat(m, hasToString(equalToIgnoringWhiteSpace(expected)));
	}


	@Test
	public void requiresFromOtherModule()
	{
		ModuleInfo required = moduleInfo()
				.named("am.required")
				.versioned("1.2.3")
				.withAccess(ACC_MANDATED)
				.construct();

		ModuleInfo requiring = ModuleInfo.moduleInfo()
				.named("am.requiring")
				.requiring(required)
				.construct();

		assertThat(requiring.requires(), contains(new Require(required.name(), required.access(), required.version())));
	}


	@Test
	public void requiresWithBothTransitiveAndStaticIs()
	{
		final String badRequireModule = "not.like.this";
		try {
			ModuleInfo.moduleInfo()
					.named("com.acme.invalid")
					.requiring(badRequireModule, EnumSet.of(STATIC, TRANSITIVE), "101")
					.construct();
			fail();
		} catch(IllegalStateException e) {
			assertThat(e.getMessage(), containsString(badRequireModule));
		}
	}


	@Test
	public void requiresStaticToString()
	{
		String optionalModuleDep = "com.acme.optional.at.runtime";
		ModuleInfo m = ModuleInfo.moduleInfo()
				.named("com.acme.dodgy.arch")
				.requiring(optionalModuleDep, ACC_STATIC_PHASE, "101.404")
				.construct();

		String expected = "module com.acme.dodgy.arch {\n" +
				"	/**\n" +
				"	 * @version 101.404\n" +
				"	 * @modifiers static" +
				"	 */\n" +
				"	requires static " + optionalModuleDep + ";\n" +
				"}\n";

		assertThat(m, hasToString(equalToIgnoringWhiteSpace(expected)));
	}


	@Test
	public void requiresTransitiveToString()
	{
		String optionalModuleDep = "com.acme.ssh.do.not.tell.the.others";
		ModuleInfo m = ModuleInfo.moduleInfo()
				.named("com.acme.ostrich.depend.on.me")
				.requiring(optionalModuleDep, ACC_TRANSITIVE, "42.god")
				.construct();

		String expected = "module com.acme.ostrich.depend.on.me {\n" +
				"	/**\n" +
				"	 * @version 42.god\n" +
				"	 * @modifiers transitive" +
				"	 */\n" +
				"	requires transitive " + optionalModuleDep + ";\n" +
				"}\n";

		assertThat(m, hasToString(equalToIgnoringWhiteSpace(expected)));
	}


	@Test
	public void extractExisting() throws IOException
	{
		String moduleName = "hoohar";

		ModuleInfo existing = moduleInfo()
				.named(moduleName)
				.exporting(pkg(DummyComparator.class))
				.construct();

		InputStream built = TinyBundles.bundle()
				.symbolicName("not.relevant.right.now")
				.add(DummyComparator.class)
				.add("module-info.class", new ByteArrayInputStream(existing.toBytecode()))
				.build();

		Path jar = Paths.get(".", "target", LocalDateTime.now(systemDefault()) + "_" + UUID.randomUUID() + ".jar");
		IoStreams.transfer(built, new FileOutputStream(jar.toFile()));

		ModuleInfo extracted = ModuleInfo.extract(jar).orElseThrow(NullPointerException::new);

		assertThat(extracted, is(equalTo(existing)));
	}


	@Test
	public void extractExistingFromInputStream() throws IOException
	{
		String moduleName = "hoohar";

		ModuleInfo existing = moduleInfo()
				.named(moduleName)
				.exporting(pkg(DummyComparator.class))
				.construct();

		InputStream built = TinyBundles.bundle()
				.symbolicName("not.relevant.right.now")
				.add(DummyComparator.class)
				.add("module-info.class", new ByteArrayInputStream(existing.toBytecode()))
				.build();

		JarInputStream jar = new JarInputStream(built);

		ModuleInfo extracted = ModuleInfo.extract(jar).orElseThrow(NullPointerException::new);

		assertThat(extracted, is(equalTo(existing)));
	}


	@Test
	public void extractSynthetic() throws IOException
	{
		String moduleName = "hoohar";

		InputStream built = TinyBundles.bundle()
				.symbolicName("not.relevant.right.now")
				.set("Automatic-Module-Name", moduleName)
				.add(DummyComparator.class)
				.add("META-INF/blah-blah/some.resource", new ByteArrayInputStream("blah blah".getBytes(UTF_8)))
				.add("NoPackageVeryBad.class", new FileInputStream("./target/test-classes/NoPackageVeryBad.class"))
				.build();

		Path jar = Paths.get(".", "target", LocalDateTime.now(systemDefault()) + "_" + UUID.randomUUID() + ".jar");
		IoStreams.transfer(built, new FileOutputStream(jar.toFile()));

		ModuleInfo moduleInfo = ModuleInfo.extract(jar).orElseThrow(NullPointerException::new);

		assertThat(moduleInfo.name(), is(equalTo(moduleName)));
		assertThat(moduleInfo.provides(), is(anEmptyMap()));
		assertThat(moduleInfo.exports()
				.stream()
				.map(Export::paquet)
				.collect(toList()), contains(pkg(DummyComparator.class)));

	}


	private String pkg(Class<?> type)
	{
		return type.getPackage().getName();
	}


	private String cn(Class<?> type)
	{
		return type.getCanonicalName();
	}


	@Test
	public void extractSyntheticWithMetaInfServices() throws IOException
	{
		String moduleName = "humbug";

		InputStream built = TinyBundles.bundle()
				.symbolicName("not.relevant.right.now")
				.set("Automatic-Module-Name", moduleName)
				.add("META-INF/servicehistory", new ByteArrayInputStream("one careful owner".getBytes(UTF_8)))
				.add("META-INF/services/java.util.Comparator", new ByteArrayInputStream(cn(DummyComparator.class).getBytes(UTF_8)))
				.build();

		Path jar = Paths.get(".", "target", LocalDateTime.now(systemDefault()) + "_" + UUID.randomUUID() + ".jar");
		IoStreams.transfer(built, new FileOutputStream(jar.toFile()));

		ModuleInfo moduleInfo = ModuleInfo.extract(jar).orElseThrow(NullPointerException::new);

		assertThat(moduleInfo.name(), is(equalTo(moduleName)));
		assertThat(moduleInfo.provides(), allOf(
				aMapWithSize(1),
				hasEntry(equalTo(cn(Comparator.class)), arrayContaining(cn(DummyComparator.class)))));
	}


	@Test
	public void extractNothing() throws IOException
	{
		InputStream built = TinyBundles.bundle()
				.symbolicName("not.relevant.right.now")
				.add("META-INF/services/java.util.Comparator", new ByteArrayInputStream(cn(DummyComparator.class).getBytes(UTF_8)))
				.build();

		Path jar = Paths.get(".", "target", LocalDateTime.now(systemDefault()) + "_" + UUID.randomUUID() + ".jar");
		IoStreams.transfer(built, new FileOutputStream(jar.toFile()));

		Optional<ModuleInfo> found = ModuleInfo.extract(jar);

		assertThat(found.isPresent(), is(false));
	}


	@Test
	public void extractNothingFromExplodedArchive() throws IOException
	{
		Path jar = Paths.get(".", "target", LocalDateTime.now(systemDefault()) + "_" + UUID.randomUUID() + "_empty_exploded_jar");
		jar.toFile().mkdirs();

		Optional<ModuleInfo> found = ModuleInfo.extract(jar);

		assertThat(found.isPresent(), is(false));
	}
}
