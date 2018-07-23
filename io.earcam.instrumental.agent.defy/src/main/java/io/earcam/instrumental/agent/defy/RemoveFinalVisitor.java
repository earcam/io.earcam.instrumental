/*-
 * #%L
 * io.earcam.instrumental.agent.defy
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
package io.earcam.instrumental.agent.defy;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ASM6;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

class RemoveFinalVisitor extends ClassVisitor {

	private boolean isInterface;


	RemoveFinalVisitor(ClassVisitor cv)
	{
		super(ASM6, cv);
	}


	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		isInterface = isInterface(access);
		cv.visit(version, removeFinalModifier(access), name, signature, superName, interfaces);
	}


	private boolean isInterface(int access)
	{
		return (access & ACC_INTERFACE) == ACC_INTERFACE;
	}


	private int removeFinalModifier(int access)
	{
		return isInterface ? access : access & ~ACC_FINAL;
	}


	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		return super.visitMethod(removeFinalModifier(access), name, desc, signature, exceptions);
	}


	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
	{
		return super.visitField(removeFinalModifier(access), name, desc, signature, value);
	}
}
