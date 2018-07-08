/*-
 * #%L
 * io.earcam.instrumental.archive.osgi
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
package io.earcam.instrumental.archive.osgi;

import java.util.Iterator;
import java.util.List;

import io.earcam.instrumental.module.osgi.Clause;

/**
 * <p>
 * PackageBundleMapper interface.
 * </p>
 *
 */
public interface PackageBundleMapper {

	/**
	 * <p>
	 * importsFor.
	 * </p>
	 *
	 * @param requiredPackages a {@link java.util.Iterator} object.
	 * @return a {@link java.util.List} object.
	 */
	public abstract List<Clause> importsFor(Iterator<String> requiredPackages);
}
