/*-
 * #%L
 * io.earcam.instrumental.archive
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
package io.earcam.instrumental.archive;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import io.earcam.instrumental.reflect.Names;
import io.earcam.instrumental.reflect.Resources;
import io.earcam.instrumental.reflect.Types;

/**
 * <p>
 * BasicArchiveResourceSource class.
 * </p>
 *
 */
public class BasicArchiveResourceSource implements ArchiveResourceSource {

	private final Set<ArchiveResource> resources = new HashSet<>();


	/** {@inheritDoc} */
	@Override
	public Stream<ArchiveResource> drain(ResourceSourceLifecycle stage)
	{
		HashSet<ArchiveResource> swamp = new HashSet<>(resources);
		resources.clear();
		return swamp.stream();
	}


	/**
	 * <p>
	 * with.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 */
	public void with(Class<?> type)
	{
		resources.add(typeToArchiveResource(type));

		Names.declaredBinaryNamesOf(type)
				.map(n -> Types.getClass(n, type.getClassLoader()))
				.map(BasicArchiveResourceSource::typeToArchiveResource)
				.forEach(resources::add);
	}


	private static ArchiveResource typeToArchiveResource(Class<?> t)
	{
		return new ArchiveResource(Names.typeToResourceName(t), Resources.classAsBytes(t));
	}


	/**
	 * <p>
	 * with.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param resource a {@link java.io.InputStream} object.
	 */
	public void with(String name, InputStream resource)
	{
		resources.add(new ArchiveResource(name, resource));
	}


	/**
	 * <p>
	 * with.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param contents an array of {@link byte} objects.
	 */
	public void with(String name, byte[] contents)
	{
		resources.add(new ArchiveResource(name, contents));
	}
}
