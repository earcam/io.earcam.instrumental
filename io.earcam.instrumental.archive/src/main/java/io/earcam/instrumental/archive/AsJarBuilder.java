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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.Manifest;

public interface AsJarBuilder<T extends AsJarBuilder<T>> extends ArchiveConfigurationPlugin {

	public abstract T launching(Class<?> mainClass);


	public abstract T launching(String mainClass);


	public abstract T sealing(Predicate<String> packageMatcher);


	/**
	 * <p>
	 * Adds SPI details under {@value #SPI_ROOT_PATH}
	 * </p>
	 * <p>
	 * Note the {@code service} parameter is <b>not</b> added to the archive.
	 * </p>
	 *
	 * @param service the service implemented
	 * @param implementations concrete implementations added to Archive and listed under SPI.
	 * @return this builder.
	 */
	public default T providing(Class<?> service, Class<?>... implementations)
	{
		return providing(service, Arrays.asList(implementations));
	}


	public abstract T providing(Class<?> service, List<Class<?>> implementations);


	public abstract T providing(String service, Set<String> implementations);


	public abstract T mergingManifest(Manifest manifest);


	public abstract T withManifestHeader(String key, String value);
}
