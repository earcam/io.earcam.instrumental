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
package io.earcam.instrumental.module.osgi.parser;

import static io.earcam.instrumental.module.osgi.BundleManifestHeaders.BUNDLE_SYMBOLICNAME;
import static io.earcam.instrumental.module.osgi.BundleManifestHeaders.EXPORT_PACKAGE;
import static io.earcam.instrumental.module.osgi.BundleManifestHeaders.IMPORT_PACKAGE;
import static io.earcam.instrumental.module.osgi.parser.ParserValidityMatcher.invalid;
import static io.earcam.instrumental.module.osgi.parser.ParserValidityMatcher.valid;
import static io.earcam.instrumental.module.osgi.parser.Parsing.failFastParserFor;
import static io.earcam.instrumental.module.osgi.parser.Parsing.parserFor;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.earcam.instrumental.module.osgi.parser.ManifestParser.ExportsContext;
import io.earcam.instrumental.module.osgi.parser.ManifestParser.ImportsContext;
import io.earcam.instrumental.module.osgi.parser.ManifestParser.ManifestContext;
import io.earcam.instrumental.module.osgi.parser.ManifestParser.SymbolicNameContext;

@SuppressWarnings("squid:S2187")  // SonarQube false-positive; tests are only in @Nested
public class ManifestGrammarTest {

	// initally just test in/valid, but must confirm tree elements?

	@Nested
	class IndividualRule {

		// @Nested
		// class Version {
		//
		// @Test
		// void versionRange()
		// {
		// String range = "\"[0.0.1,1.0.0)\"";
		// String value = "version=" + range;
		//
		// ManifestParser parser = parserFor(value);
		//
		// VersionAttributeContext context = parser.versionAttribute();
		//
		// assertThat(context, is(valid()));
		//
		// // TODO fragile - this will be covered by the listener-to-builder tests
		// assertThat(context.getChild(2).getText(), is(equalTo(range)));
		// }
		// }

		@Nested
		class SymbolicName {

			@Test
			void validWithoutParameters()
			{
				String value = "com-acme.some_thing";

				ManifestParser parser = parserFor(BUNDLE_SYMBOLICNAME, value);

				SymbolicNameContext context = parser.symbolicName();

				assertThat(context, is(valid()));

				assertThat(context.getChild(2).getText(), is(equalTo(value)));
			}


			@Test
			void validWithParameters()
			{
				String symbolicName = "com.acme.api";
				String value = symbolicName + ";amaze=balls";

				ManifestParser parser = parserFor(BUNDLE_SYMBOLICNAME, value);

				SymbolicNameContext context = parser.symbolicName();

				assertThat(context, is(valid()));

				assertThat(context.getChild(2).getText(), is(equalTo(symbolicName)));
			}


			@Test
			void invalidWithoutParametersWhenContainsSpace()
			{
				ManifestParser parser = parserFor(BUNDLE_SYMBOLICNAME, "com acme some thing");

				SymbolicNameContext context = parser.symbolicName();

				assertThat(context, is(invalid()));
			}

		}

		@Nested
		class ExportPackage {

			@Test
			void validWithParameterForSinglePackage()
			{
				String value = "com.acme.thingie;version=1.2.3";
				ManifestParser parser = parserFor(EXPORT_PACKAGE, value);

				ExportsContext context = parser.exports();

				assertThat(context, is(valid()));
			}


			@Test
			void validWithoutParametersForMultiplePackages()
			{
				String value = "com.acme.a,com.acme.b";
				ManifestParser parser = parserFor(EXPORT_PACKAGE, value);

				ExportsContext context = parser.exports();

				assertThat(context, is(valid()));
			}


			@Test
			void validWithQuotedParameterForSinglePackage()
			{
				String value = "com.acme.thing.a.ma.jig.quoted;version=\"4.5.6\"";
				ManifestParser parser = parserFor(EXPORT_PACKAGE, value);

				ExportsContext context = parser.exports();

				assertThat(context, is(valid()));
			}


			@Test
			void validWithParametersForSinglePackage()
			{
				String value = "com.acme.thingie;version=1.2.3;resolution:=optional";
				ManifestParser parser = parserFor(EXPORT_PACKAGE, value);

				ExportsContext context = parser.exports();

				assertThat(context, is(valid()));
			}


			@Test
			void validWithParametersForMultiplePackages()
			{
				String value = "com.acme.x,com.acme.y;version=1.2.3";
				ManifestParser parser = parserFor(EXPORT_PACKAGE, value);

				ExportsContext context = parser.exports();

				assertThat(context, is(valid()));
			}


			@Test
			void validWithIndividualParametersForMultiplePackages()
			{
				String value = "com.acme.aye;com.acme.bee;version=1.0.5236;resolution:=mandatory";
				ManifestParser parser = parserFor(EXPORT_PACKAGE, value);

				ExportsContext context = parser.exports();

				assertThat(context, is(valid()));
			}


			@Test
			void validExportFromOsgiCore()
			{
				String value = "org.osgi.dto;version=\"1.0\",org.osgi.resource;version=\"1." +
						"0\",org.osgi.resource.dto;version=\"1.0\";uses:=\"org.osgi.dto\",org.osgi.fr" +
						"amework;version=\"1.8\",org.osgi.framework.dto;version=\"1.8\";uses:=\"org.o" +
						"sgi.dto\",org.osgi.framework.hooks.bundle;version=\"1.1\";uses:=\"org.osgi." +
						"framework\",org.osgi.framework.hooks.resolver;version=\"1.0\";uses:=\"org.o" +
						"sgi.framework.wiring\",org.osgi.framework.hooks.service;version=\"1.1\";us" +
						"es:=\"org.osgi.framework\",org.osgi.framework.hooks.weaving;version=\"1.1\"" +
						";uses:=\"org.osgi.framework.wiring\",org.osgi.framework.launch;version=\"1" +
						".2\";uses:=\"org.osgi.framework\",org.osgi.framework.namespace;version=\"1." +
						"1\";uses:=\"org.osgi.resource\",org.osgi.framework.startlevel;version=\"1.0" +
						"\";uses:=\"org.osgi.framework\",org.osgi.framework.startlevel.dto;version=" +
						"\"1.0\";uses:=\"org.osgi.dto\",org.osgi.framework.wiring;version=\"1.2\";uses" +
						":=\"org.osgi.framework,org.osgi.resource\",org.osgi.framework.wiring.dto;" +
						"version=\"1.2\";uses:=\"org.osgi.dto,org.osgi.resource.dto\",org.osgi.servi" +
						"ce.condpermadmin;version=\"1.1.1\";uses:=\"org.osgi.framework,org.osgi.ser" +
						"vice.permissionadmin\",org.osgi.service.packageadmin;version=\"1.2\";uses:" +
						"=\"org.osgi.framework\",org.osgi.service.permissionadmin;version=\"1.2\",or" +
						"g.osgi.service.startlevel;version=\"1.1\";uses:=\"org.osgi.framework\",org." +
						"osgi.service.url;version=\"1.0\",org.osgi.util.tracker;version=\"1.5.1\";us" +
						"es:=\"org.osgi.framework\"\n";
				ManifestParser parser = parserFor(EXPORT_PACKAGE, value);

				ExportsContext context = parser.exports();

				assertThat(context, is(valid()));
			}
		}

		@Nested
		class ImportPackage {

			@Test
			void validWithoutParametersForSinglePackage()
			{
				String value = "com.acme.api";
				ManifestParser parser = parserFor(IMPORT_PACKAGE, value);

				ImportsContext context = parser.imports();

				assertThat(context, is(valid()));
			}


			@Test
			void validWithoutParametersForMultiplePackages()
			{
				String value = "com.acme.api,com.acme.ext";
				ManifestParser parser = parserFor(IMPORT_PACKAGE, value);

				ImportsContext context = parser.imports();

				assertThat(context, is(valid()));
			}


			@Test
			void validWithParametersForSinglePackage()
			{
				String value = "com.acme.api;directive:=must;attribute=has";
				ManifestParser parser = parserFor(IMPORT_PACKAGE, value);

				ImportsContext context = parser.imports();

				assertThat(context, is(valid()));
			}


			@Test
			void validWithParametersForMultiplePackages()
			{
				String value = "com.acme.api;com.acme.ext;directive:=must;attribute=has";
				ManifestParser parser = parserFor(IMPORT_PACKAGE, value);

				ImportsContext context = parser.imports();

				assertThat(context, is(valid()));
			}


			@Test
			void validWithIndividualParametersForMultiplePackages()
			{
				String value = "com.acme.api;version=\"1.2.3\",com.acme.ext;directive:=must;attribute=has";
				ManifestParser parser = parserFor(IMPORT_PACKAGE, value);

				ImportsContext context = parser.imports();

				assertThat(context, is(valid()));
			}
			// Import-Package: org.osgi.util.tracker,org.osgi.service.io;version=1.4
		}
	}

	@Nested
	class WholeManifest {

		@Test
		void manifestVersionIsMinimalRequirement()
		{
			String value = "Manifest-Version: 1.0\n\n";
			ManifestParser parser = parserFor(value);

			ManifestContext context = parser.manifest();

			assertThat(context, is(valid()));
		}


		@Test
		void manifestWithOsgiHeaders()
		{
			String value = "Manifest-Version: 1.0\n" +
					"Bnd-LastModified: 1394131053386\n" +
					"Export-Package: com.acme.thingie;version=1.2.3\n" +
					"Bundle-SymbolicName: symnym\n\n";
			ManifestParser parser = parserFor(value);

			ManifestContext context = parser.manifest();

			assertThat(context, is(valid()));
		}


		@Test
		void wrappedLinesInManifestWithOsgiHeadersFromString()
		{
			String value = "Manifest-Version: 1.0\n" +
					"Bnd-LastModified: 1394131053386\n" +
					"Export-Package: com.acme.thingie;vers\n" +
					" ion=1.2.3\n" +
					"Bundle-SymbolicName: symnym\n\n";

			ManifestParser parser = parserFor(value);

			ManifestContext context = parser.manifest();

			assertThat(context, is(valid()));
		}


		@Test
		void wrappedLinesInManifestWithOsgiHeadersFromInputStream()
		{
			String value = "Manifest-Version: 1.0\n" +
					"Bnd-LastModified: 1394131053386\n" +
					"Export-Package: com.acme.thingie;vers\n" +
					" ion=1.2.3\n" +
					"Bundle-SymbolicName: symnym\n\n";

			ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(UTF_8));

			ManifestParser parser = parserFor(input);

			ManifestContext context = parser.manifest();

			assertThat(context, is(valid()));
		}


		@Test
		void regressionWtf()
		{
			String value = "Manifest-Version: 1.0\n" +
					"Bundle-SymbolicName: foo\n" +
					"Bundle-ManifestVersion: 2\n\n";
			ManifestParser parser = failFastParserFor(value);

			ManifestContext context = parser.manifest();

			assertThat(context, is(valid()));
		}


		@Test
		void manifestWithOsgiHeadersAndNamedEntries()
		{
			String value = "Manifest-Version: 1.0\n" +
					"Bundle-SymbolicName: non.main.attr\n" +
					"\n" +
					"Name: io/earcam/instrumental/archive/JarTest.class\n" +
					"SHA-256-Digest: 1fLT4Mh8CKkkDt7WWJsQCKOLGiKjusQxCKKKukcBhSM=\n" +
					"\n" +
					"Name: io.earcam.instrumental.archive\n" +
					"Sealed: true\n" +
					"\n";
			ManifestParser parser = parserFor(value);

			ManifestContext context = parser.manifest();

			assertThat(context, is(valid()));
		}


		@Test
		void x()
		{
			String value = "Manifest-Version: 1.0\n" +
					"Bundle-SymbolicName: org.eclipse.jdt.junit.runtime;singleton:=true\n" +
					"Archiver-Version: Plexus Archiver\n" +
					"Built-By: genie.releng\n" +
					"Require-Bundle: org.junit;bundle-version=\"3.8.2\"\n" +
					"Bundle-ManifestVersion: 2\n" +
					"Bundle-RequiredExecutionEnvironment: J2SE-1.4\n" +
					"Eclipse-SourceReferences: scm:git:git://git.eclipse.org/gitroot/jdt/ec\n" +
					" lipse.jdt.ui.git;path=\"org.eclipse.jdt.junit.runtime\";tag=\"M20171130-\n" +
					" 0510\";commitId=c2281f4e78564cfe4b9ddbe4095ea3694f80f626\n" +
					"Bundle-Vendor: %providerName\n" +
					"Export-Package: org.eclipse.jdt.internal.junit.runner; x-friends:=\"org\n" +
					" .eclipse.jdt.junit.core,  org.eclipse.jdt.junit4.runtime,  org.eclips\n" +
					" e.pde.junit.runtime,  org.eclipse.jdt.junit5.runtime\",org.eclipse.jdt\n" +
					" .internal.junit.runner.junit3;x-friends:=\"org.eclipse.jdt.junit4.runt\n" +
					" ime,org.eclipse.jdt.junit5.runtime\"\n" +
					"Bundle-Name: %pluginName\n" +
					"Bundle-Version: 3.4.651.v20171130-0906\n" +
					"Bundle-Localization: plugin\n" +
					"Created-By: Apache Maven 3.3.3\n" +
					"Build-Jdk: 1.8.0_131\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/RerunExecutionListener.cla\n" +
					" ss\n" +
					"SHA-256-Digest: UOKIsXaOyQKDKiLk3X1auHjgsTLRjnajtb/613/y3OU=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/IClassifiesThrowables.clas\n" +
					" s\n" +
					"SHA-256-Digest: kLrsuAClf8459/5XC0U+ofC0t4dyLWUwh36r2u64vfw=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/NullPrioritizer.class\n" +
					"SHA-256-Digest: 7sWcH7+dZLLic0A8Sfhivyznn/KfKEhnIszwPyoJ4k4=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/TestExecution.class\n" +
					"SHA-256-Digest: JYKk7Shfxg1U8DcHEIgeKdG1m8+ZbKSHeQv7u7Y0tT4=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/IListensToTestExecutions.c\n" +
					" lass\n" +
					"SHA-256-Digest: hGqMS/QOomTj+RlSI9rJ/mCzYU65aq2zH7M54zOCBs4=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/RemoteTestRunner.class\n" +
					"SHA-256-Digest: SkOzn6REcp/EOSjqN7KaRZvC5PIcqcnqKAfijA5dxHc=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/ITestPrioritizer.class\n" +
					"SHA-256-Digest: HDNTnp9GGbu5fhBZ5IidxdmVZJta8Kaja273uyT+gXA=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/TestIdMap.class\n" +
					"SHA-256-Digest: 2xfJ+KQZ9X+n7/sRI6AqFAJxuYhJDuNYKRjpMYndRJ4=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/DefaultClassifier.class\n" +
					"SHA-256-Digest: CWjbfB44I5mIXMibBajtOmUN2VBHJiMJwKJmH/vPWWo=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/RemoteTestRunner$ReaderThr\n" +
					" ead.class\n" +
					"SHA-256-Digest: kI+FdvVPV4qSGGiKBVvCWTK8XbbH/Vl0EgicWyyiwwI=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/CustomHashtable.class\n" +
					"SHA-256-Digest: ZSARllfnrU1WjdH0bAR5/J7p2FmgEOLONw26kgtWnHo=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/TestReferenceFailure.class\n" +
					"SHA-256-Digest: iR8uh7ZPuUVie335iV1xROjuIdIMwRg9T7QW6u+3x6A=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/junit3/JUnit3TestLoader.cl\n" +
					" ass\n" +
					"SHA-256-Digest: dCD4AyvBJUbykvd/SsWVP7r+PNsIev/3sdB1yVBV0dI=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/AbstractTestLoader.class\n" +
					"SHA-256-Digest: Vymtmbf75xL3XRaFFw463GLvZ6ia2F9WPjpQsW1d63I=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/FirstRunExecutionListener.\n" +
					" class\n" +
					"SHA-256-Digest: gJzJIF4MO4UNnaxye++bgD219cT/vdno/fbe3jQ9ciA=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/ITestLoader.class\n" +
					"SHA-256-Digest: 92daszIaFcle9VezrEihDT/rm1bNb2cyM/6Nlc5R2Bs=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/IElementComparer.class\n" +
					"SHA-256-Digest: n3525ioVB8gCGRFYg6tRfCeoYBezeQC4cJZPvrsM4Jk=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/junit3/JUnit3Listener.clas\n" +
					" s\n" +
					"SHA-256-Digest: 7fEByRz/NXhTlQixHvU6ytiKlxOvWBoWtWhoLu/5Sjo=\n" +
					"\n" +
					"Name: plugin.properties\n" +
					"SHA-256-Digest: raGbqTj6Gclrjn4Fxjg8oXF4RnTTNShdNpn5F01V6BU=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/RemoteTestRunner$RerunRequ\n" +
					" est.class\n" +
					"SHA-256-Digest: 0m0/S2KbXDvhFuC/VKAmsX6U7NK5WNgJCNvghRO1HQ4=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/JUnitMessages.properties\n" +
					"SHA-256-Digest: JSr+joM/H7xEa+7z927eQFgjo+z2nTH6wZe0fpXY9sg=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/MessageIds.class\n" +
					"SHA-256-Digest: Q35h06b1Hlwqo0+EAU1Sw7yGW9c9DKbPAcmOJ5G8Q6k=\n" +
					"\n" +
					"Name: about.html\n" +
					"SHA-256-Digest: 0RM1x9lTJa7T8YkysaegPuBIeaEvSZypRDfXoMpsJAU=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/CustomHashtable$HashEnumer\n" +
					" ator.class\n" +
					"SHA-256-Digest: +luCsrh9oxZecYJBn1109eODOS/wobWWRyoT/bQfTLo=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/ITestRunner.class\n" +
					"SHA-256-Digest: uOuEjzmCazh9t9PKXtarQnZRZTjz3q6xoXSg+raYJ6k=\n" +
					"\n" +
					"Name: .api_description\n" +
					"SHA-256-Digest: t39xHLvi1geBeUO6GFKvmRmo+v9bCXJ+disr+VvC850=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/ITestReference.class\n" +
					"SHA-256-Digest: 0OND1yHwJ8sVBP2iCJWvw/prwar+1zQa54dKU4qzUsA=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/CustomHashtable$EmptyEnume\n" +
					" rator.class\n" +
					"SHA-256-Digest: iKbbDKEQ0URLuuM0ypCxqtnbiJCNkyA5F7RcNlh0BjY=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/junit3/JUnit3TestLoader$1.\n" +
					" class\n" +
					"SHA-256-Digest: +/lmLAR9nrp8z2c5F+C7Wt8PHhUZbA3WTdCioPLC1pc=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/MessageSender.class\n" +
					"SHA-256-Digest: IgQC8kfgrOMmHfJjXVsWVHvccocsoa/BsvG/mA6X+z8=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/junit3/JUnit3TestReference\n" +
					" $1.class\n" +
					"SHA-256-Digest: EmV2T+0OB4Yi6pfANNKUWXRX7LVhrA/sRCYeVrqV2j4=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/IVisitsTestTrees.class\n" +
					"SHA-256-Digest: ydQVR7WIozmwnt/OjQ/Gmzaegzfwrnl8VlB4X7dkgZM=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/CustomHashtable$HashMapEnt\n" +
					" ry.class\n" +
					"SHA-256-Digest: 993Acso+YRGi1c6PpqV95aiVekt2rsUJ3Y8PuZ0FJx8=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/FailedComparison.class\n" +
					"SHA-256-Digest: lYyyvMvIRYESUAOzPqwqGIFLcKLfwTe/w8Z2fUE2f80=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/junit3/JUnit3Identifier.cl\n" +
					" ass\n" +
					"SHA-256-Digest: inHN10Ipa5l4feQFPd7eskFKdOR6UnyIAFmjCeIT7jI=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/FailuresFirstPrioritizer.c\n" +
					" lass\n" +
					"SHA-256-Digest: nnooShJmtBhQ4SYMKoFw53ShYWyit8mB8B9HuXyx6zA=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/junit3/JUnit3TestReference\n" +
					" .class\n" +
					"SHA-256-Digest: DWwQDhuXbHQOPwG286zsU9MppHm57tRkyCUGOe7D2+s=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/ITestIdentifier.class\n" +
					"SHA-256-Digest: MvUToPxLFD6VAUKzMQzeugKZtsvqkXLnqnnBAWsViPQ=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/JUnitMessages.class\n" +
					"SHA-256-Digest: 6AivMwvsi7ryMGkPN7fUBCu+E4BL3nZAL+dcDyMjrv8=\n" +
					"\n" +
					"Name: org/eclipse/jdt/internal/junit/runner/IStopListener.class\n" +
					"SHA-256-Digest: o6J7PqvaebnXa9AhgzgpwKCBB8JPggWwB/8JwcnptkA=\n" +
					"\n" +
					"";
			ManifestParser parser = parserFor(value);

			ManifestContext context = parser.manifest();

			assertThat(context, is(valid()));
		}

		@Nested
		class RealManifestSamples {

			private final Path baseDir = Paths.get(".", "src", "test", "resources", "manifest");


			@Test
			void osgiCore6() throws IOException
			{
				Path path = baseDir.resolve(Paths.get("org", "osgi", "org.osgi.core", "6.0.0", "MANIFEST.MF"));

				try(FileInputStream input = new FileInputStream(path.toFile())) {
					ManifestParser parser = parserFor(input);

					ManifestContext context = parser.manifest();

					assertThat(context, is(valid()));
				}
			}
		}
	}
}
