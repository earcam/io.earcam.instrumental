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

import static io.earcam.instrumental.reflect.Names.descriptorToTypeName;
import static io.earcam.instrumental.reflect.Names.descriptorsToTypeNames;
import static io.earcam.instrumental.reflect.Names.typeToBinaryName;
import static io.earcam.instrumental.reflect.Names.typeToDescriptor;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

public class NamesTest {

	@Test
	public void booleanDescriptorToTypeName()
	{
		assertThat(descriptorToTypeName("Z"), is(equalTo(boolean.class.getCanonicalName())));
	}


	@Test
	public void stringDescriptorToTypeName()
	{
		assertThat(descriptorToTypeName("Ljava.lang.String;"), is(equalTo(String.class.getCanonicalName())));
	}


	@Test
	public void singleDimensionDoubleDescriptorToTypeName()
	{
		double[] d1 = new double[0];

		assertThat(descriptorToTypeName(d1.getClass().getName()), is(equalTo(d1.getClass().getCanonicalName())));
	}


	@Test
	public void biDimensionShortDescriptorToTypeName()
	{
		assertThat(descriptorToTypeName("[[S"), is(equalTo(short[][].class.getCanonicalName())));
	}


	@Test
	public void triDimensionStringDescriptorToTypeName()
	{
		String[][][] d3 = new String[0][][];

		assertThat(descriptorToTypeName(d3.getClass().getName()), is(equalTo("java.lang.String[][][]")));
	}


	@Test
	public void intIntDouble_typeDescriptorsToTypeNames()
	{
		assertThat(descriptorsToTypeNames("IID"), contains("int", "int", "double"));
	}


	@Test
	public void StringListString2dArray_typeDescriptorsToTypeNames()
	{
		String desc = "Ljava/lang/String;Ljava/util/List;[[Ljava/lang/String;";
		assertThat(descriptorsToTypeNames(desc), contains(cn(String.class), cn(List.class), cn(String[][].class)));
	}


	private static String cn(Class<?> type)
	{
		return type.getCanonicalName();
	}


	@Test
	public void typeToDescriptor_void()
	{
		assertThat(typeToDescriptor(void.class).toString(), is(equalTo("V")));
	}


	@Test
	public void typeToDescriptor_2DStringArray()
	{
		String typeToDescriptor = Names.typeToDescriptor(String[][].class).toString();

		assertThat(typeToDescriptor, is(equalTo("[[Ljava/lang/String;")));
	}


	@Test
	public void typeToDescriptor_innerClass()
	{
		// EARCAM_SNIPPET_BEGIN: type-to-binary
		String typeToDescriptor = Names.typeToDescriptor(Map.Entry.class).toString();

		assertThat(typeToDescriptor, is(equalTo("Ljava/util/Map$Entry;")));
		// EARCAM_SNIPPET_END: type-to-binary
	}


	@Test
	public void binaryToTypeName_NoPrimitiveValues()  // Would be a class named B, C, J, I, S, D, F or V
	{
		String binaryName = Names.binaryToTypeName("I");

		assertThat(binaryName, is(equalTo("I")));
	}


	@Test
	public void binaryToTypeName_primitiveBooleanArray()
	{
		String binaryName = Names.binaryToTypeName("[Z");

		assertThat(binaryName, is(equalTo(boolean[].class.getTypeName())));
	}


	@Test
	public void binaryToTypeName_discriptorInvalidUnlessArrayType()
	{
		String binaryName = Names.binaryToTypeName("Ljava/lang/String;");

		assertThat(binaryName, is(not(equalTo("java.lang.String"))));
	}


	@Test
	public void binaryToTypeName_objectArray()
	{
		String binaryName = Names.binaryToTypeName("[Ljava/lang/Number;");

		assertThat(binaryName, is(equalTo(Number[].class.getTypeName())));
	}


	@Test
	public void binaryToTypeName_object2DArray()
	{
		// EARCAM_SNIPPET_BEGIN: binary-to-type
		String binaryName = Names.binaryToTypeName("[[Ljava/lang/Object;");

		assertThat(binaryName, is(equalTo(Object[][].class.getTypeName())));
		// EARCAM_SNIPPET_END: binary-to-type
	}


	@Test
	public void descriptorsToTypeNames_regression() throws Exception
	{
		String descriptor = "Lorg/slf4j/event/Level;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/Throwable;";

		List<String> names = Names.descriptorsToTypeNames(descriptor);

		assertThat(names, contains("org.slf4j.event.Level", "java.lang.String", "java.lang.Object[]", "java.lang.Throwable"));
	}


	protected List<String> describeMe(String arg)
	{
		return null;
	}


	@Test
	public void descriptorForMethod() throws NoSuchMethodException, SecurityException
	{
		String expected = "(Ljava/lang/String;)Ljava/util/List;";

		Method method = getClass().getDeclaredMethod("describeMe", String.class);
		String actual = Names.descriptorFor(method).toString();

		assertThat(actual, is(equalTo(expected)));
	}

	@SuppressWarnings("unused")
	// EARCAM_SNIPPET_BEGIN: declared-binaries
	private class Declares {
		private class DeclaredTop {
			private class DeclaredMiddle {
				private class DeclaredBottom {}

				private final Comparator<String> anon = new Comparator<String>() {
					@Override
					public int compare(String o1, String o2)
					{
						return 0;
					}
				};


				public String x()
				{
					Supplier<String> supplier = new Supplier<String>() {

						@Override
						public String get()
						{
							return "hello";
						}

					};
					return supplier.get();
				}
			}
		}
	}
	// EARCAM_SNIPPET_END: declared-binaries


	@Test
	void classesOf()
	{
		// EARCAM_SNIPPET_BEGIN: declared-binary-names
		List<String> classes = Names.declaredBinaryNamesOf(Declares.class).collect(toList());

		assertThat(classes, containsInAnyOrder(
				typeToBinaryName(Declares.DeclaredTop.class),
				typeToBinaryName(Declares.DeclaredTop.DeclaredMiddle.class),
				typeToBinaryName(Declares.DeclaredTop.DeclaredMiddle.class) + "$1",
				typeToBinaryName(Declares.DeclaredTop.DeclaredMiddle.class) + "$2",
				typeToBinaryName(Declares.DeclaredTop.DeclaredMiddle.DeclaredBottom.class)));
		// EARCAM_SNIPPET_END: declared-binary-names
	}
}
