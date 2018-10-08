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

import static org.objectweb.asm.Opcodes.ASM6;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.annotation.WillNotClose;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;

final class BytecodeReader extends ModuleVisitor {

	private final ModuleInfoBuilder module;


	/**
	 * <p>
	 * Constructor for BytecodeReader.
	 * </p>
	 *
	 * @param module a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public BytecodeReader(ModuleInfoBuilder module)
	{
		super(ASM6);
		this.module = module;
	}


	static ModuleInfo read(byte[] bytecode)
	{
		return read(new ClassReader(bytecode));
	}


	static ModuleInfo read(@WillNotClose InputStream bytecode) throws IOException
	{
		return read(new ClassReader(bytecode));
	}


	static ModuleInfo read(ClassReader reader)
	{
		ModuleInfoBuilder module = new DefaultModuleInfo();

		BytecodeReader visitor = new BytecodeReader(module);

		ClassVisitor classVisitor = new ClassVisitor(ASM6) {
			@Override
			public ModuleVisitor visitModule(String name, int access, String version)
			{
				module.named(name).withAccess(access).versioned(version);
				return visitor;
			}
		};

		reader.accept(classVisitor, 0);

		return module.construct();

	}


	@Override
	public void visitExport(String paquet, int access, String... modules)
	{
		module.exporting(externalName(paquet), access, modules);
	}


	private String externalName(String paquet)
	{
		return paquet.replace('/', '.');
	}


	@Override
	public void visitMainClass(String mainClass)
	{
		module.launching(externalName(mainClass));
	}


	@Override
	public void visitOpen(String paquet, int access, String... modules)
	{
		module.opening(externalName(paquet), access, modules);
	}


	@Override
	public void visitPackage(String paquet)
	{
		module.packaging(externalName(paquet));
	}


	@Override
	public void visitProvide(String service, String... providers)
	{
		module.providing(externalName(service), externalNames(providers));
	}


	private String[] externalNames(String[] internalNames)
	{
		return Arrays.stream(internalNames)
				.map(this::externalName)
				.toArray(s -> new String[s]);
	}


	@Override
	public void visitRequire(String moduleName, int access, String version)
	{
		module.requiring(moduleName, access, version);
	}


	@Override
	public void visitUse(String service)
	{
		module.using(externalName(service));
	}
}
