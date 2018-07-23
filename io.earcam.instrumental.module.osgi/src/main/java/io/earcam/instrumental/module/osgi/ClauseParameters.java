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

import static java.util.Collections.emptyMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.osgi.framework.Constants;

import io.earcam.instrumental.fluent.Fluent;
import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * ClauseParameters class.
 * </p>
 *
 * @see ClauseParameter for construction
 */
public final class ClauseParameters {

	public static final class ClauseParameter {

		private ClauseParameter()
		{}


		@Fluent
		public static ClauseParameters directive(String key, String value)
		{
			ClauseParameters parameters = new ClauseParameters();
			parameters.directive(key, value);
			return parameters;
		}


		@Fluent
		public static ClauseParameters attribute(String key, String value)
		{
			ClauseParameters parameters = new ClauseParameters();
			parameters.attribute(key, value);
			return parameters;
		}


		@Fluent
		public static ClauseParameters version(int major, int minor, int micro)
		{
			ClauseParameters parameters = new ClauseParameters();
			parameters.attribute(Constants.VERSION_ATTRIBUTE, major + "." + minor + "." + micro);
			return parameters;
		}
	}

	/**
	 * Empty parameters, immutably so
	 */
	public static final ClauseParameters EMPTY_PARAMETERS = new ClauseParameters(emptyMap(), emptyMap());

	private final Map<String, String> attributes;
	private final Map<String, String> directives;


	/**
	 * <p>
	 * Constructor for ClauseParameters.
	 * </p>
	 */
	public ClauseParameters()
	{
		this(new HashMap<>(), new HashMap<>());
	}


	/**
	 * <p>
	 * Constructor for ClauseParameters.
	 * </p>
	 *
	 * @param attributes a {@link java.util.Map} object.
	 * @param directives a {@link java.util.Map} object.
	 * @see ClauseParameter#attribute(String, String)
	 * @see ClauseParameter#directive(String, String)
	 * @see ClauseParameter#attribute(String, String)
	 * @see ClauseParameter#directive(String, String)
	 */
	public ClauseParameters(Map<String, String> attributes, Map<String, String> directives)
	{
		this.attributes = attributes;
		this.directives = directives;
	}


	@Override
	public String toString()
	{
		return Exceptional.apply(this::appendTo, new StringBuilder()).toString();
	}


	@Override
	public boolean equals(Object other)
	{
		return other instanceof ClauseParameters && equals((ClauseParameters) other);
	}


	/**
	 * <p>
	 * equals.
	 * </p>
	 *
	 * @param that a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a boolean.
	 */
	public boolean equals(ClauseParameters that)
	{
		return that != null
				&& that.attributes.equals(this.attributes)
				&& that.directives.equals(this.directives);
	}


	@Override
	public int hashCode()
	{
		return Objects.hash(attributes, directives);
	}


	/**
	 * <p>
	 * isEmpty.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public boolean isEmpty()
	{
		return attributes.isEmpty() && directives.isEmpty();
	}


	/**
	 * <p>
	 * directives.
	 * </p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, String> directives()
	{
		return directives;
	}


	/**
	 * <p>
	 * directive.
	 * </p>
	 *
	 * @param key a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 */
	public ClauseParameters directive(String key, String value)
	{
		directives.put(key, value);
		return this;
	}


	/**
	 * <p>
	 * attributes.
	 * </p>
	 *
	 * @return a modifiable {@link Map}.
	 */
	public Map<String, String> attributes()
	{
		return attributes;
	}


	/**
	 * <p>
	 * attribute.
	 * </p>
	 *
	 * @param key a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 */
	public ClauseParameters attribute(String key, String value)
	{
		attributes.put(key, value);
		return this;
	}


	/**
	 * <p>
	 * appendTo.
	 * </p>
	 *
	 * @param appendix a {@link java.lang.Appendable} object.
	 * @return a {@link java.lang.Appendable} object.
	 * @throws java.io.IOException if any.
	 */
	public Appendable appendTo(Appendable appendix) throws IOException
	{
		appendTo(appendix, attributes, "=");
		appendTo(appendix, directives, ":=");
		return appendix;
	}


	private void appendTo(Appendable appendage, Map<String, String> source, String symbol) throws IOException
	{
		if(!source.isEmpty()) {
			appendage.append(';');
			for(Iterator<Entry<String, String>> it = source.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> entry = it.next();
				appendage.append(entry.getKey()).append(symbol).append(entry.getValue());
				if(it.hasNext()) {
					appendage.append(';');
				}
			}
		}
	}


	/**
	 * <p>
	 * attributeOrDefault.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param defaultValue a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public String attributeOrDefault(String name, String defaultValue)
	{
		String value = attribute(name);
		return (value == null) ? defaultValue : value;
	}


	/**
	 * <p>
	 * attribute.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public String attribute(String name)
	{
		return attributes().get(name);
	}
}
