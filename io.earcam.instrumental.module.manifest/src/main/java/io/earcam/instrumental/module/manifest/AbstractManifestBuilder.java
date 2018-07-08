/*-
 * #%L
 * io.earcam.instrumental.module.manifest
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
package io.earcam.instrumental.module.manifest;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import javax.annotation.WillNotClose;

import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * Abstract AbstractManifestBuilder class.
 * </p>
 *
 */
public abstract class AbstractManifestBuilder<T extends ManifestInfoBuilder<T>> implements ManifestInfo, ManifestInfoBuilder<T> {

	protected final Map<Name, CharSequence> mainAttributes = new HashMap<>();
	protected final Map<String, Map<Name, CharSequence>> namedEntries = new HashMap<>();

	protected boolean hooked = false;


	/**
	 * <p>
	 * self.
	 * </p>
	 *
	 * @return a T object.
	 */
	protected abstract T self();


	/** {@inheritDoc} */
	@Override
	public boolean equals(Object other)
	{
		return other instanceof AbstractManifestBuilder && equals(AbstractManifestBuilder.class.cast(other));
	}


	/**
	 * <p>
	 * Implementers should use check own properties, invoke {@code instanceof} and then delegate to
	 * {@link #same(AbstractManifestBuilder)}
	 * </p>
	 *
	 * @param that an {@link AbstractManifestBuilder} instance.
	 * @see #same(AbstractManifestBuilder)
	 * @return a boolean.
	 */
	public abstract boolean equals(AbstractManifestBuilder<?> that);


	/**
	 * <p>
	 * same.
	 * </p>
	 *
	 * @param that a {@link AbstractManifestBuilder} object.
	 * @return a boolean.
	 */
	protected final boolean same(AbstractManifestBuilder<?> that)
	{
		return that != null
				&& Objects.equals(that.mainAttributes, mainAttributes)
				&& Objects.equals(that.namedEntries, namedEntries);
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		return Objects.hash(mainAttributes, namedEntries);
	}


	/** {@inheritDoc} */
	@Override
	public T manifestMain(Entry<Name, ? extends CharSequence> attribute)
	{
		mainAttributes.put(attribute.getKey(), attribute.getValue());
		return self();
	}


	/** {@inheritDoc} */
	@Override
	public T manifestNamed(ManifestNamedEntry entry)
	{
		Map<Name, CharSequence> attributes = namedEntries.computeIfAbsent(entry.name(), n -> new HashMap<>());
		attributes.putAll(entry.attributes());
		return self();
	}


	/** {@inheritDoc} */
	@Override
	public T mergeFrom(Manifest manifest)
	{
		for(Object entry : manifest.getMainAttributes().entrySet()) {
			@SuppressWarnings("unchecked")
			Entry<Name, String> x = (Entry<Name, String>) entry;
			manifestMain(x);
		}

		for(Map.Entry<String, Attributes> namedEntry : manifest.getEntries().entrySet()) {
			ManifestNamedEntry x = ManifestNamedEntry.entry(namedEntry.getKey());
			for(Entry<Object, Object> entry : namedEntry.getValue().entrySet()) {
				x.attribute((Name) entry.getKey(), (String) entry.getValue());
			}
			manifestNamed(x);
		}
		return self();
	}


	/** {@inheritDoc} */
	@Override
	public Manifest to(Manifest manifest)
	{
		hook();
		mainAttributes.entrySet().forEach(e -> manifest.getMainAttributes().putIfAbsent(e.getKey(), e.getValue().toString()));

		Map<String, Attributes> entries = manifest.getEntries();
		for(Entry<String, Map<Name, CharSequence>> named : namedEntries.entrySet()) {

			Attributes sink = entries.computeIfAbsent(named.getKey(), n -> new Attributes());
			for(Entry<Name, CharSequence> source : named.getValue().entrySet()) {
				sink.put(source.getKey(), source.getValue().toString());
			}
		}
		return manifest;
	}


	/**
	 * <p>
	 * hook.
	 * </p>
	 */
	protected final void hook()
	{
		if(!hooked) {
			preBuildHook();
			hooked = true;
			preFlightChecks();
		}
	}


	private void preFlightChecks()
	{
		if(!(mainAttributes.containsKey(Name.MANIFEST_VERSION) || mainAttributes.containsKey(Name.SIGNATURE_VERSION))) {
			mainAttributes.put(Name.MANIFEST_VERSION, "1.0");
		}
	}


	/**
	 * <p>
	 * preBuildHook.
	 * </p>
	 */
	protected abstract void preBuildHook();


	/**
	 * <p>
	 * unhook.
	 * </p>
	 */
	protected final void unhook()
	{
		hooked = false;
		// landingChecks
	}


	/** {@inheritDoc} */
	@Override
	public void to(@WillNotClose OutputStream out)
	{
		hook();
		Exceptional.accept(toManifest()::write, out);
	}


	/**
	 * <p>
	 * mainAttributes.
	 * </p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<Name, CharSequence> mainAttributes()
	{
		return mainAttributes;
	}


	/**
	 * <p>
	 * namedEntries.
	 * </p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, Map<Name, CharSequence>> namedEntries()
	{
		return namedEntries;
	}
}
