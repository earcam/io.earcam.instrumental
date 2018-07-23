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

import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import io.earcam.instrumental.archive.ArchiveConfigurationPlugin;
import io.earcam.instrumental.archive.ArchiveRegistrar;
import io.earcam.instrumental.archive.ArchiveResource;
import io.earcam.instrumental.archive.ArchiveResourceListener;
import io.earcam.instrumental.archive.ManifestProcessor;

/**
 * <p>
 * Adds the archive's contents as digests in the manifest.
 * </p>
 *
 */
public class WithDigest implements ArchiveConfigurationPlugin, ManifestProcessor, ArchiveResourceListener {

	protected MessageDigest digester;
	private final Map<String, String> digests = new HashMap<>();
	protected Manifest manifest;


	WithDigest()
	{}


	/**
	 * <p>
	 * withDigest.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.sign.WithDigest} object.
	 */
	public static WithDigest withDigest()
	{
		return new WithDigest();
	}


	@Override
	public void attach(ArchiveRegistrar core)
	{
		core.registerManifestProcessor(this);
		core.registerResourceListener(this);
	}


	@Override
	public void added(ArchiveResource resource)
	{
		digests.put(resource.name(), base64Digest(resource.bytes()));
	}


	/**
	 * <p>
	 * base64Digest.
	 * </p>
	 *
	 * @param data an array of {@link byte} objects.
	 * @return a {@link java.lang.String} object.
	 */
	protected String base64Digest(byte[] data)
	{
		return base64Digest(digester, data);
	}


	static String base64Digest(MessageDigest digester, byte[] data)
	{
		digester.reset();
		return encodeBase64(digester.digest(data));
	}


	static String encodeBase64(byte[] data)
	{
		return Base64.getEncoder().encodeToString(data);
	}


	@Override
	public void process(Manifest manifest)
	{
		this.manifest = manifest;

		Map<String, Attributes> entries = manifest.getEntries();

		for(Entry<String, String> e : digests.entrySet()) {
			Attributes attributes = entries.computeIfAbsent(e.getKey(), k -> new Attributes());
			attributes.putValue(digester.getAlgorithm() + "-Digest", e.getValue());
		}
	}


	/* WithDigest API */

	/**
	 * Adds digest entries to the manifest. Required for signed JARs.
	 *
	 * @param hash the algorithm name
	 * @return this archive builder
	 */
	public WithDigest digestedBy(StandardDigestAlgorithms hash)
	{
		return digestedBy(hash.create());
	}


	/**
	 * Adds digest entries to the manifest. Required for signed JARs.
	 *
	 * @param digest the hash algorithm
	 * @return this archive builder
	 */
	public WithDigest digestedBy(MessageDigest digest)
	{
		this.digester = digest;
		return this;
	}

}
