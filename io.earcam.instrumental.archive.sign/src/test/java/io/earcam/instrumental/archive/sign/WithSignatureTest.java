/*-
 * #%L
 * io.earcam.instrumental.archive.sign
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
package io.earcam.instrumental.archive.sign;

//EARCAM_SNIPPET_BEGIN: imports
import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.AsJar.asJar;
import static io.earcam.instrumental.archive.sign.StandardDigestAlgorithms.*;
import static io.earcam.instrumental.archive.sign.StandardSignatureAlgorithms.*;
import static io.earcam.instrumental.archive.sign.WithDigest.withDigest;
import static io.earcam.instrumental.archive.sign.WithSignature.withSignature;
import static io.earcam.utilitarian.security.Certificates.certificate;
import static io.earcam.utilitarian.security.KeyStores.keyStore;
//EARCAM_SNIPPET_END: imports
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.earcam.acme.AmSigned;
import io.earcam.acme.in.intentionally.very.lengthy.packagename.safe72.ok.AmAlsoSigned;
import io.earcam.instrumental.lade.ClassLoaders;
import io.earcam.utilitarian.security.Keys;

/**
 * /Library/Java/JavaVirtualMachines/jdk1.8.0_171.jdk/Contents/Home/bin/jarsigner -J-Djava.security.debug=jar
 * -verbose:all -certs -sigalg SHA256withRSA -sigfile SIGNATURE -keystore /tmp/foo.ks -verify /tmp/foo-signed.jar
 *
 * /Library/Java/JavaVirtualMachines/jdk1.8.0_171.jdk/Contents/Home/bin/jarsigner -verbose:all -signedjar
 * /tmp/foo-signed.jar -sigalg SHA256withRSA -sigfile SIG -keystore /tmp/foo.ks /tmp/foo-unsigned.jar alias
 * 
 * openssl asn1parse -in ./META-INF/SIGNATURE.RSA -inform der -i
 * 
 */
public class WithSignatureTest {

	private static final Path TEST_DIR = Paths.get(".", "target", "test", UUID.randomUUID().toString());

	// EARCAM_SNIPPET_BEGIN: fields
	private final KeyPair keys = Keys.rsa();
	private final String alias = "alias";
	private final char[] password = "password".toCharArray();
	private final String SUBJECT = "subject";
	private final KeyStore keyStore = keyStore(alias, password, keys, certificate(keys, SUBJECT).toX509());
	// EARCAM_SNIPPET_END: fields


	@BeforeAll
	public static void initialize()
	{
		TEST_DIR.toFile().mkdirs();
	}


	@Test
	public void amSigned() throws Exception
	{
		Path file = TEST_DIR.resolve("am-signed.jar");

		createSignedJar(file);

		try(URLClassLoader loader = ClassLoaders.selfFirstClassLoader(file)) {
			Class<?> loaded = loader.loadClass(cn(AmAlsoSigned.class));

			Object[] signers = loaded.getSigners();

			assertThat(signers, is(not(nullValue())));
			assertThat(signers, is(not(emptyArray())));

			assertThat(Arrays.toString(signers), hasToString(allOf(
					containsString("Subject: CN=subject"),
					containsString("Signature Algorithm: SHA256withRSA"))));
		}
	}


	// EARCAM_SNIPPET_BEGIN: method
	private void createSignedJar(Path file)
	{
		archive()
				.configured(asJar())
				.configured(withSignature()
						.digestedBy(SHA512)
						.store(keyStore)
						.alias(alias)
						.password(password)
						.signatureAlgorithm(SHA256_WITH_RSA)
						.signatureFileName("SIG")
						.createdBy("necessity"))
				.with(AmSigned.class)
				.with(AmAlsoSigned.class)
				.to(file);
	}
	// EARCAM_SNIPPET_END: method


	private static String cn(Class<?> type)
	{
		return type.getTypeName();
	}


	@Test
	public void amNotSigned() throws Exception
	{
		Path file = TEST_DIR.resolve("am-not-signed.jar");
		archive()
				.configured(withDigest()
						.digestedBy(SHA512))
				.with(AmSigned.class)
				.with(AmAlsoSigned.class)
				.to(file);

		try(URLClassLoader loader = ClassLoaders.selfFirstClassLoader(file)) {
			Class<?> loaded = loader.loadClass(cn(AmAlsoSigned.class));

			assertThat(loaded.getSigners(), is(nullValue()));
		}
	}
}
