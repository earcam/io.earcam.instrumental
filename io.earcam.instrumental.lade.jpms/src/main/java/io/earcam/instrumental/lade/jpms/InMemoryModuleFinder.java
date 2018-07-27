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
import java.io.InputStream;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.earcam.instrumental.lade.InMemoryClassLoader;
import io.earcam.unexceptional.CheckedFunction;
import io.earcam.unexceptional.Closing;
import io.earcam.unexceptional.Exceptional;

public final class InMemoryModuleFinder implements ModuleFinder {

	private static final String MODULE_INFO_CLASS = "module-info.class";
	private final InMemoryClassLoader loader;


	public InMemoryModuleFinder(InMemoryClassLoader loader)
	{
		this.loader = loader;
	}


	@Override
	public Optional<ModuleReference> find(String name)
	{
		return findAll().stream()
				.filter(m -> name.equals(m.descriptor().name()))
				.findAny();
	}


	@Override
	public Set<ModuleReference> findAll()
	{
		Enumeration<URL> resources = loader.getResources(MODULE_INFO_CLASS, false);

		Set<ModuleReference> references = new HashSet<>();
		while(resources.hasMoreElements()) {
			URL moduleInfo = resources.nextElement();
			ModuleDescriptor descriptor = parseModuleInfoByteCode(moduleInfo);
			URI uri = extractBaseUri(moduleInfo);
			Set<String> list = resourcesForThisModule(uri);
			references.add(new InMemoryModuleReference(descriptor, uri, list));

		}
		return references;
	}


	private Set<String> resourcesForThisModule(URI baseUri)
	{
		return loader.resources()
				.map(Object::toString)
				.filter(r -> r.startsWith(baseUri.toString()))
				.collect(Collectors.toSet());
	}


	private URI extractBaseUri(URL moduleInfo)
	{
		String url = moduleInfo.toString();
		url = url.substring(0, url.length() - MODULE_INFO_CLASS.length());
		return Exceptional.uri(url);
	}


	private ModuleDescriptor parseModuleInfoByteCode(URL moduleInfo)
	{
		CheckedFunction<InputStream, ModuleDescriptor, IOException> converter = ModuleDescriptor::read;
		return Closing.closeAfterApplying(
				URL::openStream,
				moduleInfo,
				converter);
	}
}