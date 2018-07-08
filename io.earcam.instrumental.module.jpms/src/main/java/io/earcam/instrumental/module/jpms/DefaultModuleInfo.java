/*-
 * #%L
 * io.earcam.instrumental.module.jpms
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
package io.earcam.instrumental.module.jpms;

import static io.earcam.instrumental.module.jpms.RequireModifier.STATIC;
import static io.earcam.instrumental.module.jpms.RequireModifier.TRANSITIVE;
import static java.util.Arrays.stream;
import static java.util.EnumSet.of;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

class DefaultModuleInfo implements ModuleInfo, ModuleInfoBuilder, Serializable {

	private static final long serialVersionUID = 8059255940493321393L;
	private static final EnumSet<RequireModifier> MUTUALLY_EXCLUSIVE_REQUIRE_MODIFIERS = of(TRANSITIVE, STATIC);
	private String name;
	private String version;
	private int access;

	private TreeSet<CharSequence> packages = new TreeSet<>();
	private TreeSet<String> uses = new TreeSet<>();
	private HashMap<String, String[]> provides = new HashMap<>();
	private String mainClass;
	private HashSet<Require> requires = new HashSet<>();
	private HashSet<Export> exports = new HashSet<>();
	private HashSet<Export> opens = new HashSet<>();


	/** {@inheritDoc} */
	@Override
	public boolean equals(Object other)
	{
		return other instanceof ModuleInfo && equals((ModuleInfo) other);
	}


	/**
	 * <p>
	 * equals.
	 * </p>
	 *
	 * @param that a {@link io.earcam.instrumental.module.jpms.ModuleInfo} object.
	 * @return a boolean.
	 */
	public boolean equals(ModuleInfo that)
	{
		return that != null
				&& Objects.equals(that.name(), name)
				&& Objects.equals(that.version(), version)
				&& Objects.equals(that.access(), access)
				&& Objects.equals(that.packages(), packages)
				&& Objects.equals(that.uses(), uses)
				&& deepEqualsMap(that.provides(), provides)
				&& Objects.equals(that.mainClass(), mainClass)
				&& Objects.equals(that.requires(), requires)
				&& Objects.equals(that.exports(), exports)
				&& Objects.equals(that.opens(), opens);
	}


	private static boolean deepEqualsMap(Map<String, String[]> a, Map<String, String[]> b)
	{
		if(Objects.deepEquals(a.keySet(), b.keySet())) {
			Iterator<String[]> aIt = a.values().iterator();
			Iterator<String[]> bIt = b.values().iterator();
			while(aIt.hasNext()) {
				if(!Arrays.equals(aIt.next(), bIt.next())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		return Objects.hash(
				name, version, packages,
				uses, provides.keySet(), mainClass,
				requires, exports, opens);
	}


	/** {@inheritDoc} */
	@Override
	public String name()
	{
		return name;
	}


	/** {@inheritDoc} */
	@Override
	public String version()
	{
		return version;
	}


	/** {@inheritDoc} */
	@Override
	public SortedSet<CharSequence> packages()
	{
		return packages;
	}


	/** {@inheritDoc} */
	@Override
	public SortedSet<String> uses()
	{
		return uses;
	}


	/** {@inheritDoc} */
	@Override
	public Map<String, String[]> provides()
	{
		return provides;
	}


	/** {@inheritDoc} */
	@Override
	public String mainClass()
	{
		return mainClass;
	}


	/** {@inheritDoc} */
	@Override
	public Set<Require> requires()
	{
		return requires;
	}


	/** {@inheritDoc} */
	@Override
	public Set<Export> exports()
	{
		return exports;
	}


	/** {@inheritDoc} */
	@Override
	public Set<Export> opens()
	{
		return opens;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfoBuilder named(String moduleName)
	{
		name = moduleName;
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfoBuilder versioned(String moduleVersion)
	{
		version = moduleVersion;
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfoBuilder withAccess(int accessFlags)
	{
		access = accessFlags;
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfoBuilder packaging(CharSequence paquet)
	{
		packages.add(paquet.toString());
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfoBuilder using(String serviceApi)
	{
		uses.add(serviceApi);
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfoBuilder providing(String contract, String... concretes)
	{
		provides.put(contract, concretes);
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfoBuilder launching(String mainClass)
	{
		this.mainClass = mainClass;
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfoBuilder requiring(String module, int access, String version)
	{
		requires.add(new Require(module, access, version));
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfoBuilder exporting(String paquet, int access, String... modules)
	{
		exports.add(new Export(paquet, access, modules));
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfoBuilder opening(String paquet, int access, String... modules)
	{
		opens.add(new Export(paquet, access, modules));
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfo construct()
	{
		// TODO check validate - expose a separate method? javax.validation?
		Objects.requireNonNull(name, "name");

		List<Require> erroneousRequires = requires.stream()
				.filter(r -> r.modifiers().containsAll(MUTUALLY_EXCLUSIVE_REQUIRE_MODIFIERS))
				.collect(toList());
		if(!erroneousRequires.isEmpty()) {
			throw new IllegalStateException("require statements contain mutually exclusive modifiers: " +
					erroneousRequires.stream().map(Require::module).collect(joining(", ")));
		}
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public ModuleInfoBuilder deconstruct()
	{
		return this;
	}


	/**
	 * {@inheritDoc}
	 *
	 * Comments are added, because although you can add annotations to the
	 * module declaration, you can't to it's elements.
	 */
	@Override
	public String toString()
	{
		StringBuilder output = new StringBuilder();

		addModuleComment(output);

		output.append(visibleModifiers(modifiers()));
		output.append("module ").append(name).append(" {\n");

		for(Require require : requires) {
			addComment(output, "\t", require.modifiers(), require.version());
			output.append('\t').append("requires ");
			output.append(visibleModifiers(require.modifiers()));
			output.append(require.module()).append(";\n");
		}

		final String listJoiningDelimiter = ",\n\t\t";
		for(Export export : exports) {
			addComment(output, "\t", export.modifiers());
			output.append('\t').append("exports ").append(export.paquet());
			if(export.modules().length > 0) {
				output.append(stream(export.modules()).collect(joining(listJoiningDelimiter, " to \n\t\t", "")));
			}
			output.append(";\n");
		}

		for(Export export : opens) {
			addComment(output, "\t", export.modifiers());
			output.append('\t').append("opens ").append(export.paquet());
			if(export.modules().length > 0) {
				output.append(stream(export.modules()).collect(joining(listJoiningDelimiter, " to \n\t\t", "")));
			}
			output.append(";\n");
		}
		if(!uses.isEmpty()) {
			output.append(uses.stream().collect(Collectors.joining(";\n\tuses ", "\tuses ", ";\n")));
		}

		for(Map.Entry<String, String[]> e : provides.entrySet()) {
			output.append('\t').append("provides ").append(e.getKey())
					.append(Arrays.stream(e.getValue()).collect(joining(listJoiningDelimiter, " with \n\t\t", "")));
			output.append(";\n");
		}
		return output.append('}').toString();
	}


	private String visibleModifiers(Set<? extends Modifier> modifiers)
	{
		String mods = modifiers.stream()
				.filter(Modifier::sourceVisible)
				.map(Modifier::name)
				.map(m -> m.toLowerCase(ROOT))
				.collect(joining(" "));

		return (mods.length() > 0) ? mods + ' ' : mods;
	}


	private void addModuleComment(StringBuilder output)
	{
		if(version != null || access != 0 || !packages.isEmpty()) {
			startComment(output, "");
			versionComment(output, "", version);
			accessComment(output, "", Access.modifiers(ModuleModifier.class, access));
			packagesComment(output, packages);
			endComment(output, "");
		}
	}


	private static void packagesComment(StringBuilder output, Set<CharSequence> packages)
	{
		packages.forEach(p -> output.append(" * @package ").append(p).append('\n'));
	}


	private static <M extends Enum<M> & Modifier> void addComment(StringBuilder output, String indent, Set<M> modifiers)
	{
		if(!modifiers.isEmpty()) {
			startComment(output, indent);
			accessComment(output, indent, modifiers);
			endComment(output, indent);
		}
	}


	private static void addComment(StringBuilder output, String indent, Set<RequireModifier> modifiers, String version)
	{
		if(version != null || !modifiers.isEmpty()) {
			startComment(output, indent);
			versionComment(output, indent, version);
			accessComment(output, indent, modifiers);
			endComment(output, indent);
		}
	}


	private static void startComment(StringBuilder output, String indent)
	{
		output.append(indent).append("/**\n");
	}


	private static void versionComment(StringBuilder output, String indent, String version)
	{
		if(version != null) {
			output.append(indent).append(" * @version ").append(version).append('\n');
		}
	}


	private static void accessComment(StringBuilder output, String indent, Set<? extends Modifier> modifiers)
	{
		if(!modifiers.isEmpty()) {
			output.append(indent).append(" * @modifiers ")
					.append(modifiers.stream()
							.map(Modifier::name)
							.map(m -> m.toLowerCase(ROOT))
							.collect(joining(", ")))
					.append('\n');
		}
	}


	private static void endComment(StringBuilder output, String indent)
	{
		output.append(indent).append(" */\n");
	}


	/** {@inheritDoc} */
	@Override
	public byte[] toBytecode()
	{
		return BytecodeWriter.toBytecode(this);
	}


	/** {@inheritDoc} */
	@Override
	public int access()
	{
		return access;
	}
}
