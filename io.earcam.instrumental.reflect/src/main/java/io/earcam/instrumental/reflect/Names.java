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

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import io.earcam.utilitarian.charstar.CharSequences;

//@formatter:off
/**
 * <p>
 * Names for things.
 * </p>
 * 
 * Definitions by example, given {@code java.lang.Thread}:
 * <ul>
 *    <li>
 *       <i>internal</i>:
 *       <pre>java/lang/Thread</pre>
 *    </li>
 *    <li>
 *       <i>descriptor</i>:
 *       <pre>Ljava/lang/Thread;</pre>
 *    </li>
 *    <li>
 *       <i>resource</i>:
 *       <pre>java/lang/Thread.class</pre>
 *    </li>
 * </ul>
 * 
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.2">JVMS §4.2</a>
 */
//@formatter:on
public final class Names {

	private static final String CLASS_SUFFIX = ".class";
	private static final char L = 'L';
	private static final Map<Character, String> PRIMITIVE_DESCRIPTORS;  // excludes 'L'
	private static final Map<String, String> PRIMITIVE_TO_DESCRIPTOR;
	static {
		Map<Character, String> map = new HashMap<>(8);
		map.put('Z', "boolean");
		map.put('B', "byte");
		map.put('C', "char");
		map.put('D', "double");
		map.put('F', "float");
		map.put('I', "int");
		map.put('J', "long");
		map.put('S', "short");
		map.put('V', "void");

		PRIMITIVE_DESCRIPTORS = unmodifiableMap(map);

		PRIMITIVE_TO_DESCRIPTOR = unmodifiableMap(map.entrySet().stream()
				.collect(toMap(Map.Entry::getValue, e -> Character.toString(e.getKey()))));
	}


	private Names()
	{}


	/**
	 * @param internalName the <i>internal</i> name
	 * @return the type name
	 */
	public static String internalToTypeName(CharSequence internalName)
	{
		if(CharSequences.endsWith(internalName, "package-info") || CharSequences.endsWith(internalName, "module-info")) {
			return CharSequences.replace(internalName, '/', '.').toString();
		}

		int depth = 0;
		while(CharSequences.indexOf(internalName, '[', depth) != -1) {
			++depth;
		}
		StringBuilder typeName = new StringBuilder();
		if(depth > 0) {
			if(internalName.charAt(depth) == 'L') {
				typeName.append(internalName, depth + 1, internalName.length() - 1);
			} else {
				typeName.append(PRIMITIVE_DESCRIPTORS.get(internalName.charAt(depth)));
			}
			while(depth-- > 0) {
				typeName.append('[').append(']');
			}
		} else {
			typeName.append(internalName);
		}
		while((depth = typeName.indexOf("/", depth)) != -1) {
			typeName.setCharAt(depth, '.');
		}
		return typeName.toString();
	}


	/**
	 * @param type the fully qualified type name
	 * @return the <i>internal</i> name
	 */
	public static String typeToInternalName(CharSequence type)
	{
		return CharSequences.replace(type, '.', '/').toString();
	}


	/**
	 * @param type the type.
	 * @return the <i>internal</i> name.
	 */
	public static String typeToInternalName(Type type)
	{
		return typeToInternalName(type.getTypeName());
	}


	/**
	 * @param type the type.
	 * @return the <i>internal</i> name with ".class" appended.
	 */
	public static String typeToResourceName(Type type)
	{
		return typeToResourceName(type.getTypeName());
	}


	/**
	 * @param type FQN of the type.
	 * @return the <i>internal</i> name with ".class" appended.
	 */
	public static String typeToResourceName(String type)
	{
		return typeToInternalName(type) + CLASS_SUFFIX;
	}


	/**
	 * @see <a href="http://download.forge.objectweb.org/asm/asm4-guide.pdf">2.1.3 of asm4-guide</a>
	 * @param desc the type descriptor.
	 * @return the canonical name of the type.
	 */
	public static String descriptorToTypeName(String desc)
	{
		List<String> binaryNames = descriptorsToTypeNames(desc);
		if(binaryNames.size() != 1) {
			throw new IllegalArgumentException("No single type name in description: " + binaryNames);
		}
		return binaryNames.get(0);
	}


	private static String primitiveDescriptor(char descriptorChar)
	{
		return PRIMITIVE_DESCRIPTORS.get(descriptorChar);
	}


	/**
	 * @param desc a {@link java.lang.String} object.
	 * @return a {@link java.util.List} object.
	 */
	public static List<String> descriptorsToTypeNames(String desc)
	{
		int pos = 0;
		List<String> types = new ArrayList<>();

		while(pos < desc.length()) {
			StringBuilder arraySuffix = new StringBuilder();
			while(desc.charAt(pos) == '[') {
				arraySuffix.append("[]");
				++pos;
			}
			char current = desc.charAt(pos);
			if(current != L) {
				types.add(primitiveDescriptor(current) + arraySuffix);
				++pos;
				continue;
			}
			int end = desc.indexOf(';', pos);

			if(end == -1) {
				throw new IllegalArgumentException("Expecting object end marker ';'.  "
						+ "Could not parse next descriptor in: '" + desc + "' with: '" + desc.substring(pos) + "' remaining");
			}
			types.add(desc.substring(pos + 1, end).replace('/', '.') + arraySuffix);
			pos = end + 1;
		}
		return types;
	}


	/**
	 * @param c a {@link java.lang.Class} object.
	 * @return a {@link java.lang.CharSequence} object.
	 */
	public static CharSequence typeToDescriptor(Type c)
	{
		return typeToDescriptor(c.getTypeName());
	}


	/**
	 * @param type a {@link java.lang.CharSequence} object.
	 * @return a {@link java.lang.CharSequence} object.
	 */
	public static CharSequence typeToDescriptor(CharSequence type)
	{
		StringBuilder name = new StringBuilder();
		CharSequence t = type;
		while(CharSequences.endsWith(t, "[]")) {
			name.append('[');
			t = t.subSequence(0, t.length() - 2);
		}
		name.append(PRIMITIVE_TO_DESCRIPTOR.getOrDefault(t, classTypeToDescriptor(t)));
		return name;
	}


	private static String classTypeToDescriptor(CharSequence type)
	{
		return "L" + typeToInternalName(type) + ";";
	}


	/**
	 * @param method a {@link java.lang.reflect.Method} object.
	 * @return a {@link java.lang.CharSequence} object.
	 */
	public static CharSequence descriptorFor(Method method)
	{
		StringBuilder builder = new StringBuilder().append('(');
		for(Class<?> type : method.getParameterTypes()) {
			builder.append(typeToDescriptor(type));
		}
		return builder
				.append(')')
				.append(typeToDescriptor(method.getReturnType()));
	}


	/**
	 * @param type a {@link java.lang.Class} object.
	 * @return a stream of all class names declared <i>inside</i> the {@code type} argument
	 */
	public static Stream<String> declaredInternalNamesOf(Class<?> type)
	{
		return concat(
				concat(
						stream(type.getDeclaredClasses()).map(Names::typeToInternalName),
						stream(type.getDeclaredClasses()).flatMap(Names::declaredInternalNamesOf)),
				anonymousClassesOf(type));
	}


	private static Stream<String> anonymousClassesOf(Class<?> type)
	{
		List<String> anonymousClasses = new ArrayList<>();
		int i = 1;
		String baseName = typeToInternalName(type);
		String name = anonymousName(baseName, i);
		while(type.getClassLoader().getResource(name + CLASS_SUFFIX) != null) {
			anonymousClasses.add(name);
			name = anonymousName(baseName, ++i);
		}
		return anonymousClasses.stream();
	}


	private static String anonymousName(String baseName, int i)
	{
		return baseName + '$' + i;
	}
}
