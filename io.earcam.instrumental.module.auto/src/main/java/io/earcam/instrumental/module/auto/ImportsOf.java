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

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Remapper;

/**
 * TODO
 * <ol>
 * <li>Optionally skip imports found only in annotations and the annotations themselves</li>
 * <li>Tests for Generic types/methods, lambdas, etc - override mapDesc and take a peek</li>
 * </ol>
 */
class ImportsOf extends Remapper {

	private static final Set<String> PRIMITIVES = namesOf(
			short.class, int.class, long.class, float.class, double.class, char.class, byte.class, boolean.class, void.class);

	private Consumer<String> importConsumer;


	/**
	 * <p>
	 * Constructor for ImportsOf.
	 * </p>
	 *
	 * @param importConsumer a {@link Consumer} of imported types.
	 */
	public ImportsOf(Consumer<String> importConsumer)
	{
		this.importConsumer = importConsumer;
	}


	private static Set<String> namesOf(Class<?>... items)
	{
		return unmodifiableSet(asList(items).stream()
				.map(Class::getCanonicalName)
				.collect(toSet()));
	}


	static boolean isPrimitive(String type)
	{
		return PRIMITIVES.contains(type);
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


	void addInternalType(String internal)
	{
		Type type = Type.getObjectType(internal);
		addInternalType(type);
	}


	void addInternalType(Type type)
	{
		if(Type.ARRAY == type.getSort()) {
			return;// element/component type will be mapped later
		}
		String name = externalName(type);
		if(!isPrimitive(name)) {
			importConsumer.accept(name);
		}
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
