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

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

class RemoveFinalTransformer implements ClassFileTransformer {

	private String classNamePrefix;


	/**
	 * Note: Do not use wildcard or regex, prefix only
	 * 
	 * @param classNamePrefix the pattern to match against
	 */
	RemoveFinalTransformer(String classNamePrefix)
	{
		this.classNamePrefix = classNamePrefix.replace('.', '/');
	}


	/** {@inheritDoc} */
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
			throws IllegalClassFormatException
	{
		return className.startsWith(classNamePrefix) ? transform(classfileBuffer) : classfileBuffer;
	}


	private byte[] transform(byte[] classfileBuffer)
	{
		ClassReader cr = new ClassReader(classfileBuffer);
		ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES);
		ClassVisitor cv = new RemoveFinalVisitor(cw);
		cr.accept(cv, 0);
		return cw.toByteArray();
	}
}
