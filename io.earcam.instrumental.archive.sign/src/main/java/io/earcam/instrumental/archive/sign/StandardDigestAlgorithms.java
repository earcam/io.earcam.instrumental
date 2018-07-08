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

import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * Minimum supported, see § Algorithms in
 * https://docs.oracle.com/javase/8/docs/technotes/guides/jar/jar.html#Notes_on_Manifest_and_Signature_Files
 * </p>
 */
public enum StandardDigestAlgorithms {

	// @formatter:off
	/**
	 * Note: MD5 is slated for removal, JDK10 won't consider JAR as signed when using this as a Digest Algorithm
	 */
	MD5("MD5"), 
	SHA1("SHA1"), 
	SHA224("SHA-224"), 
	SHA256("SHA-256"), 
	SHA384("SHA-384"), 
	SHA512("SHA-512"),
	;
	// @formatter:on

	private final String algorithm;


	StandardDigestAlgorithms(String algorithm)
	{
		this.algorithm = algorithm;
	}


	/**
	 * <p>
	 * algorithm.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String algorithm()
	{
		return algorithm;
	}


	/**
	 * @return a new MessageDigest instance for the given algorithm
	 * @throws io.earcam.unexceptional.UncheckedSecurityException if no such algorithm is available
	 */
	public MessageDigest create()
	{
		return Exceptional.apply(MessageDigest::getInstance, algorithm);
	}
}
