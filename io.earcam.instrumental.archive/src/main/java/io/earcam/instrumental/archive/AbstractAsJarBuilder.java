/*-
 * #%L
 * io.earcam.instrumental.archive
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
package io.earcam.instrumental.archive;

import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.jar.Attributes.Name.SEALED;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import io.earcam.instrumental.reflect.Types;
import io.earcam.unexceptional.Exceptional;

// TODO 
// 1. Should apply checks at build time to allow e.g. ..compiler integration (this could also make validation optional... for better or worse, allows testing with "bad" jars).
/**
 * AsJar, configures an {@link Archive} as a JAR.
 */
public abstract class AbstractAsJarBuilder<T extends AsJarBuilder<T>>
		implements AsJarBuilder<T>, ArchiveConfigurationPlugin, ManifestProcessor, ArchiveResourceListener, ArchiveResourceSource {

	public static final String MANIFEST_VERSION = Name.MANIFEST_VERSION.toString();
	public static final String MAIN_CLASS = Name.MAIN_CLASS.toString();
	public static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
	public static final String SPI_ROOT_PATH = "META-INF/services/";
	public static final String CREATED_BY = "Created-By";
	public static final String V1_0 = "1.0";

	protected final BasicArchiveResourceSource source = new BasicArchiveResourceSource();

	protected final Manifest manifest = new Manifest();

	private final Map<String, Set<String>> spi = new HashMap<>();

	private Predicate<String> packageMatcher = p -> false;
	private Set<String> sealedPackages = new HashSet<>();


	/**
	 * <p>
	 * Constructor for AsJar.
	 * </p>
	 */
	protected AbstractAsJarBuilder()
	{
		manifest.getMainAttributes().putValue(MANIFEST_VERSION, V1_0);
	}


	protected abstract T self();


	@OverridingMethodsMustInvokeSuper
	@Override
	public void added(ArchiveResource resource)
	{
		if(packageMatcher.test(resource.pkg())) {
			sealedPackages.add(resource.pkg());
		}
	}


	@OverridingMethodsMustInvokeSuper
	@Override
	public void process(Manifest manifest)
	{
		merge(this.manifest, manifest);

		Map<String, Attributes> entries = manifest.getEntries();

		for(String sealed : sealedPackages) {
			entries.computeIfAbsent(sealed, k -> new Attributes())
					.put(SEALED, Boolean.TRUE.toString());
		}
	}


	@OverridingMethodsMustInvokeSuper
	@Override
	public void attach(ArchiveRegistrar core)
	{
		core.registerResourceListener(this);
		core.registerManifestProcessor(this);
		core.registerResourceSource(this);
	}


	@OverridingMethodsMustInvokeSuper
	@Override
	public Stream<ArchiveResource> drain(ResourceSourceLifecycle stage)
	{
		Stream<ArchiveResource> drained = source.drain(stage);

		if(stage == ResourceSourceLifecycle.PRE_MANIFEST) {
			List<ArchiveResource> spiSources = new ArrayList<>();
			for(Map.Entry<String, Set<String>> e : spi.entrySet()) {
				byte[] bytes = spiFileContent(e.getValue());
				spiSources.add(new ArchiveResource(SPI_ROOT_PATH + e.getKey(), bytes));
			}
			drained = Stream.concat(drained, spiSources.stream());
		}
		return drained;
	}


	static byte[] spiFileContent(Collection<String> implementors)
	{
		return implementors.stream().collect(joining("\r\n")).getBytes(UTF_8);
	}


	/**
	 * AsJar API **
	 *
	 * @param mainClass a {@link java.lang.Class} object.
	 * @return a {@link io.earcam.instrumental.archive.AbstractAsJarBuilder} object.
	 */
	@Override
	public T launching(Class<?> mainClass)
	{
		Exceptional.accept(AbstractAsJarBuilder::requireMainMethod, mainClass);
		source.with(mainClass);
		return launching(mainClass.getCanonicalName());
	}


	@Override
	public T launching(String mainClass)
	{
		manifest.getMainAttributes().putValue(MAIN_CLASS, mainClass);
		return self();
	}


	protected static void requireMainMethod(Class<?> type)
	{
		Method main = Exceptional.apply(type::getMethod, "main", String[].class);
		if(!isPublicStaticVoid(main)) {
			throw new IllegalArgumentException("'public static void main(String[] args)' method not found on " + type);
		}
	}


	/**
	 * <p>
	 * isPublicStaticVoid.
	 * </p>
	 *
	 * @param method a {@link java.lang.reflect.Method} object.
	 * @return a boolean.
	 */
	protected static boolean isPublicStaticVoid(Method method)
	{
		return isPublicStatic(method) && method.getReturnType().equals(void.class);
	}


	private static boolean isPublicStatic(Method method)
	{
		return (method.getModifiers() & (PUBLIC | STATIC)) == (PUBLIC | STATIC);
	}


	/**
	 * <p>
	 * sealing.
	 * </p>
	 *
	 * @param packageMatcher a {@link java.util.function.Predicate} object.
	 * @return this builder.
	 */
	@Override
	public T sealing(Predicate<String> packageMatcher)
	{
		this.packageMatcher = this.packageMatcher.or(packageMatcher);
		return self();
	}


	/**
	 * <p>
	 * providing.
	 * </p>
	 *
	 * @param service a {@link java.lang.Class} object.
	 * @param implementations a {@link java.util.List} object.
	 * @return this builder.
	 */
	@Override
	public T providing(Class<?> service, List<Class<?>> implementations)
	{
		implementations.forEach(i -> requireImplements(i, service));
		implementations.forEach(source::with);

		Set<String> imps = implementations.stream()
				.map(Class::getCanonicalName)
				.collect(toSet());
		return providing(service.getCanonicalName(), imps);
	}


	private static void requireImplements(Class<?> implementor, Class<?> implementee)
	{
		if(!Types.implementsAll(implementor, implementee)) {
			throw new IllegalArgumentException(implementor + " does not implement " + implementee);
		}
	}


	@Override
	public T providing(String service, Set<String> implementations)
	{
		Set<String> imps = spi.computeIfAbsent(service, x -> new TreeSet<>());
		imps.addAll(implementations);
		return self();
	}


	/**
	 * @param manifest the manifest to merge from
	 * @return this builder.
	 */
	@Override
	public T mergingManifest(Manifest manifest)
	{
		merge(manifest, this.manifest);
		return self();
	}


	protected static void merge(Manifest sauce, Manifest sink)
	{
		sink.getMainAttributes().putAll(sauce.getMainAttributes());
		sauce.getEntries().forEach((k, v) -> sink.getEntries().merge(k, v, (v1, v2) -> {
			v1.putAll(v2);
			return v1;
		}));
	}


	@Override
	public T withManifestHeader(String key, String value)
	{
		manifest.getMainAttributes().putValue(key, value);
		return self();
	}
}
