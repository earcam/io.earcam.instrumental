/*-
 * #%L
 * io.earcam.instrumental.reflect
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
package io.earcam.instrumental.reflect;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * Types class.
 * </p>
 *
 */
public final class Types {

	private static final ClassLoader CLASS_LOADER = Types.class.getClassLoader();

	/**
	 * {@link java.lang.Class#isPrimitive()}
	 */
	private static final Set<String> PRIMITIVES = namesOf(
			short.class, int.class, long.class, float.class, double.class, char.class, byte.class, boolean.class, void.class);

	private static final Set<String> WRAPPERS = namesOf(
			Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class, Byte.class, Boolean.class, Void.class);

	private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE;
	private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER;
	static {
		Map<Class<?>, Class<?>> map = new HashMap<>(8);
		map.put(Boolean.class, boolean.class);
		map.put(Byte.class, byte.class);
		map.put(Character.class, char.class);
		map.put(Double.class, double.class);
		map.put(Float.class, float.class);
		map.put(Integer.class, int.class);
		map.put(Long.class, long.class);
		map.put(Short.class, short.class);
		map.put(Void.class, void.class);
		WRAPPER_TO_PRIMITIVE = unmodifiableMap(map);
		PRIMITIVE_TO_WRAPPER = unmodifiableMap(WRAPPER_TO_PRIMITIVE.entrySet().stream().collect(toMap(Map.Entry::getValue, Map.Entry::getKey)));
	}


	private Types()
	{}


	private static Set<String> namesOf(Class<?>... items)
	{
		return unmodifiableSet(asList(items).stream()
				.map(Class::getCanonicalName)
				.collect(toSet()));
	}


	/**
	 * <p>
	 * wrapperToPrimitive.
	 * </p>
	 *
	 * @param type a {@link Class} object.
	 * @return a {@link Class} object.
	 */
	public static Class<?> wrapperToPrimitive(Class<?> type)
	{
		return WRAPPER_TO_PRIMITIVE.get(type);
	}


	/**
	 * <p>
	 * primitiveToWrapper.
	 * </p>
	 *
	 * @param type a {@link Class} object.
	 * @return a {@link Class} object.
	 */
	public static Class<?> primitiveToWrapper(Class<?> type)
	{
		return PRIMITIVE_TO_WRAPPER.get(type);
	}


	/**
	 * <p>
	 * isPrimitive.
	 * </p>
	 *
	 * @param type a {@link java.lang.reflect.Type} object.
	 * @return a boolean.
	 */
	public static boolean isPrimitive(Type type)
	{

		Class<?> clazz = getClass(type);
		return clazz.isPrimitive();
	}


	/**
	 * <p>
	 * isPrimitive.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isPrimitive(String name)
	{
		return PRIMITIVES.contains(name);
	}


	/**
	 * <p>
	 * isPrimitiveWrapper.
	 * </p>
	 *
	 * @param type a {@link java.lang.reflect.Type} object.
	 * @return a boolean.
	 */
	public static boolean isPrimitiveWrapper(Type type)
	{
		return isPrimitiveWrapper(type.getTypeName());
	}


	/**
	 * <p>
	 * isPrimitiveWrapper.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isPrimitiveWrapper(String name)
	{
		return WRAPPERS.contains(name);
	}


	/**
	 * <p>
	 * isPrimitiveArray.
	 * </p>
	 *
	 * @param type a {@link java.lang.reflect.Type} object.
	 * @return a boolean.
	 */
	public static boolean isPrimitiveArray(Type type)
	{
		Class<?> clazz = getClass(type);
		return clazz.isArray() && isPrimitive(clazz.getComponentType());
	}


	/**
	 * <p>
	 * isInterface.
	 * </p>
	 *
	 * @param type a {@link java.lang.reflect.Type} object.
	 * @return a boolean.
	 */
	public static boolean isInterface(Type type)
	{
		Class<?> clazz = getClass(type);
		return clazz.isInterface() && !clazz.isAnnotation();
	}


	/**
	 * <p>
	 * isInterface.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isInterface(String name)
	{
		return isInterface(getClass(name, CLASS_LOADER));
	}


	/**
	 * <p>
	 * isClass.
	 * </p>
	 *
	 * @param type a {@link java.lang.reflect.Type} object.
	 * @return a boolean.
	 */
	public static boolean isClass(Type type)
	{
		Class<?> clazz = getClass(type);
		return clazz.getSuperclass() != null || Object.class.equals(clazz);
	}


	/**
	 * <p>
	 * requireClass.
	 * </p>
	 *
	 * @param type a {@link java.lang.reflect.Type} object.
	 */
	public static void requireClass(Type type)
	{
		Objects.requireNonNull(type, "type cannot be null");
		if(!isClass(type)) {
			throw new IllegalArgumentException("type '" + type.getTypeName() + "' is not a class");
		}
	}


	/**
	 * <p>
	 * isClass.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isClass(String name)
	{
		return isClass(getClass(name, CLASS_LOADER));
	}


	/**
	 * <p>
	 * getClass.
	 * </p>
	 *
	 * @param type a {@link java.lang.reflect.Type} object.
	 * @return a {@link Class} object.
	 */
	public static Class<?> getClass(Type type)
	{
		return getClass(type, CLASS_LOADER);
	}


	/**
	 * <p>
	 * getClass.
	 * </p>
	 *
	 * @param type a {@link java.lang.reflect.Type} object.
	 * @param classLoader a {@link java.lang.ClassLoader} object.
	 * @return a {@link Class} object.
	 */
	public static Class<?> getClass(Type type, ClassLoader classLoader)
	{
		return type instanceof Class ? ((Class<?>) type) : getClass(type.getTypeName(), classLoader);
	}


	/**
	 * <p>
	 * getClass.
	 * </p>
	 *
	 * @param typeName a {@link java.lang.String} object.
	 * @param classLoader a {@link java.lang.ClassLoader} object.
	 * @return a {@link Class} object.
	 */
	public static Class<?> getClass(String typeName, ClassLoader classLoader)
	{
		return Exceptional.apply(classLoader::loadClass, typeName);
	}


	/**
	 * <p>
	 * requireInterface.
	 * </p>
	 *
	 * @param type a {@link java.lang.reflect.Type} object.
	 */
	public static void requireInterface(Type type)
	{
		Objects.requireNonNull(type, "type cannot be null");
		if(!isInterface(type)) {
			throw new IllegalArgumentException("type '" + type.getTypeName() + "' is not an interface");
		}
	}


	/**
	 * <p>
	 * implementsAll.
	 * </p>
	 *
	 * @param type a {@link Class} object.
	 * @param interfaces a {@link Class} object.
	 * @return a boolean.
	 */
	public static boolean implementsAll(Class<?> type, Class<?>... interfaces)
	{
		return implementsAll(type, Arrays.asList(interfaces));

	}


	/**
	 * <p>
	 * Returns {@code true} IFF {@code type} implements all {@code interfaces}.
	 * </p>
	 *
	 * @param type a {@link Class} object.
	 * @param interfaces a {@link Collection} object.
	 * @return a boolean.
	 */
	public static boolean implementsAll(Class<?> type, Collection<Class<?>> interfaces)
	{
		List<Class<?>> implemented = allInterfacesOf(type).collect(Collectors.toList());
		Predicate<? super Class<?>> contained = implemented::contains;
		return !(implemented.isEmpty() || interfaces.stream().anyMatch(contained.negate()));
	}


	/**
	 * <p>
	 * Return all the interfaces of a given class.
	 * </p>
	 *
	 * @param type a {@link Class}.
	 * @return a {@link Stream} consisting of all interfaces of {@code type}.
	 */
	public static Stream<Class<?>> allInterfacesOf(Class<?> type)
	{
		Stream<Class<?>> interfaces = Arrays.stream(type.getInterfaces());
		return type.getSuperclass() == null ? interfaces : Stream.concat(interfaces, allInterfacesOf(type.getSuperclass()));
	}


	/**
	 * <p>
	 * Return {@code true} IFF the given {@code type} extends from the {@code superType}.
	 * </p>
	 *
	 * @param type a {@link Class} to check.
	 * @param superType a {@link Class} to check against.
	 * @return {@code true} IFF {@code type extends superType}.
	 */
	public static boolean extendsFrom(Class<?> type, Class<?> superType)
	{
		Stream<Class<?>> supers = allSuperTypesOf(type);
		return supers.anyMatch(Predicate.isEqual(superType));
	}


	/**
	 * <p>
	 * Return all the super-types of a given class.
	 * </p>
	 *
	 * @param type a {@link Class} object.
	 * @return a {@link Stream} object.
	 */
	public static Stream<Class<?>> allSuperTypesOf(Class<?> type)
	{
		Stream<Class<?>> supers = Stream.of(type);
		return type.getSuperclass() == null ? supers : Stream.concat(supers, allSuperTypesOf(type.getSuperclass()));
	}
}
