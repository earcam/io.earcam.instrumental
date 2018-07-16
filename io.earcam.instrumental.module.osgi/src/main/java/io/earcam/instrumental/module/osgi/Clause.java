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

import static io.earcam.instrumental.fluent.Role.AUX;
import static io.earcam.instrumental.module.osgi.ClauseParameters.EMPTY_PARAMETERS;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.ParametersAreNonnullByDefault;

import io.earcam.instrumental.fluent.Fluent;
import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * Clause class.
 * </p>
 *
 */
@ParametersAreNonnullByDefault
public class Clause {

	private final SortedSet<String> uniqueNames;
	private final ClauseParameters parameters;


	/**
	 * <p>
	 * Constructor for Clause.
	 * </p>
	 *
	 * @param uniqueName a {@link java.lang.String} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 */
	public Clause(String uniqueName, ClauseParameters parameters)
	{
		uniqueNames = new TreeSet<>();
		uniqueNames.add(uniqueName);
		this.parameters = parameters;
	}


	/**
	 * <p>
	 * Constructor for Clause.
	 * </p>
	 *
	 * @param uniqueNames a {@link java.util.SortedSet} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 */
	public Clause(SortedSet<String> uniqueNames, ClauseParameters parameters)
	{
		this.uniqueNames = uniqueNames;
		this.parameters = parameters;
	}


	/**
	 * <p>
	 * clause.
	 * </p>
	 *
	 * @param uniqueName a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.Clause} object.
	 */
	public static Clause clause(String uniqueName)
	{
		return clause(uniqueName, EMPTY_PARAMETERS);
	}


	/**
	 * <p>
	 * clause.
	 * </p>
	 *
	 * @param uniqueName a {@link java.lang.String} object.
	 * @param parameters a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.Clause} object.
	 */
	public static Clause clause(String uniqueName, ClauseParameters parameters)
	{
		return new Clause(uniqueName, parameters);
	}


	/**
	 * <p>
	 * sortedSet.
	 * </p>
	 *
	 * @param elements a T object.
	 * @param <T> a T object.
	 * @return a {@link java.util.SortedSet} object.
	 */
	@Fluent(role = AUX)
	public static SortedSet<String> sortedSet(String... elements)
	{
		return new TreeSet<>(Arrays.asList(elements));
	}


	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return Exceptional.apply(this::appendTo, new StringBuilder()).toString();
	}


	/** {@inheritDoc} */
	@Override
	public boolean equals(Object other)
	{
		return other instanceof Clause && equals((Clause) other);
	}


	/**
	 * <p>
	 * equals.
	 * </p>
	 *
	 * @param that a {@link io.earcam.instrumental.module.osgi.Clause} object.
	 * @return a boolean.
	 */
	@SuppressWarnings("squid:S2589")  // SonarQube false positive - "that" could clearly be null
	public boolean equals(Clause that)
	{
		return that != null
				// && that.uniqueNames().equals(this.uniqueNames)
				&& that.uniqueNames().size() == this.uniqueNames.size()
				&& that.uniqueNames().containsAll(this.uniqueNames)
				&& that.parameters().equals(this.parameters);
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		return Objects.hash(uniqueNames, parameters);
	}


	/**
	 * <p>
	 * uniqueNames.
	 * </p>
	 *
	 * @return a {@link java.util.SortedSet} object.
	 */
	public SortedSet<String> uniqueNames()
	{
		return uniqueNames;
	}


	/**
	 * <p>
	 * parameters.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.osgi.ClauseParameters} object.
	 */
	public ClauseParameters parameters()
	{
		return parameters;
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
		appendix.append(uniqueNames().stream().collect(joining(";")));
		return parameters.appendTo(appendix);
	}


	public static String allToString(List<Clause> clauses)
	{
		StringBuilder santa = new StringBuilder();
		for(int i = 0; i < clauses.size(); i++) {
			Exceptional.accept(clauses.get(i)::appendTo, santa);
			if(i < clauses.size() - 1) {
				santa.append(',');
			}
		}
		return santa.toString();
	}
}
