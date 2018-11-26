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

import static io.earcam.instrumental.archive.AbstractAsJarBuilder.CREATED_BY;
import static io.earcam.instrumental.archive.AbstractAsJarBuilder.V1_0;
import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.FINAL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.jar.Attributes.Name.SIGNATURE_VERSION;

import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import io.earcam.instrumental.archive.ArchiveConfigurationPlugin;
import io.earcam.instrumental.archive.ArchiveRegistrar;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.archive.ArchiveResourceListener;
import io.earcam.instrumental.archive.ArchiveResourceSource;
import io.earcam.instrumental.archive.ManifestProcessor;
import io.earcam.instrumental.fluent.Fluent;
import io.earcam.unexceptional.Closing;
import io.earcam.utilitarian.security.OpenedKeyStore;
import io.earcam.utilitarian.security.Signatures;

/**
 * <p>
 * RSA Jar signing archive extension.
 * </p>
 */
public class WithSignature extends WithDigest implements ArchiveConfigurationPlugin, ManifestProcessor, ArchiveResourceSource, ArchiveResourceListener {

	private static final String META_INF = "META-INF/";

	private static final String DEFAULT_SIGNATURE_FILENAME = "SIGNATURE";
	private static final String DEFAULT_SIGNATURE_ALGORITHM = "SHA512withRSA";

	private KeyStore keyStore;
	private String keyAlias;
	private char[] keyPassword;
	private String signatureFilename = DEFAULT_SIGNATURE_FILENAME;
	private String signatureAlgorithm = DEFAULT_SIGNATURE_ALGORITHM;
	private Manifest signatures = new Manifest();
	private String createdBy = WithSignature.class.getPackage().getName();


	WithSignature()
	{}


	/**
	 * <p>
	 * Create new {@link WithSignature}.
	 * </p>
	 *
	 * @return a new {@link WithSignature} archive extension.
	 */
	@Fluent
	public static WithSignature withSignature()
	{
		return new WithSignature();
	}


	@Override
	public void added(ArchiveResource resource)
	{
		if(!resource.name().startsWith(META_INF + signatureFilename + '.')) {
			super.added(resource);
		}
	}


	@Override
	public Stream<ArchiveResource> drain(ResourceSourceLifecycle stage)
	{
		if(stage == FINAL) {
			byte[] sign = sign();
			ArchiveResource sf = new ArchiveResource(META_INF + signatureFilename + ".SF", sign);
			OpenedKeyStore openedKeyStore = new OpenedKeyStore(keyStore, keyAlias, keyPassword);
			ArchiveResource cert = new ArchiveResource(META_INF + signatureFilename + ".RSA", Signatures.sign(sign, openedKeyStore, signatureAlgorithm));
			return Stream.of(sf, cert);
		}
		return Stream.empty();
	}


	private byte[] sign()
	{
		Attributes mainAttributes = signatures.getMainAttributes();
		mainAttributes.put(SIGNATURE_VERSION, V1_0);
		mainAttributes.put(new Name(CREATED_BY), createdBy);
		mainAttributes.put(new Name(digester.getAlgorithm() + "-Digest-Manifest"), manifestDigest());

		mainAttributes.put(new Name(digester.getAlgorithm() + "-Digest-Manifest-Main-Attributes"), manifestMainDigest());

		manifest.getEntries().keySet().forEach(this::addSigned);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Closing.closeAfterAccepting(out, signatures::write);
		return out.toByteArray();
	}


	private String manifestDigest()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Closing.closeAfterAccepting(out, manifest::write);
		return base64Digest(out.toByteArray());
	}


	private String manifestMainDigest()
	{
		StringBuilder main = new StringBuilder();
		attributesAsString(manifest.getMainAttributes(), main);
		return base64Digest(newline(main).toString().getBytes(UTF_8));
	}


	private void addSigned(String resourceName)
	{
		Attributes signature = new Attributes();
		signatures.getEntries().put(resourceName, signature);

		Attributes attributes = manifest.getAttributes(resourceName);

		StringBuilder sig = make72Safe(newline(new StringBuilder("Name: ").append(resourceName)));
		attributesAsString(attributes, sig);

		String digested = base64Digest(sig.toString().getBytes(UTF_8));
		signature.putValue(digester.getAlgorithm() + "-Digest", digested);
	}


	private static final StringBuilder newline(StringBuilder string)
	{
		return string.append("\r\n");
	}


	private void attributesAsString(Attributes attributes, StringBuilder string)
	{
		attributes.forEach((k, v) -> string.append(make72Safe(newline(new StringBuilder(k.toString()).append(": ").append(v)))));
		newline(string);
	}


	private static StringBuilder make72Safe(StringBuilder line)
	{
		int length = line.length();
		if(length > 72) {
			int i = 70;
			while(i < length - 2) {
				line.insert(i, "\r\n ");
				i += 72;
				length += 3;
			}
		}
		return line;
	}


	@Override
	public void attach(ArchiveRegistrar core)
	{
		super.attach(core);
		core.registerResourceSource(this);
	}


	/* WithDigest API */

	@Override
	public WithSignature digestedBy(StandardDigestAlgorithms hash)
	{
		super.digestedBy(hash);
		return this;
	}


	@Override
	public WithSignature digestedBy(MessageDigest digest)
	{
		super.digestedBy(digest);
		return this;
	}


	/* WithSignature API */

	/**
	 * The store containing the key and cert(s)
	 *
	 * @param store the key store
	 * @return this builder.
	 */
	public WithSignature store(KeyStore store)
	{
		this.keyStore = store;
		return this;
	}


	/**
	 * The alias for the key in store
	 *
	 * @param alias by which the key shall be known.
	 * @return this builder.
	 */
	public WithSignature alias(String alias)
	{
		this.keyAlias = alias;
		return this;
	}


	/**
	 * The password for the key in store.
	 *
	 * @param password the alias password to use for the keystore.
	 * @return this builder.
	 */
	public WithSignature password(char[] password)
	{
		this.keyPassword = password;
		return this;
	}


	/**
	 * <p>
	 * The filename <b>without</b> suffix.
	 * </p>
	 * 
	 * <p>
	 * Defaults to {@value #DEFAULT_SIGNATURE_FILENAME} if not set.
	 * </p>
	 *
	 * @param signatureFilename the filename.
	 * @return this builder.
	 */
	public WithSignature signatureFileName(String signatureFilename)
	{
		this.signatureFilename = signatureFilename;
		return this;
	}


	/**
	 * <p>
	 * signatureAlgorithm.
	 * </p>
	 *
	 * @param algorithm the signature algorithm.
	 * @return this builder.
	 */
	public WithSignature signatureAlgorithm(StandardSignatureAlgorithms algorithm)
	{
		return signatureAlgorithm(algorithm.algorithm());
	}


	/**
	 * <p>
	 * signatureAlgorithm.
	 * </p>
	 *
	 * @param algorithm the signature algorithm.
	 * @return this builder.
	 * 
	 * @see #signatureAlgorithm(StandardSignatureAlgorithms)
	 */
	public WithSignature signatureAlgorithm(String algorithm)
	{
		this.signatureAlgorithm = algorithm;
		return this;
	}


	/**
	 * <p>
	 * createdBy.
	 * </p>
	 *
	 * @param author the signature author.
	 * @return this builder.
	 */
	public WithSignature createdBy(String author)
	{
		this.createdBy = author;
		return this;
	}

}
