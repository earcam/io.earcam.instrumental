/*-
 * #%L
 * io.earcam.instrumental.archive
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
package io.earcam.instrumental.archive;

import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.AsJar.asJar;
import static io.earcam.instrumental.archive.AsMultiReleaseJar.asMultiReleaseJar;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.objectweb.asm.Opcodes.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.lang.model.SourceVersion;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import io.earcam.instrumental.reflect.Methods;

public class AsMultiReleaseJarTest {

	private static final byte[] CAFEBABE = { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE };

	private static final String CLASS_MAIN = "io/earcam/acme/gen/asm/Main";
	private static final String CLASS_SUPPORTED = "io/earcam/acme/gen/asm/SupportedVersion";
	private static final String CLASS_WHICH = "io/earcam/acme/gen/asm/WhichVersion";

	private static final Path BASE_DIR = Paths.get(".", "target", AsMultiReleaseJarTest.class.getSimpleName(), Long.toString(System.currentTimeMillis()));


	// @formatter:off
	
	private static byte[] whichVersionByteCode(int version) throws Exception
	{
		if(version < 8) {
			throw new UnsupportedOperationException();
		}
		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		cw.visit(classVersion(version), ACC_PUBLIC + ACC_FINAL + ACC_SUPER, CLASS_WHICH, null, "java/lang/Object", null);

		cw.visitSource("WhichVersion.java", null);


		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(3, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "L" + CLASS_WHICH + ";", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "javaVersion", "()I", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(8, l0);
			mv.visitIntInsn(BIPUSH, version);
			mv.visitInsn(IRETURN);
			mv.visitMaxs(1, 0);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}


	private static int classVersion(int version)
	{
		int classVersion = 0 << 16 | (44 + version);
		return classVersion;
	}


	@Test
	public void multiReleaseWithNoInterDependencies() throws Exception
	{
		Path baseDir = BASE_DIR.resolve("no-interdependencies");
		
		String name = CLASS_WHICH + ".class";
		
		Path jar = archive().configured(asMultiReleaseJar()
				.base(RELEASE_8,
					archive()
						.with(name, whichVersionByteCode( 8))
				)
				.release(9, archive()
						.with(name, whichVersionByteCode( 9))
				)
				.release(10,
					archive()
						.with(name, whichVersionByteCode(10))
				)
		).configured(
			asJar()
				// Multi-Release jars don't appear to work without a nod to JPMS (when loaded by URLClassLoader)
				.withManifestHeader("Automatic-Module-Name", "no.interdependencies"))
		.to(baseDir.resolve("test.jar"));

		try(URLClassLoader loader = new URLClassLoader(new URL[]{ jar.toUri().toURL() })) {
			Class<?> loaded = loader.loadClass(CLASS_WHICH.replace('/', '.'));
			
			Method method = Methods.getMethod(loaded, "javaVersion").orElseThrow(NullPointerException::new);
			
			Object response = method.invoke(null);

			// This test is run with JDK 8, 9 and 10
			assertThat(response, is(equalTo(SourceVersion.latestSupported().ordinal())));
		}
	}
	
	
	
	
	public static byte[] supportedVersionByteCode(int version) throws Exception
	{
		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		cw.visit(classVersion(version), ACC_PUBLIC + ACC_SUPER, CLASS_SUPPORTED, null, "java/lang/Object", null);

		cw.visitSource("SupportedVersion.java", null);

		{
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(5, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "L" + CLASS_SUPPORTED + ";", null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "javaVersion", "()I", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(9, l0);
		mv.visitMethodInsn(INVOKESTATIC, "javax/lang/model/SourceVersion", "latestSupported", "()Ljavax/lang/model/SourceVersion;", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, "javax/lang/model/SourceVersion", "ordinal", "()I", false);
		mv.visitInsn(IRETURN);
		mv.visitMaxs(1, 0);
		mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
		}
		

	public static byte[] mainByteCode(int version) throws Exception
	{
		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		cw.visit(classVersion(version), ACC_PUBLIC + ACC_SUPER, CLASS_MAIN, null, "java/lang/Object", null);

		cw.visitSource("Main.java", null);

		{
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(3, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "L" + CLASS_MAIN + ";", null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(8, l0);
		mv.visitIntInsn(BIPUSH, version);
		mv.visitVarInsn(ISTORE, 1);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(9, l1);
		mv.visitMethodInsn(INVOKESTATIC, "io/earcam/acme/gen/asm/SupportedVersion", "javaVersion", "()I", false);
		mv.visitVarInsn(ILOAD, 1);
		Label l2 = new Label();
		mv.visitJumpInsn(IF_ICMPEQ, l2);
		Label l3 = new Label();
		mv.visitLabel(l3);
		mv.visitLineNumber(10, l3);
		mv.visitTypeInsn(NEW, "java/lang/IllegalStateException");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalStateException", "<init>", "()V", false);
		mv.visitInsn(ATHROW);
		mv.visitLabel(l2);
		mv.visitLineNumber(12, l2);
		mv.visitFrame(F_APPEND,1, new Object[] {INTEGER}, 0, null);
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("Version is %d\n");
		mv.visitInsn(ICONST_1);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		mv.visitInsn(DUP);
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ILOAD, 1);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		mv.visitInsn(AASTORE);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "printf", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;", false);
		mv.visitInsn(POP);
		Label l4 = new Label();
		mv.visitLabel(l4);
		mv.visitLineNumber(13, l4);
		mv.visitInsn(RETURN);
		Label l5 = new Label();
		mv.visitLabel(l5);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, l0, l5, 0);
		mv.visitLocalVariable("version", "I", null, l1, l5, 1);
		mv.visitMaxs(6, 2);
		mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
	
	


	@Test
	public void multiReleaseWithInterDependencies() throws Exception
	{
		Path baseDir = BASE_DIR.resolve("interdependencies");
		
		String mainName = CLASS_MAIN + ".class";
		
		Path jar = archive().configured(
			asMultiReleaseJar()
				.base(RELEASE_8,
					archive()
						.with(CLASS_SUPPORTED + ".class", supportedVersionByteCode(8))
						.with(mainName, mainByteCode(8))
				).release(9,
					archive()
							.with(mainName, mainByteCode(9))
				).release(10,
					archive()
							.with(mainName, mainByteCode(10))
				)
		).configured(
			asJar()
				// Multi-Release jars don't appear to work without a nod to JPMS (when loaded by URLClassLoader)
				.withManifestHeader("Automatic-Module-Name", "interdependencies")
				.launching(CLASS_MAIN.replace('/', '.'))
		)
		.to(baseDir.resolve("test.jar"));
		
		try(URLClassLoader loader = new URLClassLoader(new URL[]{ jar.toUri().toURL() })) {
			Class<?> mainClass = loader.loadClass(CLASS_MAIN.replace('/', '.'));
			assertJavaMajorVersion(mainClass, SourceVersion.latestSupported().ordinal());

			Class<?> supportedClass = loader.loadClass(CLASS_SUPPORTED.replace('/', '.'));
			assertJavaMajorVersion(supportedClass, 8);

			// This test is run with JDK 8, 9 and 10 - the main method throws on version mismatch
			Method main = Methods.getMethod(mainClass, "main", String[].class).orElseThrow(NullPointerException::new);
			main.invoke(null, new Object[] { new String[0] });
		}
	}
	
	


	private void assertJavaMajorVersion(Class<?> type, int version) throws IOException
	{
		int classFileMajorVersion = classVersion(version);
		String name = type.getCanonicalName().replace('.', '/') + ".class";
		ClassLoader loader = type.getClassLoader();

		try(InputStream inputStream = loader.getResourceAsStream(name)) {
			byte[] bytes = readFirstEightBytes(inputStream);

			assertMagicNumber(bytes);
			assertTargetVersion(bytes, classFileMajorVersion);
		}
	}


	private static void assertMagicNumber(byte[] compiled)
	{
		assertThat(compiled[0], is(CAFEBABE[0]));
		assertThat(compiled[1], is(CAFEBABE[1]));
		assertThat(compiled[2], is(CAFEBABE[2]));
		assertThat(compiled[3], is(CAFEBABE[3]));
	}


	private void assertTargetVersion(byte[] compiled, int classFileMajorVersion)
	{
		int inClassFile = compiled[7];
		assertThat(inClassFile, is(classFileMajorVersion));
	}


	private static byte[] readFirstEightBytes(InputStream in) throws IOException
	{
		byte[] buf = new byte[8];
		int b, i = 0;
		while((b = in.read()) != -1 && i < 8) {
			buf[i++] = (byte) b;
		}
		return buf;
	}

	// @formatter:on
}
