/*-
 * #%L
 * io.earcam.instrumental.proxy.concrete
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
package io.earcam.instrumental.proxy.concrete;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;

final class SignedPackageRenamingPolicy extends DefaultNamingPolicy {

	private static final SignedPackageRenamingPolicy RENAMING_INSTANCE = new SignedPackageRenamingPolicy();


	private SignedPackageRenamingPolicy()
	{}


	/**
	 * <p>
	 * apply.
	 * </p>
	 *
	 * @param enhancer a {@link net.sf.cglib.proxy.Enhancer} object.
	 * @param type a {@link java.lang.Class} object.
	 */
	public static void apply(Enhancer enhancer, Class<?> type)
	{
		if(type.getSigners() != null) {
			enhancer.setNamingPolicy(RENAMING_INSTANCE);
		}
	}


	@Override
	public String getClassName(String prefix, String source, Object key, Predicate names)
	{
		return Proxy.class.getPackage().getName() + "." + super.getClassName(prefix, source, key, names);
	}
}
