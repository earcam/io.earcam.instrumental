/*-
 * #%L
 * io.earcam.instrumental.lade.jpms
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
package io.earcam.acme.app;

import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import io.earcam.acme.api.AcmeApi;

public class AcmeApp {

	public static void main(String[] args)
	{
		Module module = AcmeApp.class.getModule();
		AcmeApi implementation = loadService(module);

		String msg = implementation.greet(args[0]);

		System.out.println(module + ": " + msg);
	}


	private static AcmeApi loadService(Module module)
	{
		ServiceLoader<AcmeApi> services = loadServiceLoader(module);

		return StreamSupport
				.stream(services.spliterator(), false)
				.findFirst()
				.orElseThrow(NullPointerException::new);
	}


	private static ServiceLoader<AcmeApi> loadServiceLoader(Module module)
	{
		ModuleLayer layer = module.getLayer();

		ServiceLoader<AcmeApi> services = (layer == null) ? ServiceLoader.load(AcmeApi.class) : ServiceLoader.load(layer, AcmeApi.class);
		return services;
	}
}
