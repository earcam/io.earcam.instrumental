/*-
 * #%L
 * io.earcam.instrumental.agent
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
package io.earcam.instrumental.agent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.agent.InstrumentationServiceProvider;

public class InstrumentationServiceProviderTest {

	@Test
	public void availableViaSpi()
	{
		List<Instrumentation> provided = new ArrayList<>();

		ServiceLoader.load(Instrumentation.class)
				.forEach(provided::add);

		assertThat(provided, contains(instanceOf(InstrumentationServiceProvider.class)));
	}


	@Test
	public void delegateSet()
	{
		// EARCAM_SNIPPET_BEGIN: instrumentation-spi
		Instrumentation instrumentation = ServiceLoader.load(Instrumentation.class)
				.iterator().next();
		// EARCAM_SNIPPET_END: instrumentation-spi

		assertThat(instrumentation.getAllLoadedClasses(), is(not(emptyArray())));
	}
}
