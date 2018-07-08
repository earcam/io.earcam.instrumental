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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

/**
 * <p>
 * ModuleInfo interface.
 * </p>
 *
 */
public interface ModuleInfo extends Serializable {

	/**
	 * <p>
	 * name.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract @Nonnull String name();


	/**
	 * <p>
	 * version.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract @Nullable String version();


	/**
	 * <p>
	 * access.
	 * </p>
	 *
	 * @return a int.
	 */
	public abstract int access();


	/**
	 * <p>
	 * modifiers.
	 * </p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public default Set<ModuleModifier> modifiers()
	{
		return Access.modifiers(ModuleModifier.class, access());
	}


	/**
	 * <p>
	 * packages.
	 * </p>
	 *
	 * @return a {@link java.util.SortedSet} object.
	 */
	public abstract SortedSet<CharSequence> packages();


	/**
	 * <p>
	 * uses.
	 * </p>
	 *
	 * @return a {@link java.util.SortedSet} object.
	 */
	public abstract SortedSet<String> uses();


	/**
	 * <p>
	 * provides.
	 * </p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public abstract Map<String, String[]> provides();


	/**
	 * <p>
	 * mainClass.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String mainClass();


	/**
	 * <p>
	 * requires.
	 * </p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public abstract Set<Require> requires();


	/**
	 * <p>
	 * exports.
	 * </p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public abstract Set<Export> exports();


	/**
	 * <p>
	 * opens.
	 * </p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public abstract Set<Export> opens();


	/**
	 * <p>
	 * toBytecode.
	 * </p>
	 *
	 * @return an array of {@link byte} objects.
	 */
	public abstract byte[] toBytecode();


	/**
	 * <p>
	 * moduleInfo.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public static ModuleInfoBuilder moduleInfo()
	{
		return new DefaultModuleInfo();
	}


	/**
	 * <p>
	 * read.
	 * </p>
	 *
	 * @param bytecode an array of {@link byte} objects.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfo} object.
	 */
	public static ModuleInfo read(byte[] bytecode)
	{
		return BytecodeReader.read(bytecode);
	}


	/**
	 * <p>
	 * read.
	 * </p>
	 *
	 * @param bytecode a {@link java.io.InputStream} object.
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfo} object.
	 * @throws java.io.IOException if any.
	 */
	public static ModuleInfo read(@WillNotClose InputStream bytecode) throws IOException
	{
		return BytecodeReader.read(bytecode);
	}


	/**
	 * <p>
	 * deconstruct.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public abstract ModuleInfoBuilder deconstruct();
}
