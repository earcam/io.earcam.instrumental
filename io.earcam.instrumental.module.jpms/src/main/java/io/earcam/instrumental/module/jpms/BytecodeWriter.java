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

import static org.objectweb.asm.Opcodes.ACC_MODULE;
import static org.objectweb.asm.Opcodes.ASM6;

import java.util.Arrays;
import java.util.Objects;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ModuleVisitor;

import io.earcam.utilitarian.charstar.CharSequences;

/**
 * Handles ASM bits:
 * 1. Creates the class writer returning this modulevisitor
 * 2. Converts JLS names to internal names
 */
class BytecodeWriter extends ModuleVisitor {

	/**
	 * <p>
	 * Constructor for BytecodeWriter.
	 * </p>
	 *
	 * @param mv a {@link org.objectweb.asm.ModuleVisitor} object.
	 */
	public BytecodeWriter(ModuleVisitor mv)
	{
		super(ASM6, mv);
	}


	static byte[] toBytecode(int jdkVersion, ModuleInfo module)
	{
		int asmJdkVersion = 0 << 16 | (44 + jdkVersion);
		ClassWriter writer = new ClassWriter(ASM6);
		writer.visit(asmJdkVersion, ACC_MODULE, "module-info", null, null, null);
		BytecodeWriter visitor = new BytecodeWriter(writer.visitModule(module.name(), module.access(), module.version()));
		if(module.mainClass() != null) {
			visitor.visitMainClass(module.mainClass());
		}

		module.packages().stream()
				.forEach(visitor::visitPackage);

		module.requires().forEach(r -> visitor.visitRequire(r.module(), r.access(), r.version()));
		module.exports().forEach(e -> visitor.visitExport(e.paquet(), e.access(), e.modules()));
		module.opens().forEach(o -> visitor.visitOpen(o.paquet(), o.access(), o.modules()));
		module.uses().forEach(visitor::visitUse);
		module.provides().forEach(visitor::visitProvide);
		visitor.visitEnd();
		writer.visitEnd();
		return writer.toByteArray();

	}


	@Override
	public void visitExport(String packaze, int access, String... modules)
	{
		super.visitExport(internalName(packaze), access, modules);
	}


	static String internalName(CharSequence qualifiedName)
	{
		return CharSequences.replace(qualifiedName, '.', '/').toString();
	}


	@Override
	public void visitMainClass(String mainClass)
	{
		super.visitMainClass(internalName(mainClass));
	}


	@Override
	public void visitOpen(String packaze, int access, String... modules)
	{
		super.visitOpen(internalName(packaze), access, modules);
	}


	/**
	 * <p>
	 * visitPackage.
	 * </p>
	 *
	 * @param packaze a {@link java.lang.CharSequence} object.
	 */
	public void visitPackage(CharSequence packaze)
	{
		visitPackage(Objects.toString(packaze));
	}


	@Override
	public void visitPackage(String packaze)
	{
		super.visitPackage(internalName(packaze));
	}


	@Override
	public void visitProvide(String service, String... providers)
	{
		super.visitProvide(internalName(service), internalNames(providers));
	}


	static String[] internalNames(String[] providers)
	{
		return Arrays.stream(providers)
				.map(BytecodeWriter::internalName)
				.toArray(s -> new String[s]);
	}


	@Override
	public void visitUse(String service)
	{
		super.visitUse(internalName(service));
	}
}
