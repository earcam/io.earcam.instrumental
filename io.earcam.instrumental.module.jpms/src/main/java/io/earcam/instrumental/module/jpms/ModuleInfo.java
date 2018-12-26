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
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.jar.JarInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;
import javax.lang.model.SourceVersion;

/**
 *
 */
public interface ModuleInfo extends Serializable {

	/**
	 * @return a new {@link ModuleInfoBuilder}.
	 */
	public static ModuleInfoBuilder moduleInfo()
	{
		return new DefaultModuleInfo();
	}


	/**
	 * <p>
	 * Read a {@code module-info.class}
	 * </p>
	 *
	 * @param bytecode the binary for {@code module-info.class}
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfo} object.
	 */
	public static ModuleInfo read(byte[] bytecode)
	{
		return BytecodeReader.read(bytecode);
	}


	/**
	 * <p>
	 * Read a {@code module-info.class}. The InputStream will not be closed, allowing
	 * use coding inspecting {@link JarInputStream} entries.
	 * </p>
	 *
	 * @param bytecode the {@link InputStream}, which at current point contains {@code module-info.class}
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfo} object.
	 * 
	 * @throws java.io.IOException if any.
	 */
	public static ModuleInfo read(@WillNotClose InputStream bytecode) throws IOException
	{
		return BytecodeReader.read(bytecode);
	}


	/**
	 * Extracts a {@code module-info} from a JAR (zip or directory-as-exploded-JAR)
	 * 
	 * @param jar path to the JAR
	 * @return a {@link ModuleInfo} instance, possibly {@link SYNTHETIC},
	 * or {@link Optional#empty()} if no module-info can be derived.
	 * @throws IOException
	 * 
	 * @see {@link #read(byte[])} to just read a {@code module-info.class} file
	 */
	public static Optional<ModuleInfo> extract(Path jar) throws IOException
	{
		return ModuleInfoExtractor.extract(jar);
	}


	/**
	 * Extracts a {@code module-info} from a {@link JarInputStream}.
	 * 
	 * @param jar the JAR input stream.
	 * @return a {@link ModuleInfo} instance, possibly {@link SYNTHETIC},
	 * or {@link Optional#empty()} if no module-info can be derived.
	 * @throws IOException
	 */
	public static Optional<ModuleInfo> extract(@WillClose JarInputStream jar) throws IOException
	{
		return ModuleInfoExtractor.extract(jar);
	}


	/**
	 * @return the module's name.
	 */
	public abstract @Nonnull String name();


	/**
	 * @return the module's version.
	 */
	public abstract @Nullable String version();


	/**
	 * @return the module's access flags.
	 */
	public abstract int access();


	/**
	 * @return the module's modifiers.
	 */
	public default Set<ModuleModifier> modifiers()
	{
		return Access.modifiers(ModuleModifier.class, access());
	}


	/**
	 * @return the module's packages.
	 */
	public abstract SortedSet<CharSequence> packages();


	/**
	 * @return the module's <b>uses</b>.
	 */
	public abstract SortedSet<String> uses();


	/**
	 * @return the module's <b>provides</b>.
	 */
	public abstract Map<String, String[]> provides();


	/**
	 * @return the module's main-class to launch.
	 */
	public abstract String mainClass();


	/**
	 * @return the module's <b>requires</b>.
	 */
	public abstract Set<Require> requires();


	/**
	 * @return the module's <b>exports</b>.
	 */
	public abstract Set<Export> exports();


	/**
	 * @return the module's <b>opens</b>.
	 */
	public abstract Set<Export> opens();


	/**
	 * The generated class file with a major version (53) set for JDK9
	 * 
	 * @return convert this model to bytecode.
	 * 
	 * @see #toBytecode(int)
	 * @see #toBytecode(SourceVersion)
	 */
	public default byte[] toBytecode()
	{
		return toBytecode(9);
	}


	/**
	 * You may choose to use {@link SourceVersion#latest()} on JDKs &ge; 9
	 * 
	 * @return convert this model to bytecode.
	 * 
	 * @see #toBytecode(int)
	 */
	public default byte[] toBytecode(SourceVersion jdkVersion)
	{
		return toBytecode(jdkVersion.ordinal());
	}


	/**
	 * @return convert this model to bytecode.
	 * 
	 * @see #toBytecode(SourceVersion)
	 */
	public abstract byte[] toBytecode(int jdkVersion);


	/**
	 * @return a mutable version of this model.
	 */
	public abstract ModuleInfoBuilder deconstruct();
}
