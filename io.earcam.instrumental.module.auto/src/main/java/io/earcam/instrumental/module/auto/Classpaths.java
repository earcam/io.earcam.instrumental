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
 * Classpaths class.
 * </p>
 *
 */
public final class Classpaths {

	/** Constant <code>PROPERTY_CLASS_PATH="java.class.path"</code> */
	public static final String PROPERTY_CLASS_PATH = "java.class.path";
	/** Constant <code>PROPERTY_MODULE_PATH="jdk.module.path"</code> */
	public static final String PROPERTY_MODULE_PATH = "jdk.module.path";
	/** Constant <code>PROPERTY_MODULE_UPGRADE_PATH="jdk.module.path"</code> */
	public static final String PROPERTY_MODULE_UPGRADE_PATH = "jdk.module.path";


	private Classpaths()
	{}


	/**
	 * <p>
	 * allClasspaths.
	 * </p>
	 *
	 * @return a {@link java.util.stream.Stream} object.
	 */
	public static Stream<Path> allClasspaths()
	{
		return concat(paths(PROPERTY_CLASS_PATH), concat(paths(PROPERTY_MODULE_PATH), paths(PROPERTY_MODULE_UPGRADE_PATH)));
	}


	/**
	 * <p>
	 * paths.
	 * </p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @return a {@link java.util.stream.Stream} object.
	 */
	public static Stream<Path> paths(String property)
	{
		String value = System.getProperty(property);
		return value == null ? Stream.empty() : Arrays.stream(value.split(pathSeparator)).map(Paths::get);
	}

}
