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

import static io.earcam.instrumental.reflect.Types.isClass;
import static io.earcam.instrumental.reflect.Types.isInterface;
import static io.earcam.instrumental.reflect.Types.requireClass;
import static io.earcam.instrumental.reflect.Types.requireInterface;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Formattable;
import java.util.jar.JarInputStream;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.reflect.Types;

public class TypesTest {

	@Test
	public void serializableIsAnInterface()
	{
		assertThat(isInterface(Serializable.class), is(true));
	}


	@Test
	public void overrideAnnotationIsNotAnInterface()
	{
		assertThat(isInterface(Override.class), is(false));
	}


	@Test
	public void stringIsNotAnInterface()
	{
		assertThat(isInterface(String.class), is(false));
		assertThat(isInterface(String.class.getCanonicalName()), is(false));
	}


	@Test
	public void thisTestIsAClass()
	{
		assertThat(isClass(TypesTest.class), is(true));
		assertThat(isClass(TypesTest.class.getCanonicalName()), is(true));
	}


	@Test
	public void thisObjectIsAClass()
	{
		assertThat(isClass(Object.class), is(true));
	}


	@Test
	public void serializableIsNotAClass()
	{
		assertThat(isClass(Serializable.class), is(false));
		assertThat(isClass(Serializable.class.getCanonicalName()), is(false));
	}


	@Test
	public void overrideAnnotationIsNotAClass()
	{
		assertThat(isClass(Override.class), is(false));
		assertThat(isClass(Override.class.getCanonicalName()), is(false));
	}


	@Test
	public void throwsWhenRequireClassReceivesAnInterface()
	{
		// EARCAM_SNIPPET_BEGIN: require-class
		try {
			requireClass(Comparable.class);
			fail();
		} catch(IllegalArgumentException e) {}
		// EARCAM_SNIPPET_END: require-class
	}


	@Test
	public void throwsWhenRequireInterfaceReceivesAClass()
	{
		try {
			requireInterface(BigDecimal.class);
			fail();
		} catch(IllegalArgumentException e) {}
	}


	@Test
	public void doesNotThrowsWhenRequireClassReceivesAClass()
	{
		requireClass(System.class);
	}


	@Test
	public void doesNotThrowsWhenRequireInterfaceReceivesAnInterface()
	{
		requireInterface(Formattable.class);
	}


	@Test
	public void loadsClassFromSimpleType()
	{
		Type type = new Type() {
			@Override
			public String getTypeName()
			{
				return BigDecimal.class.getCanonicalName();
			}
		};
		assertThat(Types.getClass(type), is(equalTo(BigDecimal.class)));
	}


	@Test
	public void isPrimitiveFromString()
	{
		assertThat(Types.isPrimitive(long.class.getCanonicalName()), is(true));
	}


	@Test
	public void isPrimitiveFromType()
	{
		assertThat(Types.isPrimitive(long.class), is(true));
	}


	@Test
	public void isPrimitiveArrayFromType()
	{

		assertThat(Types.isPrimitiveArray(long[].class), is(true));
	}


	@Test
	public void isNotPrimitiveArrayFromType()
	{
		assertThat(Types.isPrimitiveArray(Long[].class), is(false));
	}


	@Test
	public void isPrimitiveWrapper()
	{
		assertThat(Types.isPrimitiveWrapper(Double.class), is(true));
	}


	@Test
	public void isNotPrimitiveWrapper()
	{
		assertThat(Types.isPrimitiveWrapper(Number.class), is(false));
	}


	@Test
	public void wrapperToPrimitive()
	{
		assertThat(Types.wrapperToPrimitive(Float.class), is(float.class));
	}


	@Test
	public void primitiveToWrapper()
	{
		assertThat(Types.primitiveToWrapper(short.class), is(Short.class));
	}


	@Test
	void implementsAll()
	{
		// EARCAM_SNIPPET_BEGIN: implements-all
		assertThat(Types.implementsAll(String.class,
				Serializable.class,
				Comparable.class,
				CharSequence.class), is(true));
		// EARCAM_SNIPPET_END: implements-all
	}


	@Test
	void implementsSome()
	{
		assertThat(Types.implementsAll(String.class, Serializable.class, Comparable.class), is(true));
	}


	@Test
	void doesNotImplementsAll()
	{
		assertThat(Types.implementsAll(String.class, Serializable.class, Comparable.class, CharSequence.class, InputStream.class), is(false));
	}


	@Test
	void extendsFromImmediately()
	{
		assertThat(Types.extendsFrom(JarInputStream.class, ZipInputStream.class), is(true));
	}


	@Test
	void extendsFromEventually()
	{
		assertThat(Types.extendsFrom(JarInputStream.class, InputStream.class), is(true));
	}


	@Test
	void extendsFromUltimately()
	{
		assertThat(Types.extendsFrom(JarInputStream.class, Object.class), is(true));
	}


	@Test
	void doesNotExtendFrom()
	{
		assertThat(Types.extendsFrom(JarInputStream.class, String.class), is(false));
	}


	@Test
	void doesNotImplementsAny()
	{
		assertThat(Types.implementsAll(TypesTest.class, Serializable.class, Comparable.class), is(false));
	}
}
