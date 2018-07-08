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

import static java.util.stream.Collectors.toList;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * Methods class.
 * </p>
 *
 */
@ParametersAreNonnullByDefault
public final class Methods {

	/** Constant <code>IS_BRIDGE</code> */
	public static final Predicate<Method> IS_BRIDGE = Method::isBridge;
	/** Constant <code>IS_SYNTHETIC</code> */
	public static final Predicate<Method> IS_SYNTHETIC = Method::isSynthetic;
	/** Constant <code>IS_STATIC</code> */
	public static final Predicate<Method> IS_STATIC = hasModifiers(Modifier.STATIC);

	static final Predicate<Method> IS_NOT_BRIDGE = IS_BRIDGE.negate();
	static final Predicate<Method> IS_NOT_SYNTHETIC = IS_SYNTHETIC.negate();
	static final Predicate<Method> IS_NOT_STATIC = IS_STATIC.negate();


	private Methods()
	{}


	/**
	 * <p>
	 * hasModifiers.
	 * </p>
	 *
	 * @param modifiers a int.
	 * @return a {@link java.util.function.Predicate} object.
	 */
	public static Predicate<Method> hasModifiers(int... modifiers)
	{
		int mods = Arrays.stream(modifiers).reduce(0, (a, b) -> a | b);
		return m -> (m.getModifiers() & mods) == mods;
	}


	/**
	 * <p>
	 * getMethod.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @param name a {@link java.lang.String} object.
	 * @param parameterTypes a {@link java.lang.Class} object.
	 * @return a {@link java.util.Optional} object.
	 */
	public static Optional<Method> getMethod(Class<?> type, String name, Class<?>... parameterTypes)
	{
		return allMethodsOf(type)
				.filter(m -> m.getName().equals(name) && Arrays.equals(m.getParameterTypes(), parameterTypes))
				.findFirst();
	}


	/**
	 * Note: To find a <b>var-args</b> method, the <code>args</code> element must be an actual array
	 *
	 * @param instance a {@link java.lang.Object} object.
	 * @param name the method's name
	 * @param args instances from which the parameter type can be deduced
	 * @return the {@link java.lang.reflect.Method} if found
	 */
	public static Optional<Method> getMethod(Object instance, String name, Object... args)
	{
		return getMethod(instance.getClass(), name, parameterTypesOf(args));
	}


	private static Class<?>[] parameterTypesOf(Object[] args)
	{
		return Arrays.stream(args)
				.map(Object::getClass)
				.toArray(s -> new Class<?>[s]);
	}


	/**
	 * <p>
	 * allMethodsOf.
	 * </p>
	 *
	 * @param type the class
	 * @return absolutely <b>all</b> methods, including those inherited <b>and</b> those overridden
	 * @see #methodsOf(Class)
	 */
	public static Stream<Method> allMethodsOf(Class<?> type)
	{
		Objects.requireNonNull(type, "type cannot be null");
		Stream<Method> methods = Stream.empty();
		Class<?> t = type;
		while(t != null) {
			methods = Stream.concat(methods, Arrays.stream(t.getDeclaredMethods())).sequential();
			t = t.getSuperclass();
		}
		t = type;
		while(t != null) {
			for(Class<?> iface : t.getInterfaces()) {
				methods = Stream.concat(methods, Arrays.stream(iface.getDeclaredMethods()).filter(Method::isDefault)).sequential();
			}
			t = t.getSuperclass();
		}
		return methods;
	}


	/**
	 * <p>
	 * Finds <i>all</i> methods, including inherited but excluding overridden, bridge
	 * and synthetic - and excluding those inherited from {@link Object}.
	 * </p>
	 *
	 * @param type the class
	 * @return all methods, including inherited but excluding overridden, bridge and synthetic
	 * @see #allMethodsOf(Class)
	 */
	public static Stream<Method> methodsOf(Class<?> type)
	{
		List<Method> methods = allMethodsOf(type)
				.filter(IS_NOT_BRIDGE.and(IS_NOT_SYNTHETIC))
				.filter(Methods::isNotDeclaredOnObject)
				.collect(toList());

		List<Method> overridenRidden = removeOverridden(methods);
		return overridenRidden.stream().sequential();
	}


	/**
	 * <p>
	 * removeOverridden.
	 * </p>
	 *
	 * @param methods a {@link java.util.List} object.
	 * @return a {@link java.util.List} object.
	 */
	protected static List<Method> removeOverridden(List<Method> methods)
	{
		List<Method> overridenRidden = new ArrayList<>(methods.size());
		for(Method method : methods) {
			if(!isOverridden(method, overridenRidden)) {
				overridenRidden.add(method);
			}
		}
		return overridenRidden;
	}


	private static boolean isNotDeclaredOnObject(Method m)
	{
		return m.getDeclaringClass() != Object.class;
	}


	private static boolean isOverridden(Method method, List<Method> overridenRidden)
	{
		Predicate<Method> bySameName = m -> m.getName().equals(method.getName());
		Predicate<Method> parameterTypes = sameParameters(method);
		Predicate<Method> returnTypes = m -> m.getReturnType().equals(method.getReturnType());

		return overridenRidden.stream()
				.anyMatch(bySameName.and(parameterTypes).and(returnTypes));
	}


	private static Predicate<Method> sameParameters(Method method)
	{
		Predicate<Method> numberOfParameters = m -> m.getParameterCount() == method.getParameterCount();
		Predicate<Method> parameterTypes = m -> Arrays.equals(m.getParameterTypes(), method.getParameterTypes());

		Predicate<Method> genericParameterTypes = m -> {
			Type[] genericTypes = m.getGenericParameterTypes();
			for(int i = 0; i < m.getParameterCount(); i++) {
				Class<?> type = getClassFromType(genericTypes[i], m.getParameterTypes()[i], m);
				if(!method.getParameterTypes()[i].isAssignableFrom(type)) {
					return false;
				}
			}
			return true;
		};
		return numberOfParameters.and(parameterTypes.or(genericParameterTypes));
	}


	private static Class<?> getClassFromType(Type type, Class<?> clazz, Method m)
	{
		if(type instanceof ParameterizedType) {
			ParameterizedType parameterized = (ParameterizedType) type;
			return Exceptional.apply(classLoaderOf(m)::loadClass, parameterized.getRawType().getTypeName());
		}
		if(type.getTypeName().equals(clazz.getCanonicalName())) {
			return clazz;
		}
		return Exceptional.apply(classLoaderOf(m)::loadClass, type.getTypeName());
	}


	private static ClassLoader classLoaderOf(Method m)
	{
		ClassLoader classLoader = m.getDeclaringClass().getClassLoader();
		return (classLoader == null) ? ClassLoader.getSystemClassLoader() : classLoader;
	}


	/**
	 * MethodHandle for normal class, or default interface, method.
	 *
	 * @param method a normal class instance or interface default method
	 * @return a handle
	 */
	public static MethodHandle handleFor(Method method)
	{
		return Exceptional.get(() -> isJava8() ? java8HandleFor(method) : java9PlusHandleFor(method));
	}


	private static boolean isJava8()
	{
		return System.getProperty("java.version").startsWith("1.8");
	}


	private static MethodHandle java9PlusHandleFor(Method method) throws ReflectiveOperationException
	{
		Class<?> targetClass = method.getDeclaringClass();
		String descriptor = Names.descriptorFor(method).toString();
		MethodType methodType = MethodType.fromMethodDescriptorString(descriptor, targetClass.getClassLoader());

		if(IS_STATIC.test(method)) {
			return MethodHandles.lookup()
					.in(Methods.class)
					.findStatic(targetClass, method.getName(), methodType);
		} else if(method.isDefault()) {
			return MethodHandles.lookup()
					.in(Methods.class)
					.findSpecial(targetClass, method.getName(), methodType, targetClass);
		} else {
			return MethodHandles.lookup()
					.in(Methods.class)
					.findVirtual(targetClass, method.getName(), methodType);
		}

	}


	private static MethodHandle java8HandleFor(Method method) throws ReflectiveOperationException
	{
		Class<?> targetClass = method.getDeclaringClass();
		Field field = Lookup.class.getDeclaredField("IMPL_LOOKUP");
		field.setAccessible(true);
		Lookup lookup = (Lookup) field.get(null);
		return (IS_STATIC.test(method)) ? lookup.unreflect(method) : lookup.unreflectSpecial(method, targetClass);
	}
}
