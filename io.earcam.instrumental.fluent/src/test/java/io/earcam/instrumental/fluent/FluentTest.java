/*-
 * #%L
 * io.earcam.instrumental.fluent
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
package io.earcam.instrumental.fluent;

import static io.earcam.instrumental.fluent.Role.AUX;
import static io.earcam.instrumental.reflect.Methods.getMethod;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

public class FluentTest {

	@Fluent(role = AUX)
	public static int apiMethod(String x)
	{
		return 42;
	}


	@Test
	void retentionIsRuntime()
	{
		Method method = getMethod(FluentTest.class, "apiMethod", String.class)
				.orElseThrow(AssertionError::new);

		Fluent annotation = method.getAnnotation(Fluent.class);
		assertThat(annotation, is(not(nullValue())));

		assertThat(annotation.role(), is(AUX));
	}
}
