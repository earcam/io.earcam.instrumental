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

import static io.earcam.instrumental.reflect.Matchers.aStream;
import static io.earcam.instrumental.reflect.Matchers.isAbsent;
import static io.earcam.instrumental.reflect.Matchers.isPresent;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * TODO Note: when refactoring this to produce test suites with meaningful
 * hierarchies and names ;-) you must honour the synthetic accessor (i.e. must
 * have a failing test when isSynthetic() is not filtered)
 *
 * Coverage instrumentation adds methods etc to classes, there cannot assert
 * on explicit size (e.g. number of methods returned)
 *
 */
public class MethodsTest {

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	private int onlyHereToInduceBridge = 42;

	public class InnerToInduceBridge {
		public int nabOuterField()
		{
			return onlyHereToInduceBridge;
		}

	}

	public static class OuterClassToInduceBridge {

		private int numb = 1;

		public class Inner {
			private int num = 5;


			public int nabOuterField()
			{
				return numb;
			}
		}


		public int nabInnerField()
		{
			return new Inner().num;
		}
	}


	@SuppressWarnings("unchecked")
	@Test
	public void getMethodReturnsSynthetic()
	{
		Optional<Method> method = Methods.allMethodsOf(OuterClassToInduceBridge.Inner.class)
				.filter(Methods.IS_SYNTHETIC)
				.filter(m -> m.getReturnType().equals(int.class)) // This is needed as coverage instrumentation adds
																	 // synthetic method
				.findAny();

		assertThat(method, isPresent());
		assertThat(method.get().getReturnType(), is(int.class));
		assertThat(method.get().getParameterTypes(), is(arrayContaining(OuterClassToInduceBridge.Inner.class)));
	}

	public static class Super {
		public void foo()
		{}


		protected void bar()
		{}
	}

	public static class Sub extends Super {
		@Override
		public void foo()
		{}
	}


	@Test
	public void getMethodReturnsOverriddenFromSubclass()
	{
		Method method = Methods.getMethod(Sub.class, "foo").get();
		assertThat(method.getDeclaringClass(), is(Sub.class));
	}


	@Test
	public void getMethodReturnsInherited()
	{
		Method method = Methods.getMethod(Sub.class, "bar").get();
		assertThat(method.getDeclaringClass(), is(Super.class));
	}


	@Test
	public void getMethodReturnsEmptyOptional()
	{
		assertThat(Methods.getMethod(Sub.class, "humbug").isPresent(), is(false));
	}

	// ------

	@SuppressWarnings("unused")
	private class BaseClass {

		protected void boo(String goose)
		{}
	}

	@SuppressWarnings("unused")
	private class SubClass extends BaseClass {
		@Override
		protected void boo(String goose)
		{}


		public String cheese()
		{
			return "red leicester";
		}
	}

	@SuppressWarnings("unused")
	private class SubOfSubClass extends SubClass {

		public String boo(String... geese)
		{
			return "Boo, boo I say to you geese...\n" +
					stream(geese).map(g -> "Boo to " + g).collect(joining("\n"));
		}
	}

	private static Method BASE_BOO_METHOD;
	private static Method OVERRIDDING_BOO_METHOD;


	@BeforeAll
	public static void setUp() throws NoSuchMethodException, SecurityException
	{
		BASE_BOO_METHOD = BaseClass.class.getDeclaredMethod("boo", String.class);
		OVERRIDDING_BOO_METHOD = SubClass.class.getDeclaredMethod("boo", String.class);
	}


	@Test
	public void doesNotIncludeMethodsFromJavaLangObject()
	{
		assertThat(
				Methods.methodsOf(BaseClass.class)
						.filter(m -> m.getDeclaringClass() == Object.class)
						.findAny(),
				isAbsent());

		assertThat(Methods.methodsOf(BaseClass.class), aStream(not(hasItem(hasProperty("declaringClass", equalTo(Object.class))))));
	}


	@Test
	public void testFindsSingleDeclaredProtectedMethod()
	{
		assertThat(Methods.methodsOf(BaseClass.class), aStream(hasItem(BASE_BOO_METHOD)));
	}


	@Test
	public void testFindsCorrectlyOverriddenMethod()
	{
		assertThat(Methods.methodsOf(SubClass.class).collect(toList()), allOf(
				hasItem(OVERRIDDING_BOO_METHOD),
				not(hasItem(BASE_BOO_METHOD))));
	}


	@Test
	public void testFindsCorrectlyOverriddenAndInheritedMethod()
	{
		assertThat(Methods.methodsOf(SubOfSubClass.class).collect(toList()), hasItem(OVERRIDDING_BOO_METHOD));
	}


	@Test
	public void testFindsAllMethodsDeclaredOrInherited()
	{
		assertThat(Methods.methodsOf(SubOfSubClass.class).collect(toList()), hasItem(OVERRIDDING_BOO_METHOD));
	}


	@Test
	public void givenExistingMethodThenOptionalIsPresent()
	{
		Optional<Method> declaredMethod = Methods.getMethod(BaseClass.class, "boo", String.class);
		assertTrue(declaredMethod.isPresent());
		assertThat(declaredMethod.get().getName(), is("boo"));
	}


	@Test
	public void givenNonExistentMethodThenOptionalIsAbsent()
	{
		assertFalse(Methods.getMethod(BaseClass.class, "nonexistent", String.class).isPresent());
	}

	interface A<T extends Serializable> {
		void doSome(T thing);
	}

	class B implements A<String> {
		@Override
		public void doSome(String thing)
		{}
	}


	@Test
	public void whenGenericInterfacePresentThenOnlyListConcrete()
	{
		assertThat(Methods.methodsOf(B.class)
				.filter(m -> "doSome".equals(m.getName()))
				.filter(m -> String.class.equals(m.getParameterTypes()[0]))
				.findAny(), isPresent());
	}

	public static class W<N extends Number> {
		public void receive(N number)
		{}
	}

	class X extends W<Integer> {
		int x;


		@Override
		public void receive(Integer number)
		{
			x = number;
		}
	}

	public static class Y extends W<Float> {
		float y;


		@Override
		public void receive(Float number)
		{
			y = number;
		}


		public void randomUnrelatedMethod(String s1)
		{}
	}

	public static class Z extends Y {
		float z;


		@Override
		public void receive(Float number)
		{
			z = number;
		}


		public void randomUnrelatedMethod(String s1, String s2)
		{}
	}


	@Test
	public void whenGenericSuperClassPresentForNestedInnerClassThenOnlyListConcrete()
	{
		assertThat(Methods.methodsOf(X.class)
				.filter(m -> "receive".equals(m.getName()))
				.count(), is(equalTo(1L)));
	}


	@Test
	public void whenGenericSuperClassPresentForStaticInnerClassThenOnlyListConcrete()
	{
		assertThat(Methods.methodsOf(Y.class)
				.filter(m -> "receive".equals(m.getName()))
				.count(), is(equalTo(1L)));
	}


	@Test
	public void testWhenPrivateFieldOnInnerClassIsReferencedThenSyntheticMethodShouldNotBeRetrieved()
	{
		@SuppressWarnings("unused")
		class IntegerEventSubscriber {
			private Integer event;


			public void on(Integer event)
			{
				this.event = event;
			}
		}
		IntegerEventSubscriber subscriber = new IntegerEventSubscriber();

		subscriber.event = 101;  // This triggers the creation of an synthetic method, in current jvm named "access$0"

		assertThat("Direct field access of an inner class' private field trigger the creation of a synthetic method",
				Arrays.stream(subscriber.getClass().getDeclaredMethods()).filter(Method::isSynthetic).findAny().isPresent(), is(true));

		assertThat(Methods.methodsOf(IntegerEventSubscriber.class).count(), is(1L));

	}


	@Test
	public void whenBlah()
	{
		assertThat(Methods.methodsOf(Z.class)
				.filter(m -> "receive".equals(m.getName()))
				.count(), is(equalTo(1L)));
	}


	@Test
	public void whenGenericInterfacePresentForClassThenOnlyListConcrete()
	{
		assertThat(Methods.methodsOf(String.CASE_INSENSITIVE_ORDER.getClass())
				.filter(m -> "compare".equals(m.getName()))
				.count(), is(equalTo(1L)));
	}

	public abstract class TypeCapture<T> {
		public abstract void withType(T type);
	}


	// TODO with TypeLiteral information we can deduce the concrete type (in this case "N" is "Integer"), need to look
	// at
	// class def to see upper bound "N extends..."
	@Test
	public void testGenericType()
	{
		TypeCapture<W<Integer>> tc = new TypeCapture<W<Integer>>() {
			@Override
			public void withType(W<Integer> type)
			{}
		};
		assertThat(Methods.methodsOf(tc.getClass())
				.filter(m -> "withType".equals(m.getName()))
				.count(), is(equalTo(1L)));
	}

	public abstract class SubTypeCapture<T extends Number> extends TypeCapture<W<T>> {
		public abstract void withType(W<T> type);


		public abstract void withType(W<Float> type, Object otherParameter);
	}


	@Test
	public void testGenericType2()
	{
		Method overridden = Methods.getMethod(SubTypeCapture.class, "withType", W.class).get();
		Method overloaded = Methods.getMethod(SubTypeCapture.class, "withType", W.class, Object.class).get();

		List<Method> methods = Methods.methodsOf(SubTypeCapture.class)
				.filter(m -> "withType".equals(m.getName()))
				.collect(toList());

		assertThat(methods, containsInAnyOrder(overridden, overloaded));
	}

	interface Foo {
		default void defaultMethod(String arg)
		{}
	}

	class Bar implements Foo {
		@Override
		public void defaultMethod(String arg)
		{}
	}

	class Bar2 implements Foo {}

	class Bar3 extends Bar2 {}


	@Test
	public void methodsOfDoesNotReturnOverridenDefault()
	{
		Method method = Methods.methodsOf(Bar.class)
				.filter(m -> m.getName().equals("defaultMethod"))
				.findAny().get();

		assertThat(method.getDeclaringClass(), is(Bar.class));
	}


	@Test
	public void methodsOfReturnsDefault()
	{
		Method method = Methods.methodsOf(Bar2.class)
				.filter(m -> m.getName().equals("defaultMethod"))
				.findAny().get();

		assertThat(method.getDeclaringClass(), is(Foo.class));
	}


	@Test
	public void methodsOfReturnsInheritedDefault()
	{
		Method method = Methods.methodsOf(Bar3.class)
				.filter(m -> m.getName().equals("defaultMethod"))
				.findAny().get();

		assertThat(method.getDeclaringClass(), is(Foo.class));
	}

	public static interface WithDefault {
		default String hello(String name)
		{
			return "Hello " + name;
		}
	}

	public static class ImplementingWithDefault implements WithDefault {

	}


	@Test
	public void handleForDefaultMethod() throws Throwable
	{
		Method method = Methods.getMethod(ImplementingWithDefault.class, "hello", String.class).get();

		ImplementingWithDefault target = new ImplementingWithDefault();
		String arg = "name";

		MethodHandle handle = Methods.handleFor(method);

		assertThat(handle.bindTo(target).invoke(arg), is(equalTo(target.hello(arg))));
	}


	// EARCAM_SNIPPET_BEGIN: normal-handle
	public String taDa()
	{
		return "TA-DA";
	}


	@Test
	public void handleForNormalMethod() throws Throwable
	{
		Method method = Methods.getMethod(MethodsTest.class, "taDa")
				.orElseThrow(NullPointerException::new);

		MethodHandle handle = Methods.handleFor(method).bindTo(this);

		assertThat(handle.invoke(), is(equalTo(taDa())));
	}
	// EARCAM_SNIPPET_END: normal-handle


	public static String baddaBing()
	{
		return "Badda Bing";
	}


	@Test
	public void handleForStaticMethod() throws Throwable
	{
		Method method = Methods.getMethod(MethodsTest.class, "baddaBing").get();
		MethodHandle handle = Methods.handleFor(method);

		assertThat(handle.invoke(), is(equalTo(baddaBing())));
	}


	@Override
	public int hashCode()
	{
		return super.hashCode();
	}


	@Test
	public void handleForOverridenMethod() throws Throwable
	{
		Method method = Methods.getMethod(MethodsTest.class, "hashCode").get();
		MethodHandle handle = Methods.handleFor(method).bindTo(this);

		assertThat(handle.invoke(), is(equalTo(hashCode())));
	}


	@Test
	public void handleForInheritedMethod() throws Throwable
	{
		Method method = Methods.getMethod(MethodsTest.class, "equals", Object.class).get();
		MethodHandle handle = Methods.handleFor(method).bindTo(this);

		assertThat(handle.invokeWithArguments(this), is(true));
		assertThat(handle.invokeWithArguments(new Object[] { null }), is(false));
	}
}
