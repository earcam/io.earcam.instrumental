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

import java.util.jar.Attributes.Name;

import javax.annotation.WillNotClose;

import io.earcam.instrumental.fluent.Fluent;

import java.util.jar.Manifest;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * ManifestInfoBuilder interface.
 * </p>
 *
 */
public interface ManifestInfoBuilder<T extends ManifestInfoBuilder<T>> {

	public static class ManifestNamedEntry {
		private final String name;
		private final Map<Name, CharSequence> attributes = new HashMap<>();


		private ManifestNamedEntry(String name)
		{
			this.name = name;
		}


		@Fluent
		public static ManifestNamedEntry entry(CharSequence name)
		{
			return new ManifestNamedEntry(name.toString());
		}


		public ManifestNamedEntry attribute(CharSequence key, CharSequence value)
		{
			return attribute(new Name(key.toString()), value);
		}


		public ManifestNamedEntry attribute(Name key, CharSequence value)
		{
			attributes.put(key, value);
			return this;
		}


		public String name()
		{
			return name;
		}


		public Map<Name, CharSequence> attributes()
		{
			return attributes;
		}
	}


	/**
	 * <p>
	 * attribute.
	 * </p>
	 *
	 * @param key a {@link java.lang.CharSequence} object.
	 * @param value a {@link java.lang.CharSequence} object.
	 * @return a {@link java.util.Map.Entry} object.
	 */
	@Fluent
	public static Map.Entry<Name, CharSequence> attribute(CharSequence key, CharSequence value)
	{
		return attribute(new Name(key.toString()), value);
	}


	/**
	 * <p>
	 * attribute.
	 * </p>
	 *
	 * @param key a {@link java.util.jar.Attributes.Name} object.
	 * @param value a {@link java.lang.CharSequence} object.
	 * @return a {@link java.util.Map.Entry} object.
	 */
	@Fluent
	public static Map.Entry<Name, CharSequence> attribute(Name key, CharSequence value)
	{
		return new AbstractMap.SimpleEntry<>(key, value);
	}


	/**
	 * <p>
	 * manifestMain.
	 * </p>
	 *
	 * @param attribute a {@link java.util.Map.Entry} object.
	 * @return a T object.
	 */
	public abstract T manifestMain(Map.Entry<Name, ? extends CharSequence> attribute);


	/**
	 * <p>
	 * manifestNamed.
	 * </p>
	 *
	 * @param entry a {@link io.earcam.instrumental.module.manifest.ManifestInfoBuilder.ManifestNamedEntry} object.
	 * @return a T object.
	 */
	public abstract T manifestNamed(ManifestNamedEntry entry);


	/**
	 * <p>
	 * mergeFrom.
	 * </p>
	 *
	 * @param manifest a {@link java.util.jar.Manifest} object.
	 * @return a T object.
	 */
	public abstract T mergeFrom(Manifest manifest);


	/**
	 * <p>
	 * toManifest.
	 * </p>
	 *
	 * @return a {@link java.util.jar.Manifest} object.
	 */
	public default Manifest toManifest()
	{
		Manifest manifest = new Manifest();
		return to(manifest);
	}


	/**
	 * <p>
	 * to.
	 * </p>
	 *
	 * @param manifest a {@link java.util.jar.Manifest} object.
	 * @return a {@link java.util.jar.Manifest} object.
	 */
	public abstract Manifest to(Manifest manifest);


	/**
	 * <p>
	 * to.
	 * </p>
	 *
	 * @param out a {@link java.io.OutputStream} object.
	 */
	public abstract void to(@WillNotClose OutputStream out);
}
