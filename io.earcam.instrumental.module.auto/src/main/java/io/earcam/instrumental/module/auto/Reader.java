/*-
 * #%L
 * io.earcam.instrumental.module.auto
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
package io.earcam.instrumental.module.auto;

import static org.objectweb.asm.Opcodes.ASM6;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.annotation.WillNotClose;
import javax.annotation.concurrent.ThreadSafe;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.AnnotationRemapper;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.FieldRemapper;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SignatureRemapper;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import io.earcam.utilitarian.io.ExplodedJarInputStream;
import io.earcam.utilitarian.io.IoStreams;

/**
 *
 * We need the full kaboodle when processing our own classes, but when processing
 * our dependencies we only want manifest (for OSGi) and module-info.class (for JPMS)
 *
 */
@ThreadSafe
public class Reader {

	private final class TypeMapper extends ClassRemapper {

		private final class MethodMapper extends MethodRemapper {
			private MethodMapper(MethodVisitor methodVisitor, Remapper remapper)
			{
				super(methodVisitor, remapper);
			}


			// ASM API quirk?
			@Override
			public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface)
			{
				remapper.mapType(owner);
				remapper.mapMethodName(owner, name, descriptor);
				remapper.mapMethodDesc(descriptor);
				super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
			}


			@Override
			public AnnotationVisitor visitAnnotation(String descriptor, boolean visible)
			{
				return (ignoreAnnotations) ? null : new AnnotationRemapper(super.visitAnnotation(descriptor, visible), remapper);
			}


			@Override
			public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible)
			{
				return (ignoreAnnotations) ? null : new AnnotationRemapper(super.visitInsnAnnotation(typeRef, typePath, descriptor, visible), remapper);
			}


			@Override
			public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index,
					String descriptor, boolean visible)
			{
				return (ignoreAnnotations) ? null
						: new AnnotationRemapper(super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible), remapper);
			}


			@Override
			public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible)
			{
				return (ignoreAnnotations) ? null : new AnnotationRemapper(super.visitParameterAnnotation(parameter, descriptor, visible), remapper);
			}


			@Override
			public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible)
			{
				return (ignoreAnnotations) ? null : new AnnotationRemapper(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible), remapper);
			}


			@Override
			public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible)
			{
				return (ignoreAnnotations) ? null : new AnnotationRemapper(super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible), remapper);
			}
		}

		private final class FieldMapper extends FieldRemapper {
			private FieldMapper(FieldVisitor fieldVisitor, Remapper remapper)
			{
				super(fieldVisitor, remapper);
			}


			@Override
			public AnnotationVisitor visitAnnotation(String descriptor, boolean visible)
			{
				return (ignoreAnnotations) ? null : new AnnotationRemapper(super.visitAnnotation(descriptor, visible), remapper);
			}


			@Override
			public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible)
			{
				return (ignoreAnnotations) ? null : new AnnotationRemapper(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible), remapper);
			}
		}

		private final ImportsOf importsOf;
		private final Set<String> imps;


		private TypeMapper(ImportsOf importsOf, Set<String> imps)
		{
			super(ASM6, new ClassVisitor(ASM6) {}, importsOf);
			this.importsOf = importsOf;
			this.imps = imps;
		}


		@Override
		public void visitEnd()
		{
			super.visitEnd();
			String importer = typeReducer.apply(className.replace('/', '.'));
			imps.remove(importer);
			importConsumer.accept(importer, imps);
		}


		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible)
		{
			return (ignoreAnnotations) ? null : new AnnotationRemapper(super.visitAnnotation(descriptor, visible), remapper);
		}


		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible)
		{
			return (ignoreAnnotations) ? null : new AnnotationRemapper(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible), remapper);
		}


		@Override
		public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value)
		{
			FieldVisitor visitor = super.visitField(access, name, descriptor, signature, value);
			return new FieldMapper(visitor, remapper);
		}


		@Override
		public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
		{
			SignatureReader signatureReader = new SignatureReader((signature == null) ? descriptor : signature);
			SignatureVisitor signatureVisitor = new SignatureRemapper(new SignatureVisitor(ASM6) {}, importsOf);
			signatureReader.accept(signatureVisitor);

			if(exceptions != null) {
				for(int i = 0; i < exceptions.length; i++) {
					importsOf.addInternalType(exceptions[i]);
				}
			}
			MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);

			return new MethodMapper(methodVisitor, remapper);
		}
	}

	private static final Consumer<byte[]> NOOP_BYTECODE_CONSUMER = b -> {};

	private Consumer<Manifest> manifestConsumer = m -> {};
	private Consumer<byte[]> byteCodeConsumer = NOOP_BYTECODE_CONSUMER;
	private BiConsumer<JarEntry, InputStream> entryConsumer = (e, i) -> {};
	private BiConsumer<String, Set<String>> importConsumer = (t, i) -> {};

	private UnaryOperator<String> typeReducer = UnaryOperator.identity();
	private UnaryOperator<String> importedTypeReducer = UnaryOperator.identity();

	private boolean ignoreAnnotations;


	/**
	 * <p>
	 * reader.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.auto.Reader} object.
	 */
	public static Reader reader()
	{
		return new Reader();
	}


	/**
	 * If you pass in {@link #typeToPackageReducer(String)} as a
	 * method reference then it will reduce imported types to packages.
	 *
	 * @param importedTypeReducer a {@link java.util.function.UnaryOperator} object.
	 * @return a {@link io.earcam.instrumental.module.auto.Reader} object.
	 */
	public Reader setImportedTypeReducer(UnaryOperator<String> importedTypeReducer)
	{
		this.importedTypeReducer = importedTypeReducer;
		return this;
	}


	/**
	 * If you pass in {@link #typeToPackageReducer(String)} as a
	 * method reference then it will reduce imported types to packages.
	 *
	 * @param importingTypeReducer a {@link java.util.function.UnaryOperator} object.
	 * @return a {@link io.earcam.instrumental.module.auto.Reader} object.
	 */
	public Reader setImportingTypeReducer(UnaryOperator<String> importingTypeReducer)
	{
		typeReducer = importingTypeReducer;
		return this;
	}


	/**
	 * <p>
	 * typeToPackageReducer.
	 * </p>
	 *
	 * @param type a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String typeToPackageReducer(String type)
	{
		int index = type.lastIndexOf('.');
		return (index == -1) ? type : type.substring(0, index);
	}


	public Reader ignoreAnnotations()
	{
		ignoreAnnotations = true;
		return this;
	}


	/**
	 * Only invoked when processing a JAR (i.e. using {@link #processJar(Path)},
	 * {@link #processJar(InputStream)} or {@link #processJar(JarInputStream)}),
	 * not when invoking {@link #processClass(byte[])}
	 *
	 * @param listener a {@link java.util.function.Consumer} object.
	 * @return a {@link io.earcam.instrumental.module.auto.Reader} object.
	 */
	public Reader addByteCodeListener(Consumer<byte[]> listener)
	{
		byteCodeConsumer = byteCodeConsumer.andThen(listener);
		return this;
	}


	/**
	 * For entries other than classes and Manifest
	 *
	 * @param listener a {@link java.util.function.BiConsumer} object.
	 * @return a {@link io.earcam.instrumental.module.auto.Reader} object.
	 */
	public Reader setJarEntryListener(BiConsumer<JarEntry, InputStream> listener)
	{
		entryConsumer = listener;
		return this;
	}


	/**
	 * <p>
	 * addManifestListener.
	 * </p>
	 *
	 * @param listener a {@link java.util.function.Consumer} object.
	 * @return a {@link io.earcam.instrumental.module.auto.Reader} object.
	 */
	public Reader addManifestListener(Consumer<Manifest> listener)
	{
		manifestConsumer = manifestConsumer.andThen(listener);
		return this;
	}


	/**
	 * <p>
	 * addImportListener.
	 * </p>
	 *
	 * @param listener a {@link java.util.function.BiConsumer} object.
	 * @return a {@link io.earcam.instrumental.module.auto.Reader} object.
	 */
	public Reader addImportListener(BiConsumer<String, Set<String>> listener)
	{
		byteCodeConsumer = byteCodeConsumer.andThen(this::processClass);
		importConsumer = importConsumer.andThen(listener);
		return this;
	}


	/**
	 * <p>
	 * processJar.
	 * </p>
	 *
	 * @param jar a {@link java.nio.file.Path} object.
	 * @throws java.io.IOException if any.
	 */
	public void processJar(Path jar) throws IOException
	{
		try(JarInputStream input = createJarInputStream(jar.toFile())) {
			processJar(input);
		}
	}


	private JarInputStream createJarInputStream(File jar) throws IOException
	{
		return (jar.isDirectory()) ? new JarInputStream(new FileInputStream(jar))
				: ExplodedJarInputStream.explodedJar(jar);
	}


	/**
	 * <p>
	 * processJar.
	 * </p>
	 *
	 * @param input a {@link java.io.InputStream} object.
	 * @throws java.io.IOException if any.
	 */
	public void processJar(@WillNotClose InputStream input) throws IOException
	{
		JarInputStream jin = (input instanceof JarInputStream) ? ((JarInputStream) input) : new JarInputStream(input);
		processJar(jin);
	}


	/**
	 * <p>
	 * processJar.
	 * </p>
	 *
	 * @param input a {@link java.util.jar.JarInputStream} object.
	 * @throws java.io.IOException if any.
	 */
	public void processJar(@WillNotClose JarInputStream input) throws IOException
	{
		processManifest(input);

		JarEntry entry;
		while((entry = input.getNextJarEntry()) != null) {
			processEntry(entry, input);
		}
	}


	private void processEntry(JarEntry entry, JarInputStream input)
	{
		if(isClassEntry(entry) && !noopByteCodeConsumer()) {
			byte[] bytes = IoStreams.readAllBytes(input);
			byteCodeConsumer.accept(bytes);
		} else {
			entryConsumer.accept(entry, input);
		}
	}


	private void processManifest(JarInputStream input)
	{
		Optional.ofNullable(input.getManifest()).ifPresent(manifestConsumer);
	}


	private boolean noopByteCodeConsumer()
	{
		return byteCodeConsumer == NOOP_BYTECODE_CONSUMER;
	}


	private static boolean isClassEntry(JarEntry entry)
	{
		return !entry.isDirectory() && entry.getName().endsWith(".class");
	}


	/**
	 * <p>
	 * processClass.
	 * </p>
	 *
	 * @param bytecode an array of {@link byte} objects.
	 */
	public void processClass(byte[] bytecode)
	{
		ClassReader reader = new ClassReader(bytecode);

		Set<String> imps = new HashSet<>();

		Consumer<String> gut = i -> {
			String reduced = importedTypeReducer.apply(i);
			imps.add(reduced);
		};

		ImportsOf importsOf = new ImportsOf(gut);

		ClassRemapper remapper = new TypeMapper(importsOf, imps);
		reader.accept(remapper, 0);

	}
}
