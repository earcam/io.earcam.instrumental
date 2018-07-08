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

import java.util.List;
import java.util.Optional;
import java.util.jar.Manifest;

import io.earcam.instrumental.fluent.Fluent;

/**
 * <p>
 * Archive interface.
 * </p>
 *
 */
public interface Archive extends ArchiveTransform {

	/**
	 * <p>
	 * archive.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.ArchiveConfiguration} object.
	 */
	@Fluent
	public static ArchiveConfiguration archive()
	{
		return new ArchiveBuilder();
	}


	/**
	 * <p>
	 * manifest.
	 * </p>
	 *
	 * @return a {@link java.util.Optional} object.
	 */
	public abstract Optional<Manifest> manifest();


	/**
	 * <p>
	 * contents.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public abstract List<ArchiveResource> contents();


	/**
	 * <p>
	 * content.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link java.util.Optional} object.
	 */
	public default Optional<ArchiveResource> content(String name)
	{
		return contents().stream()
				.filter(r -> r.sameName(name))
				.findAny();
	}
}
