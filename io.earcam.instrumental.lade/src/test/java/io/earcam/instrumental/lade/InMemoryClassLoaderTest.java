/*-
 * #%L
 * io.earcam.instrumental.lade
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
package io.earcam.instrumental.lade;

import static io.earcam.instrumental.lade.ClassLoaders.inMemoryClassLoader;
import static io.earcam.instrumental.lade.InMemoryClassLoader.MANIFEST_PATH;
import static io.earcam.instrumental.lade.InMemoryClassLoader.URL_PROTOCOL;
import static io.earcam.instrumental.reflect.Names.typeToInternalName;
import static io.earcam.instrumental.reflect.Names.typeToResourceName;
import static io.earcam.utilitarian.io.IoStreams.readAllBytes;
import static io.earcam.utilitarian.security.Certificates.certificate;
import static io.earcam.utilitarian.security.KeyStores.keyStore;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.annotation.concurrent.NotThreadSafe;
import javax.lang.model.SourceVersion;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import io.earcam.instrumental.reflect.Methods;
import io.earcam.instrumental.reflect.Resources;
import io.earcam.unexceptional.Exceptional;
import io.earcam.utilitarian.security.Keys;
import io.earcam.utilitarian.security.OpenedKeyStore;
import io.earcam.utilitarian.security.Signatures;

@NotThreadSafe
public class InMemoryClassLoaderTest {

	private static final KeyPair KEYS = Keys.rsa();
	private static final String ALIAS = "alias";
	private static final char[] PASSWORD = "password".toCharArray();
	private static final String SUBJECT = "subject";
	private static final X509Certificate CERTIFICATE = certificate(KEYS, SUBJECT).toX509();
	private static final KeyStore KEYSTORE = keyStore(ALIAS, PASSWORD, KEYS, CERTIFICATE);
	private static final OpenedKeyStore OPENED_KEYSTORE = new OpenedKeyStore(KEYSTORE, ALIAS, PASSWORD);

	static final String CLASS_WHICH = "io.earcam.acme.gen.asm.WhichVersion";
	static final String BINARY_WHICH = CLASS_WHICH.replace('.', '/');
	static final String PATH_WHICH = BINARY_WHICH + ".class";
	static final String MR_PREFIX = "META-INF/versions/";


	@Test
	public void identicallyNamedClassesLoadedAsResourcesFromMultipleJars() throws IOException
	{
		byte[] jarOneBytes = createJar("id", "1");
		byte[] jarTwoBytes = createJar("id", "2");
		// EARCAM_SNIPPET_BEGIN: simple-inmemory
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader()
				.jar(jarOneBytes, "one")
				.jar(jarTwoBytes, "two")) {

			String name = typeToResourceName(InMemoryClassLoaderTest.class);
			Enumeration<URL> resources = classLoader.getResources(name);

			List<URL> earls = enumerationToList(resources);

			// 3 because the loader also delegates to parent
			assertThat(earls, hasSize(3));
		}
		// EARCAM_SNIPPET_END: simple-inmemory
	}


	@Test
	public void identicallyNamedClassesLoadedAsResourcesOnlyFromMultipleJarsInThisClassLoader() throws IOException
	{
		byte[] jarOneBytes = createJar("id", "1");
		byte[] jarTwoBytes = createJar("id", "2");

		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader()
				.jar(jarOneBytes, "one")
				.jar(jarTwoBytes, "two")) {

			String name = typeToResourceName(InMemoryClassLoaderTest.class);
			Enumeration<URL> resources = classLoader.getResources(name, false);

			List<URL> earls = enumerationToList(resources);

			// 2 because the getResources(name, false) does not delegate to parent
			assertThat(earls, hasSize(2));
		}
	}


	@Test
	public void identicallyNamedClassesLoadedByOrphanAsResourcesFromMultipleJars() throws IOException
	{
		byte[] jarOneBytes = createJar("id", "1");
		byte[] jarTwoBytes = createJar("id", "2");

		// EARCAM_SNIPPET_BEGIN: simple-orphan
		try(InMemoryClassLoader classLoader = ClassLoaders.orphanedInMemoryClassLoader()) {
			classLoader.jar(jarOneBytes, "one");
			classLoader.jar(jarTwoBytes, "two");

			String name = typeToResourceName(InMemoryClassLoaderTest.class);
			Enumeration<URL> resources = classLoader.getResources(name);

			List<URL> earls = enumerationToList(resources);

			// 2 because the loader has no parent
			assertThat(earls, hasSize(2));
		}
		// EARCAM_SNIPPET_END: simple-orphan
	}


	@Test
	public void getResourceAsStreamForClassFoundInOrphanedLoader() throws IOException
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.orphanedInMemoryClassLoader()) {
			classLoader.jar(createJar("id", "1"), "one");

			String name = typeToResourceName(InMemoryClassLoaderTest.class);

			try(InputStream input = classLoader.getResourceAsStream(name)) {
				assertThat(input, is(not(nullValue())));
			}
		}
	}


	@Test
	public void getResourceAsStreamForClassFoundInParentLoader() throws IOException
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.orphanedInMemoryClassLoader()) {
			classLoader.jar(createJar("id", "1"), "one");

			String name = typeToResourceName(String.class);
			try(InputStream input = classLoader.getResourceAsStream(name)) {
				assertThat(input, is(not(nullValue())));
			}
		}
	}


	private <T> List<T> enumerationToList(Enumeration<T> resources)
	{
		ArrayList<T> list = new ArrayList<>();
		while(resources.hasMoreElements()) {
			list.add(resources.nextElement());
		}
		return list;
	}


	private byte[] createJar(String manifestKey, String manifestValue)
	{
		Class<?> type = InMemoryClassLoaderTest.class;
		return createJar(type, manifestKey, manifestValue);
	}


	private byte[] createJar(Class<?> type, String manifestKey, String manifestValue)
	{
		return Exceptional.get(() -> readAllBytes(bundle()
				.add(type)
				.set(manifestKey, manifestValue)
				.build()));
	}


	@Test
	public void classFromJarsCanBeLoaded() throws ClassNotFoundException
	{
		canLoadClass(InMemoryClassLoaderTest.class.getCanonicalName());
	}


	public void canLoadClass(String canonicalName) throws ClassNotFoundException
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jar(createJar("SomeKey", "SomeValue"))) {
			Class<?> loaded = classLoader.loadClass(canonicalName);
			assertThat(loaded.getCanonicalName(), is(equalTo(canonicalName)));
		}
	}


	@Test
	public void classFromSystemCanBeLoaded() throws ClassNotFoundException
	{
		canLoadClass(String.class.getCanonicalName());
	}


	@Test
	public void reloadedClassFromJarIsSameInstance() throws ClassNotFoundException
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jar(createJar("SomeKey", "SomeValue"))) {

			Class<?> loaded = classLoader.loadClass(InMemoryClassLoaderTest.class.getCanonicalName());
			Class<?> reloaded = classLoader.loadClass(InMemoryClassLoaderTest.class.getCanonicalName());

			assertThat(reloaded, is(sameInstance(loaded)));
		}
	}


	@Test
	public void missingManifestIsNotAnIssue() throws ClassNotFoundException, IOException
	{
		InputStream jar = new JarInputStream(new ByteArrayInputStream(createJar("SomeKey", "SomeValue"))) {
			public java.util.jar.Manifest getManifest()
			{
				return null;
			}
		};
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jar(jar, "no.manifest.jar")) {

			URL resource = classLoader.getResource(typeToResourceName(InMemoryClassLoaderTest.class));
			URL manifest = classLoader.getResource(MANIFEST_PATH);

			assertThat(resource, is(not(nullValue())));
			assertThat(manifest.getProtocol(), is(not(equalTo(URL_PROTOCOL))));
		}
	}


	@Test
	public void resourceUrlCanBeStreamed() throws IOException
	{
		String key = "SomeKey";
		String value = "SomeValue";
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jars(createJar(key, value))) {

			URL resource = classLoader.getResource(MANIFEST_PATH);

			assertThat(resource.getProtocol(), is(equalTo(URL_PROTOCOL)));

			String contents = new String(readAllBytes(resource.openStream()), UTF_8);

			assertThat(contents, containsString(key + ": " + value));
		}
	}


	@Test
	public void resourceUrlCanBeSerializedIfHandlerSet() throws IOException
	{
		// EARCAM_SNIPPET_BEGIN: add-handler
		io.earcam.instrumental.lade.Handler.addProtocolHandlerSystemProperty();
		// EARCAM_SNIPPET_END: add-handler

		String key = "SomeKey";
		String value = "SomeValue";
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jars(createJar(key, value))) {

			String earl = classLoader.getResource(MANIFEST_PATH).toString();

			URL resource = Exceptional.url(earl);

			assertThat(resource.getProtocol(), is(equalTo(URL_PROTOCOL)));

			String contents = new String(readAllBytes(resource.openStream()), UTF_8);

			assertThat(contents, containsString(key + ": " + value));
		}
	}


	@Test
	public void resourceUrlCannotBeSerializedIfHandlerIsNotSet() throws IOException
	{
		// EARCAM_SNIPPET_BEGIN: remove-handler
		io.earcam.instrumental.lade.Handler.removeProtocolHandlerSystemProperty();
		// EARCAM_SNIPPET_END: remove-handler

		String key = "SomeKey";
		String value = "SomeValue";
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jars(createJar(key, value))) {

			String earl = classLoader.getResource(MANIFEST_PATH).toString();
			new URL(earl);
			fail();
		} catch(MalformedURLException e) {}
	}


	@Test
	public void deserializedUrlForNonsenseResourceReturnsNull() throws IOException
	{
		Handler.addProtocolHandlerSystemProperty();

		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jars(createJar("SomeKey", "SomeValue"))) {

			String earl = classLoader.getResource(MANIFEST_PATH).toString();

			String resource = Exceptional.url(earl).toString();

			resource = resource.substring(0, resource.length() - MANIFEST_PATH.length())
					+ "this/path/will/not/be/Found";

			URL nonsense = new URL(resource);

			assertThat(nonsense.openStream(), is(nullValue()));
		}
	}


	@Test
	public void getResourceWithNoLeadingSlash()
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jars(createJar("SomeKey", "SomeValue"), createJar("OtherKey", "OtherValue"))) {

			URL resource = classLoader.getResource(typeToResourceName(InMemoryClassLoaderTest.class));
			assertThat(resource, is(not(nullValue())));
		}
	}


	@Test
	public void getResourceWithLeadingSlash()
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jars(createJar("SomeKey", "SomeValue"), createJar("OtherKey", "OtherValue"))) {

			URL resource = classLoader.getResource("/" + typeToResourceName(InMemoryClassLoaderTest.class));
			assertThat(resource, is(not(nullValue())));
		}
	}


	@Test
	public void unknownResourceReturnsNull()
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jars(createJar("SomeKey", "SomeValue"), createJar("OtherKey", "OtherValue"))) {

			URL resource = classLoader.getResource(" this ::: is utter !?@£$%^ nonsense");
			assertThat(resource, is(nullValue()));
		}
	}


	@Test
	public void unknownResourceAsStreamReturnsNull()
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jar(createJar("SomeKey", "SomeValue"))) {

			InputStream stream = classLoader.getResourceAsStream(" this ::: is utter !?@£$%^ nonsense");
			assertThat(stream, is(nullValue()));
		}
	}


	@SuppressWarnings("unchecked")
	@Test
	public void resourcesListsAll()
	{
		try(InMemoryClassLoader classLoader = ClassLoaders.inMemoryClassLoader().jars(createJar(InMemoryClassLoaderTest.class, "SomeKey", "SomeValue"))) {

			List<String> resources = classLoader.resources()
					.map(Object::toString)
					.collect(toList());

			assertThat(resources, hasSize(greaterThanOrEqualTo(2)));
			assertThat(resources, hasItems(endsWith("/META-INF/MANIFEST.MF"), endsWith(typeToResourceName(InMemoryClassLoaderTest.class))));
		}
	}


	@Test
	public void resourceUrlCanBeTrimmedToRootPathAndUsedToAccessFullJar() throws IOException
	{
		resourceUrlCanBeTrimmedToAccessFullJar(false);
	}


	private void resourceUrlCanBeTrimmedToAccessFullJar(boolean trimRoot) throws IOException
	{
		Handler.addProtocolHandlerSystemProperty();

		int trimBy = trimRoot ? 1 : 0;

		try(InMemoryClassLoader classLoader = ClassLoaders.orphanedInMemoryClassLoader()
				.jar(createJar(ClassLoaders.class, "AKey", "BeValued"))
				.jar(createJar(InMemoryClassLoaderTest.class, "SomeKey", "SomeValue"))
				.jar(createJar(InMemoryClassLoader.class, "SomeOtherKey", "SomeOtherValue"))) {

			String resourceName = typeToResourceName(InMemoryClassLoaderTest.class);
			String resource = classLoader.getResource(resourceName).toString();
			String trimmed = resource.substring(0, resource.length() - (resourceName.length() + trimBy));

			URL url = new URL(trimmed);

			try(JarInputStream input = new JarInputStream(url.openStream())) {

				assertThat(input.getManifest().getMainAttributes(), hasEntry(new Name("SomeKey"), "SomeValue"));

				JarEntry entry = input.getNextJarEntry();

				assertThat(entry.getName(), startsWith(typeToInternalName(InMemoryClassLoaderTest.class)));
			}
		}
	}


	@Test
	public void resourceUrlCanBeTrimmedToCompletelyRemovePathAndUsedToAccessFullJar() throws IOException
	{
		resourceUrlCanBeTrimmedToAccessFullJar(true);
	}


	@Test
	public void orphanedInMemoryClassLoaderCannotLoadSystemClass() throws Exception
	{
		try {
			ClassLoaders.orphanedInMemoryClassLoader().loadClass(String.class.getCanonicalName());
			fail();
		} catch(ClassNotFoundException cnfe) {
			assertThat(cnfe.getMessage(), containsString(String.class.getCanonicalName()));
		}
	}


	@Test
	public void loadFailsOnNull()
	{
		try {
			ClassLoaders.load(null);
			fail();
		} catch(NullPointerException npe) {}
	}


	@Test
	public void canDefineAClass()
	{
		byte[] bytes = Resources.classAsBytes(InMemoryClassLoaderTest.class);

		Class<?> loaded = inMemoryClassLoader().define(InMemoryClassLoaderTest.class.getCanonicalName(), bytes, null);

		assertThat(loaded.getCanonicalName(), is(equalTo(InMemoryClassLoaderTest.class.getCanonicalName())));
		assertThat(loaded, is(not(equalTo(InMemoryClassLoaderTest.class))));
	}


	@Test
	public void whenDefiningAClassTheNameMustEqualThatInTheBytecode()
	{
		byte[] bytes = Resources.classAsBytes(InMemoryClassLoaderTest.class);

		try {
			inMemoryClassLoader().define(ClassLoaders.class.getCanonicalName(), bytes, null);
			fail();
		} catch(NoClassDefFoundError e) {}
	}


	@Test
	public void canDefineAClassFromSubsectionOfArray()
	{
		byte[] section = Resources.classAsBytes(InMemoryClassLoaderTest.class);

		byte[] bytes = new byte[10 + section.length + 20];

		System.arraycopy(section, 0, bytes, 10, section.length);

		Class<?> loaded = inMemoryClassLoader().define(InMemoryClassLoaderTest.class.getCanonicalName(), bytes, 10, section.length, null);

		assertThat(loaded.getCanonicalName(), is(equalTo(InMemoryClassLoaderTest.class.getCanonicalName())));
		assertThat(loaded, is(not(equalTo(InMemoryClassLoaderTest.class))));
	}


	@Test
	public void asSystemClassLoaderCanLoadSystemClass()
	{
		asSystemClassLoaderCanLoad(String.class.getCanonicalName());
	}


	private void asSystemClassLoaderCanLoad(String canonicalName)
	{
		String property = System.getProperty("java.system.class.loader");
		assumeTrue(InMemoryClassLoader.class.getCanonicalName().equals(property));

		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		assertThat(systemClassLoader, is(instanceOf(InMemoryClassLoader.class)));

		Class<?> loaded = Exceptional.apply(systemClassLoader::loadClass, canonicalName);

		assertThat(loaded.getCanonicalName(), is(equalTo(canonicalName)));
	}


	@Test
	public void asSystemClassLoaderCanLoadApplicationClass()
	{
		asSystemClassLoaderCanLoad(InMemoryClassLoaderTest.class.getCanonicalName());
	}


	@Test
	public void loadedClassesHaveAssociatedCodeSignerWhenSignatureFileDetected() throws ClassNotFoundException
	{
		byte[] signed = Signatures.sign("Any old rubbish here, for now".getBytes(UTF_8), OPENED_KEYSTORE, "SHA256withRSA");

		byte[] fakeSignedJar = createFakeSignedJar(InMemoryClassLoaderTest.class, "META-INF/TEST-SIGNATURE", signed);

		try(InMemoryClassLoader loader = inMemoryClassLoader()
				.jar(fakeSignedJar)) {
			Class<?> loaded = loader.loadClass(InMemoryClassLoaderTest.class.getCanonicalName());
			Object[] signers = loaded.getSigners();

			assertThat(signers, is(not(nullValue())));
			assertThat(signers, is(not(emptyArray())));

			assertThat(Arrays.toString(signers), hasToString(allOf(
					containsString("CN=acme"),
					containsString("CN=subject"),
					containsStringIgnoringCase("Signature Algorithm: SHA256withRSA"))));
		}

	}


	private byte[] createFakeSignedJar(Class<?> type, String filePrefix, byte[] fileContent)
	{
		return Exceptional.get(() -> readAllBytes(bundle()
				.add(type)
				.add(filePrefix + ".SF", new ByteArrayInputStream("not currently processed".getBytes(UTF_8)))
				.add(filePrefix + ".RSA", new ByteArrayInputStream(fileContent))
				.build()));
	}


	@Test
	public void loadedClassesDoNotHaveAssociatedCodeSignerWhenSignatureDetectionDisabled() throws ClassNotFoundException
	{
		byte[] signed = Signatures.sign("Any old rubbish here, for now".getBytes(UTF_8), OPENED_KEYSTORE, "SHA256withRSA");

		byte[] fakeSignedJar = createFakeSignedJar(InMemoryClassLoaderTest.class, "META-INF/TEST-SIGNATURE", signed);

		try(InMemoryClassLoader loader = inMemoryClassLoader()
				.doNotAddSubsequentSignatures()
				.jar(fakeSignedJar)) {
			Class<?> loaded = loader.loadClass(InMemoryClassLoaderTest.class.getCanonicalName());
			Object[] signers = loaded.getSigners();

			assertThat(signers, is(nullValue()));
		}
	}

	@Nested
	public class MultiRelease {

		@Test
		public void multiRelease() throws ReflectiveOperationException
		{
			byte[] mrJar = Exceptional.get(() -> readAllBytes(bundle()
					.add(PATH_WHICH, whichVersion(8, 8))
					.add(MR_PREFIX + "9/" + PATH_WHICH, whichVersion(9, 9))
					.add(MR_PREFIX + "10/" + PATH_WHICH, whichVersion(10, 10))
					.add(MR_PREFIX + "11/" + PATH_WHICH, whichVersion(11, 11))
					.add(MR_PREFIX + "12/" + PATH_WHICH, whichVersion(12, 12))
					.build()));

			int expectedVersion = Math.min(12, SourceVersion.latest().ordinal());

			try(InMemoryClassLoader loader = ClassLoaders.inMemoryClassLoader().jar(mrJar).forceMultiReleaseVersion(SourceVersion.latest())) {
				Class<?> loaded = loader.loadClass(CLASS_WHICH);

				Method method = Methods.getMethod(loaded, "javaVersion").orElseThrow(NullPointerException::new);

				Object response = method.invoke(null);

				assertThat(response, is(expectedVersion));
			}
		}


		@Test
		public void forceMultiReleaseVersion() throws ReflectiveOperationException
		{
			byte[] mrJar = Exceptional.get(() -> readAllBytes(bundle()
					.add(PATH_WHICH, whichVersion(8, 8))
					.add(MR_PREFIX + "9/" + PATH_WHICH, whichVersion(9, 9))
					.add(MR_PREFIX + "4096/" + PATH_WHICH, whichVersion(8, 42))
					.build()));

			try(InMemoryClassLoader loader = ClassLoaders.inMemoryClassLoader().jar(mrJar).forceMultiReleaseVersion(4096)) {
				Class<?> loaded = loader.loadClass(CLASS_WHICH);

				Method method = Methods.getMethod(loaded, "javaVersion").orElseThrow(NullPointerException::new);

				Object response = method.invoke(null);

				assertThat(response, is(42));
			}
		}


		private InputStream whichVersion(int compileVersion, int reportVersion) throws Exception
		{
			return new ByteArrayInputStream(whichVersionByteCode(compileVersion, reportVersion));
		}


		// @formatter:off
		
		private byte[] whichVersionByteCode(int compileVersion, int reportVersion) throws Exception
		{
			ClassWriter cw = new ClassWriter(0);
			MethodVisitor mv;

			cw.visit(classVersion(compileVersion), ACC_PUBLIC + ACC_FINAL + ACC_SUPER, BINARY_WHICH, null, "java/lang/Object", null);

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
				mv.visitLocalVariable("this", "L" + BINARY_WHICH + ";", null, l0, l1, 0);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
			
			{
				mv = cw.visitMethod(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "javaVersion", "()I", null, null);
				mv.visitCode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitLineNumber(8, l0);
				mv.visitIntInsn(BIPUSH, reportVersion);
				mv.visitInsn(IRETURN);
				mv.visitMaxs(1, 0);
				mv.visitEnd();
			}
			cw.visitEnd();

			return cw.toByteArray();
		}
		// @formatter:on


		private int classVersion(int version)
		{
			int classVersion = 0 << 16 | (44 + version);
			return classVersion;
		}
	}
}
