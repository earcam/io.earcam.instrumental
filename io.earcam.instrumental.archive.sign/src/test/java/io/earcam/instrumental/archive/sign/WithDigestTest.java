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

import static io.earcam.instrumental.archive.Archive.archive;
import static io.earcam.instrumental.archive.sign.StandardDigestAlgorithms.MD5;
import static io.earcam.instrumental.archive.sign.WithDigest.base64Digest;
import static io.earcam.instrumental.archive.sign.WithDigest.withDigest;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.security.MessageDigest;
import java.util.jar.Attributes;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.archive.Archive;
import io.earcam.unexceptional.Exceptional;

public class WithDigestTest {

	@Test
	void addsDigestAsManifestNamedEntry()
	{
		String filename = "hello.txt";
		byte[] contents = "world".getBytes(UTF_8);

		Archive archive = archive()
				.configured(
						withDigest().digestedBy(MD5))
				.with(filename, contents)
				.toObjectModel();

		Attributes attributes = archive.manifest().get().getAttributes(filename);

		String expected = base64Digest(Exceptional.apply(MessageDigest::getInstance, "MD5"), contents);

		assertThat(attributes, hasEntry(new Attributes.Name("MD5-Digest"), expected));
	}
}
