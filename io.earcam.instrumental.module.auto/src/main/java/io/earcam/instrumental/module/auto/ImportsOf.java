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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * <ol>
 * <li>Optionally skip imports found only in annotations and the annotations themselves</li>
 * <li>Tests for Generic types/methods, lambdas, etc</li>
 * </ol>
 */
class ImportsOf extends Remapper {

	private static final Logger LOG = LoggerFactory.getLogger(ImportsOf.class);

	private static final Set<String> PRIMITIVES = namesOf(
			short.class, int.class, long.class, float.class, double.class, char.class, byte.class, boolean.class, void.class);

	private Consumer<String> importConsumer;


	/**
	 * <p>
	 * Constructor for ImportsOf.
	 * </p>
	 *
	 * @param importConsumer a {@link java.util.function.Consumer} object.
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


	/** {@inheritDoc} */
	@Override
	public String map(String typeName)
	{
		String mapped = super.map(typeName);
		addInternalType(typeName);
		return mapped;
	}


	/** {@inheritDoc} */
	@Override
	public String mapType(String type)
	{
		String mapped = super.mapType(type);
		addInternalType(type);
		return mapped;
	}


	void addInternalType(String internal)
	{
		if(internal == null) {
			return; // TODO why? @see AsJpmsModuleAutoRequireIntegrationTest
		}
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


	/** {@inheritDoc} */
	@Override
	public String[] mapTypes(String[] types)
	{
		String[] mapped = super.mapTypes(types);
		Arrays.stream(types).forEach(this::addInternalType);
		return mapped;
	}


	/** {@inheritDoc} */
	@Override
	public String mapDesc(String desc)
	{
		String mapped = super.mapDesc(desc);
		LOG.trace("mapDesc({})", desc);
		// TODO Type type = Type.getType(desc) ... addInternalType(type)
		return mapped;
	}
}
