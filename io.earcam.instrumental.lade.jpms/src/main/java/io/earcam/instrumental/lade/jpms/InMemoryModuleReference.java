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
package io.earcam.instrumental.lade.jpms;

import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import io.earcam.unexceptional.Exceptional;

final class InMemoryModuleReference extends ModuleReference {

	private class InMemoryModuleReader implements ModuleReader {
		@Override
		public Stream<String> list() throws IOException
		{
			return list.stream();
		}


		@Override
		public Optional<URI> find(String name) throws IOException
		{
			return list()
					.filter(r -> r.startsWith(location().get().toString()))
					.map(Exceptional::uri)
					.filter(r -> r.toString().endsWith(name))
					.findAny();
		}


		@Override
		public void close()
		{
			/* NoOp */
		}
	}

	private final Set<String> list;


	InMemoryModuleReference(ModuleDescriptor descriptor, URI location, Set<String> list)
	{
		super(descriptor, location);
		this.list = list;
	}


	@Override
	public ModuleReader open() throws IOException
	{
		return new InMemoryModuleReader();
	}
}