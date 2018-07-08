/*-
 * #%L
 * io.earcam.instrumental.module.manifest
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
package io.earcam.instrumental.module.manifest;

import java.util.Map;
import java.util.jar.Attributes.Name;

/**
 * <p>
 * ManifestInfo interface.
 * </p>
 *
 */
public interface ManifestInfo {

	/**
	 * <p>
	 * mainAttributes.
	 * </p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public abstract Map<Name, CharSequence> mainAttributes();


	/**
	 * <p>
	 * namedEntries.
	 * </p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public abstract Map<String, Map<Name, CharSequence>> namedEntries();


	/**
	 * <p>
	 * mainAttribute.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link java.lang.CharSequence} object.
	 */
	public default CharSequence mainAttribute(String name)
	{
		return mainAttribute(new Name(name));
	}


	/**
	 * <p>
	 * mainAttribute.
	 * </p>
	 *
	 * @param name a {@link java.util.jar.Attributes.Name} object.
	 * @return a {@link java.lang.CharSequence} object.
	 */
	public default CharSequence mainAttribute(Name name)
	{
		return mainAttributes().get(name);
	}


	/**
	 * <p>
	 * namedEntry.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link java.util.Map} object.
	 */
	public default Map<Name, CharSequence> namedEntry(String name)
	{
		return namedEntries().get(name);
	}

}
