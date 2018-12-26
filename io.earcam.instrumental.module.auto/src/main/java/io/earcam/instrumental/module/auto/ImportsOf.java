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

import java.util.Arrays;
import java.util.function.Consumer;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Remapper;

/**
 * Determine the imported types of a given ASM construct.
 */
class ImportsOf extends Remapper {

	private Consumer<String> importConsumer;


	/**
	 * <p>
	 * Create with the specified consumer ({@link ImportsOf} holds no state)
	 * </p>
	 *
	 * @param importConsumer a {@link Consumer} of imported types.
	 */
	public ImportsOf(Consumer<String> importConsumer)
	{
		this.importConsumer = importConsumer;
	}


	@Override
	public String map(String typeName)
	{
		String mapped = super.map(typeName);
		addInternalType(typeName);
		return mapped;
	}


	@Override
	public String mapType(String type)
	{
		String mapped = super.mapType(type);
		addInternalType(type);
		return mapped;
	}


	/**
	 * The null guard below needed as {@link org.objectweb.asm.commons.ClassRemapper}
	 * invokes {@link #mapType(String)} with the {@code superName} which is {@code null}
	 * when the class name is {@code module-info}
	 * 
	 * @param internal
	 */
	void addInternalType(String internal)
	{
		if(internal != null) {
			Type type = Type.getObjectType(internal);
			addInternalType(type);
		}
	}


	void addInternalType(Type type)
	{
		if(Type.ARRAY == type.getSort()) {
			return; // element/component type will be mapped later
		}
		String name = externalName(type);
		importConsumer.accept(name);
	}


	String externalName(Type type)
	{
		return type.getClassName().replace('/', '.');
	}


	@Override
	public String[] mapTypes(String[] types)
	{
		String[] mapped = super.mapTypes(types);
		Arrays.stream(types).forEach(this::addInternalType);
		return mapped;
	}
}
