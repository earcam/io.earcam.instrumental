/*-
 * #%L
 * io.earcam.instrumental.agent
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
package io.earcam.instrumental.agent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM6;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.ServiceLoader;
import java.util.jar.JarFile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class InstrumentationWrapperTest {

	private static final String FIELD = "$testField";
	private static final int CONSTANT_VALUE = 42;

	private static class Fiddler extends ClassVisitor {

		Fiddler(ClassVisitor cv)
		{
			super(ASM6, cv);
		}


		@Override
		public void visitEnd()
		{
			super.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, FIELD, "I", null, CONSTANT_VALUE);
			super.visitEnd();
		}
	}

	private static class Transformer implements ClassFileTransformer {

		@Override
		public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
				throws IllegalClassFormatException
		{
			ClassReader cr = new ClassReader(classfileBuffer);
			ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES);
			ClassVisitor cv = new Fiddler(cw);
			cr.accept(cv, 0);
			return cw.toByteArray();
		}

	}

	static {
		Instrumentation instrumentation = ServiceLoader.load(Instrumentation.class).iterator().next();

		instrumentation.addTransformer(new Transformer(), true);
	}


	@Test
	void addedTransformerIsUsed() throws ReflectiveOperationException
	{
		Class<?> loaded = getClass().getClassLoader().loadClass("io.earcam.acme.DummyPojo");

		Field field = loaded.getField(FIELD);

		assertThat(field, is(not(nullValue())));

		assertThat(field.get(null), is(CONSTANT_VALUE));
	}

	@Nested
	class Wrapper {

		private final InstrumentationWrapper wrapper = new InstrumentationWrapper();
		private Instrumentation delegate;

		private final JarFile stubJarFile = mock(JarFile.class);
		private final ClassFileTransformer stubTransformer = mock(ClassFileTransformer.class);
		private final ClassDefinition stubClassDefinition = new ClassDefinition(InstrumentationWrapperTest.class, new byte[0]);


		@BeforeEach
		public void before()
		{
			delegate = mock(Instrumentation.class);
			wrapper.setDelegate(delegate);
		}


		@Test
		void getDelegate()
		{
			assertThat(wrapper.getDelegate(), is(sameInstance(delegate)));
		}


		@Test
		void appendToBootstrapClassLoaderSearch()
		{
			wrapper.appendToBootstrapClassLoaderSearch(stubJarFile);

			verify(delegate, only()).appendToBootstrapClassLoaderSearch(stubJarFile);
		}


		@Test
		void appendToSystemClassLoaderSearch()
		{
			wrapper.appendToSystemClassLoaderSearch(stubJarFile);

			verify(delegate, only()).appendToSystemClassLoaderSearch(stubJarFile);
		}


		@Test
		void addTransformer()
		{
			wrapper.addTransformer(stubTransformer);

			verify(delegate, only()).addTransformer(stubTransformer);
		}


		@Test
		void addTransformerCanRetransform()
		{
			wrapper.addTransformer(stubTransformer, true);

			verify(delegate, only()).addTransformer(stubTransformer, true);
		}


		@Test
		void removeTransformer()
		{
			wrapper.removeTransformer(stubTransformer);

			verify(delegate, only()).removeTransformer(stubTransformer);
		}


		@Test
		void retransformClasses() throws UnmodifiableClassException
		{
			wrapper.retransformClasses(InstrumentationWrapperTest.class);

			verify(delegate, only()).retransformClasses(InstrumentationWrapperTest.class);
		}


		@Test
		void redefineClasses() throws UnmodifiableClassException, ClassNotFoundException
		{
			wrapper.redefineClasses(stubClassDefinition);

			verify(delegate, only()).redefineClasses(stubClassDefinition);
		}


		@Test
		void setNativeMethodPrefix()
		{
			wrapper.setNativeMethodPrefix(stubTransformer, "prefix");

			verify(delegate, only()).setNativeMethodPrefix(stubTransformer, "prefix");
		}


		@Test
		void isModifiableClass()
		{
			given(delegate.isModifiableClass(String.class)).willReturn(true);

			assertThat(wrapper.isModifiableClass(String.class), is(true));

			given(delegate.isModifiableClass(String.class)).willReturn(false);

			assertThat(wrapper.isModifiableClass(String.class), is(false));
		}


		@Test
		void isNativeMethodPrefixSupported()
		{
			given(delegate.isNativeMethodPrefixSupported()).willReturn(true);

			assertThat(wrapper.isNativeMethodPrefixSupported(), is(true));

			given(delegate.isNativeMethodPrefixSupported()).willReturn(false);

			assertThat(wrapper.isNativeMethodPrefixSupported(), is(false));
		}


		@Test
		void isRedefineClassesSupported()
		{
			given(delegate.isRedefineClassesSupported()).willReturn(true);

			assertThat(wrapper.isRedefineClassesSupported(), is(true));

			given(delegate.isRedefineClassesSupported()).willReturn(false);

			assertThat(wrapper.isRedefineClassesSupported(), is(false));
		}


		@Test
		void isRetransformClassesSupported()
		{
			given(delegate.isRetransformClassesSupported()).willReturn(true);

			assertThat(wrapper.isRetransformClassesSupported(), is(true));

			given(delegate.isRetransformClassesSupported()).willReturn(false);

			assertThat(wrapper.isRetransformClassesSupported(), is(false));
		}


		@Test
		void getObjectSize()
		{
			Object sized = new Object();
			given(delegate.getObjectSize(sized)).willReturn(42L);

			assertThat(wrapper.getObjectSize(sized), is(42L));
		}


		@Test
		void getInitiatedClasses()
		{
			ClassLoader loader = mock(ClassLoader.class);
			Class<?>[] classes = new Class<?>[] { String.class, Number.class };
			given(delegate.getInitiatedClasses(loader)).willReturn(classes);

			assertThat(wrapper.getInitiatedClasses(loader), is(equalTo(classes)));
		}
	}
}
