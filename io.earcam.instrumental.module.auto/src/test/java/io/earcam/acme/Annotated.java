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
package io.earcam.acme;

import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.Objects;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsAnything;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.signature.SignatureReader;

import io.earcam.utilitarian.charstar.CharSequences;

@AcmeAnnotation(CharSequences.class)
public class Annotated implements Comparable<@AcmeAnnotation(Test.class) Annotated> {

	@AcmeAnnotation(Label.class)
	private int num = 42;


	@AcmeAnnotation(SignatureReader.class)
	public int getNum() throws @AcmeAnnotation(IllegalStateException.class) OutOfMemoryError
	{
		@AcmeAnnotation(Matchers.class)
		int numnum = num;
		return new @AcmeAnnotation(IsAnything.class) Integer(numnum).intValue();
	}


	@Override
	public int compareTo(@AcmeAnnotation(Comparator.class) Annotated o)
	{
		return 0;
	}


	public static <T extends Comparable<T>> boolean isNaNaNullALul(T instance, Class<@AcmeAnnotation(Objects.class) T> type)
	{
		try {
			return instance == null;
		} catch(@AcmeAnnotation(UncheckedIOException.class) Throwable t) {
			return true;
		}
	}
}
