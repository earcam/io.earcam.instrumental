/*-
 * #%L
 * io.earcam.instrumental.module.osgi
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
package io.earcam.instrumental.module.osgi.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ParserValidityMatcher extends TypeSafeMatcher<ParserRuleContext> {

	private boolean shouldSucceed;


	private ParserValidityMatcher(boolean shouldSucceed)
	{
		this.shouldSucceed = shouldSucceed;
	}


	public static ParserValidityMatcher valid()
	{
		return new ParserValidityMatcher(true);
	}


	public static ParserValidityMatcher invalid()
	{
		return new ParserValidityMatcher(false);
	}


	@Override
	public void describeTo(Description description)
	{
		if(shouldSucceed) {
			description.appendText("expected success");
		} else {
			description.appendText("expected failure");
		}
	}


	@Override
	protected void describeMismatchSafely(ParserRuleContext context, Description mismatchDescription)
	{
		super.describeMismatchSafely(context, mismatchDescription);
		mismatchDescription.appendValue(context.exception);
		// TODO invoke the TestRig to get a good description of failure....
	}


	@Override
	protected boolean matchesSafely(ParserRuleContext context)
	{
		return (context.exception == null) == shouldSucceed;
	}

}
