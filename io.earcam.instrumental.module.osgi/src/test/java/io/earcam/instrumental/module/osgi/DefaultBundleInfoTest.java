/*-
 * #%L
 * io.earcam.instrumental.module.osgi
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
package io.earcam.instrumental.module.osgi;

import static io.earcam.instrumental.module.osgi.BundleInfoBuilder.bundle;
import static io.earcam.instrumental.module.osgi.Clause.clause;
import static io.earcam.instrumental.module.osgi.Clause.sortedSet;
import static io.earcam.instrumental.module.osgi.ClauseParameters.ClauseParameter.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.acme.imp.DummyActivator;

import io.earcam.instrumental.module.manifest.AbstractManifestBuilder;

public class DefaultBundleInfoTest {

	@Nested
	class Equality {

		private class AnotherManifestBuilder extends AbstractManifestBuilder<AnotherManifestBuilder> {

			@Override
			protected AnotherManifestBuilder self()
			{
				return this;
			}


			@Override
			public boolean equals(AbstractManifestBuilder<?> that)
			{
				throw new IllegalStateException("Only meant to be called as RHS arg of eq");
			}


			@Override
			protected void preBuildHook()
			{}

		}

		final BundleInfo a = createBundleA();


		private BundleInfo createBundleA()
		{
			return (BundleInfo) bundle()
					.symbolicName("com.acme.sym.nom")
					.exportPackages(sortedSet("com.acme.foo", "com.acme.bar"), version(0, 1, 42).attribute("foo", "bar"))
					.exportPackages("com.acme.hoo", "com.acme.hum")
					.importPackages("com.acme.base");
		}


		@Test
		void equal()
		{

			BundleInfo b = createBundleA();

			assertThat(a, is(equalTo(b)));
			assertThat(a.hashCode(), is(equalTo(b.hashCode())));
		}


		@Test
		void notEqualWhenSymbolicNamesDiffer()
		{
			BundleInfo b = bundle()
					.symbolicName("org.sample.example")
					.exportPackages(sortedSet("com.acme.foo", "com.acme.bar"), version(0, 1, 42).attribute("foo", "bar"))
					.exportPackages("com.acme.hoo", "com.acme.hum")
					.importPackages("com.acme.base")
					.construct();

			assertThat(a, is(not(equalTo(b))));
		}


		@Test
		void notEqualWhenClausesDiffer()
		{
			BundleInfo b = bundle()
					.symbolicName("org.sample.example")
					.exportPackages(sortedSet("com.acme.foo"), version(9, 99, 999))
					.exportPackages("com.acme.hoo", "com.acme.hum")
					.importPackages("com.acme.base")
					.construct();

			assertThat(a, is(not(equalTo(b))));
		}


		@Test
		void equalWhenClausesIdentical()
		{
			BundleInfo b = createBundleA();

			assertThat(a, is(equalTo(b)));
		}


		@Test
		void notEqualToSimilarBuilderOfDifferentType()
		{
			AnotherManifestBuilder b = new AnotherManifestBuilder();
			assertThat(a, is(not(equalTo(b))));
		}


		@Test
		void notEqualToNull()
		{
			BundleInfo b = null;

			assertFalse(a.equals(b));
		}


		@Test
		void notEqualToNullObject()
		{
			Object b = null;

			assertFalse(a.equals(b));
		}

	}

	@Nested
	class Reconstruct {

		@Test
		void reconstructedAttributesAreMerged()
		{
			BundleInfo reconstructed = bundle()
					.symbolicName("org.sample.example")
					.exportPackages(sortedSet("com.acme.a"), version(1, 2, 3))
					.construct()
					.deconstruct()
					.exportPackages(sortedSet("com.acme.b"), version(4, 5, 6))
					.construct();

			assertThat(reconstructed.exportPackage(), contains(
					clause("com.acme.a", version(1, 2, 3)),
					clause("com.acme.b", version(4, 5, 6))));
		}
	}


	@Test
	void bundleFromStringManifest()
	{
		// EARCAM_SNIPPET_BEGIN: parse
		String manifest = "Manifest-Version: 1.0\n" +
				"Bundle-ManifestVersion: 2\n" +
				"Bundle-SymbolicName: com.acme.sym.nom\n" +
				"Export-Package: com.acme.bar;com.acme.foo;version=0.1.42;hoo:=har\n" +
				"Import-Package: com.acme.base\n\n";

		BundleInfo actual = BundleInfoBuilder.bundleFrom(manifest).construct();

		BundleInfo expected = bundle()
				.symbolicName("com.acme.sym.nom")
				.exportPackages(
						sortedSet("com.acme.foo", "com.acme.bar"),
						version(0, 1, 42).directive("hoo", "har"))
				.importPackages("com.acme.base")
				.construct();

		assertThat(actual, is(equalTo(expected)));
		// EARCAM_SNIPPET_END: parse
	}


	@Test
	void bundleToStringManifest()
	{
		Manifest manifest = bundle()
				.symbolicName("com.acme.sym.nom")
				.exportPackages(sortedSet("com.acme.a", "com.acme.b"), directive("hoo", "har"))
				.exportPackages(sortedSet("com.acme.c"), attribute("fee", "fi"))
				.importPackages("com.acme.base")
				.toManifest();

		Attributes attributes = manifest.getMainAttributes();
		assertThat(attributes.getValue("Export-Package"), equalTo("com.acme.a;com.acme.b;hoo:=har,com.acme.c;fee=fi"));
	}


	@Test
	void activator()
	{
		String activatorName = DummyActivator.class.getCanonicalName();

		BundleInfo bundle = bundle()
				.symbolicName("com.acme.sym.nom")
				.activator(activatorName)
				.construct();

		assertThat(bundle.activator(), is(equalTo(activatorName)));
	}


	@Test
	void givenNoActivatorThenReturnsNull()
	{
		BundleInfo bundle = bundle()
				.symbolicName("com.acme.sym.nom")
				.construct();

		assertThat(bundle.activator(), is(nullValue()));
	}
}
