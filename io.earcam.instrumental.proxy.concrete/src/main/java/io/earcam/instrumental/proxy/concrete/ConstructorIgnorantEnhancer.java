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

import java.util.List;

import net.sf.cglib.proxy.Enhancer;

@SuppressWarnings({ "rawtypes", "unchecked" })
final class ConstructorIgnorantEnhancer<T> extends Enhancer {

	/** {@inheritDoc} */
	@Override
	protected void filterConstructors(Class sc, List constructors)
	{ /* Avoid constructors as using Objenesis */ }


	/** {@inheritDoc} */
	@Override
	public Class<T> createClass()
	{
		return (Class<T>) super.createClass();
	}
}
