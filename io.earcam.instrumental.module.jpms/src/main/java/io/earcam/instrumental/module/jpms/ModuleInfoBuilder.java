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

import static io.earcam.instrumental.module.jpms.Access.ACC_MANDATED;

import java.util.Set;
import java.util.SortedSet;

// TODO parse and build: annotations and import statements
/**
 * ModuleInfo builder.
 */
public interface ModuleInfoBuilder {

	/**
	 * <p>
	 * named.
	 * </p>
	 *
	 * @param moduleName a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public abstract ModuleInfoBuilder named(String moduleName);


	/**
	 * <p>
	 * versioned.
	 * </p>
	 *
	 * @param moduleVersion a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public abstract ModuleInfoBuilder versioned(String moduleVersion);


	/**
	 * <p>
	 * withAccess.
	 * </p>
	 *
	 * @param accessFlags a int.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public abstract ModuleInfoBuilder withAccess(int accessFlags);


	/**
	 * <p>
	 * withAccess.
	 * </p>
	 *
	 * @param modifiers a {@link java.util.Set} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder withAccess(Set<ModuleModifier> modifiers)
	{
		return withAccess(Access.access(modifiers));
	}


	/**
	 * <p>
	 * packaging.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.CharSequence} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public abstract ModuleInfoBuilder packaging(CharSequence paquet);


	/**
	 * <p>
	 * packaging.
	 * </p>
	 *
	 * @param packages a {@link java.util.SortedSet} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder packaging(SortedSet<CharSequence> packages)
	{
		packages.forEach(this::packaging);
		return this;
	}


	/**
	 * <p>
	 * using.
	 * </p>
	 *
	 * @param serviceApi a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public abstract ModuleInfoBuilder using(String serviceApi);


	/**
	 * <p>
	 * using.
	 * </p>
	 *
	 * @param services a {@link java.util.SortedSet} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder using(SortedSet<String> services)
	{
		services.forEach(this::using);
		return this;
	}


	/**
	 * <p>
	 * providing.
	 * </p>
	 *
	 * @param contract a {@link java.lang.String} object.
	 * @param concretes a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public abstract ModuleInfoBuilder providing(String contract, String... concretes);


	/**
	 * <p>
	 * providing.
	 * </p>
	 *
	 * @param contract a {@link java.lang.String} object.
	 * @param concretes a {@link java.util.SortedSet} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder providing(String contract, Set<String> concretes)
	{
		return providing(contract, concretes.toArray(new String[concretes.size()]));
	}


	/**
	 * <p>
	 * launching.
	 * </p>
	 *
	 * @param mainClass a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public abstract ModuleInfoBuilder launching(String mainClass);


	/**
	 * <p>
	 * requiring.
	 * </p>
	 *
	 * @param module a {@link java.lang.String} object.
	 * @param access a int.
	 * @param version a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public abstract ModuleInfoBuilder requiring(String module, int access, String version);


	/**
	 * <p>
	 * requiring.
	 * </p>
	 *
	 * @param module a {@link java.lang.String} object.
	 * @param modifiers a {@link java.util.Set} object.
	 * @param version a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder requiring(String module, Set<RequireModifier> modifiers, String version)
	{
		return requiring(module, Access.access(modifiers), version);
	}


	/**
	 * <p>
	 * requiring.
	 * </p>
	 *
	 * @param module a {@link io.earcam.instrumental.module.jpms.ModuleInfo} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder requiring(ModuleInfo module)
	{
		return requiring(module.name(), ACC_MANDATED, module.version());
	}


	/**
	 * <p>
	 * exporting.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param access a int.
	 * @param modules a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public abstract ModuleInfoBuilder exporting(String paquet, int access, String... modules);


	/**
	 * <p>
	 * exporting.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param modifiers a {@link java.util.Set} object.
	 * @param modules a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder exporting(String paquet, Set<ExportModifier> modifiers, String... modules)
	{
		return exporting(paquet, Access.access(modifiers), modules);
	}


	/**
	 * <p>
	 * exporting.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param modules a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder exporting(String paquet, String... modules)
	{
		return exporting(paquet, ACC_MANDATED, modules);
	}


	/**
	 * <p>
	 * exporting.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param modifiers a {@link java.util.Set} object.
	 * @param modules a {@link java.util.SortedSet} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder exporting(String paquet, Set<ExportModifier> modifiers, SortedSet<String> modules)
	{
		return exporting(paquet, modifiers, modules.toArray(new String[modules.size()]));
	}


	/**
	 * <p>
	 * opening.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param access a int.
	 * @param modules a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public abstract ModuleInfoBuilder opening(String paquet, int access, String... modules);


	/**
	 * <p>
	 * opening.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param modifiers a {@link java.util.Set} object.
	 * @param modules a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder opening(String paquet, Set<ExportModifier> modifiers, String... modules)
	{
		return opening(paquet, Access.access(modifiers), modules);
	}


	/**
	 * <p>
	 * opening.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param modules a {@link java.lang.String} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder opening(String paquet, String... modules)
	{
		return opening(paquet, ACC_MANDATED, modules);
	}


	/**
	 * <p>
	 * opening.
	 * </p>
	 *
	 * @param paquet a {@link java.lang.String} object.
	 * @param modifiers a {@link java.util.Set} object.
	 * @param modules a {@link java.util.SortedSet} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public default ModuleInfoBuilder opening(String paquet, Set<ExportModifier> modifiers, SortedSet<String> modules)
	{
		return opening(paquet, modifiers, modules.toArray(new String[modules.size()]));
	}


	/**
	 * <p>
	 * construct.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfo} object.
	 */
	public abstract ModuleInfo construct();
}
