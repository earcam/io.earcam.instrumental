/*-
 * #%L
 * io.earcam.instrumental.reflect
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
package io.earcam.instrumental.reflect;

import static java.util.stream.Collectors.toList;

import java.util.Optional;
import java.util.stream.Stream;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class Matchers {

	public static <T> Matcher<Optional<T>> isPresent()
	{
		return new BaseMatcher<Optional<T>>() {

			@Override
			public boolean matches(Object item)
			{
				return ((Optional<?>) item).isPresent();
			}


			@Override
			public void describeTo(Description description)
			{
				description.appendText(" Optional.present");
			}
		};
	}


	public static <T> Matcher<Optional<T>> isAbsent()
	{
		return new BaseMatcher<Optional<T>>() {

			@Override
			public boolean matches(Object item)
			{
				return !((Optional<?>) item).isPresent();
			}


			@Override
			public void describeTo(Description description)
			{
				description.appendText(" Optional.empty");
			}
		};
	}


	public static <T> Matcher<Stream<T>> aStream(Matcher<Iterable<? super T>> matcher)
	{
		return new BaseMatcher<Stream<T>>() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean matches(Object item)
			{
				return matcher.matches(((Stream<T>) item).collect(toList()));
			}


			@Override
			public void describeTo(Description description)
			{
				description.appendText("a stream");
				matcher.describeTo(description);
			}
		};
	}
}
