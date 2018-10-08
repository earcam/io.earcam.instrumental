/*-
 * #%L
 * io.earcam.instrumental.module.auto
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
package io.earcam.instrumental.module.auto;

import static java.io.File.pathSeparator;
import static java.util.stream.Stream.concat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * <p>
 * Paths from Classpaths.
 * </p>
 *
 */
public final class Classpaths {

	public static final String PROPERTY_CLASS_PATH = "java.class.path";
	public static final String PROPERTY_MODULE_PATH = "jdk.module.path";
	public static final String PROPERTY_MODULE_UPGRADE_PATH = "jdk.module.upgrade.path";


	private Classpaths()
	{}


	/**
	 * <p>
	 * allClasspaths.
	 * </p>
	 *
	 * @return a {@link java.util.stream.Stream} object.
	 * 
	 * @see #PROPERTY_CLASS_PATH
	 * @see #PROPERTY_MODULE_PATH
	 * @see #PROPERTY_MODULE_UPGRADE_PATH
	 */
	public static Stream<Path> allClasspaths()
	{
		return concat(paths(PROPERTY_CLASS_PATH), concat(paths(PROPERTY_MODULE_PATH), paths(PROPERTY_MODULE_UPGRADE_PATH)));
	}


	/**
	 * <p>
	 * Paths for a given classpath {@code property}.
	 * </p>
	 *
	 * @param property the property name for given classpath
	 * @return a {@link java.util.stream.Stream} of the paths defined by the classpath property
	 * 
	 * @see #PROPERTY_CLASS_PATH
	 * @see #PROPERTY_MODULE_PATH
	 * @see #PROPERTY_MODULE_UPGRADE_PATH
	 */
	public static Stream<Path> paths(String property)
	{
		String value = System.getProperty(property);
		return value == null ? Stream.empty() : Arrays.stream(value.split(pathSeparator)).map(Paths::get);
	}

}
