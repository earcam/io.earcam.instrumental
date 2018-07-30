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

import java.lang.instrument.Instrumentation;
import java.util.jar.Manifest;

import io.earcam.instrumental.reflect.Methods;

class DefaultAsAgentJar extends AbstractAsJarBuilder<AsAgentJar> implements AsAgentJar {

	static final String PREMAIN_CLASS = "Premain-Class";
	static final String AGENT_CLASS = "Agent-Class";

	static final String CAN_REDEFINE_CLASSES = "Can-Redefine-Classes";
	static final String CAN_RETRANSFORM_CLASSES = "Can-Retransform-Classes";

	private boolean canRedefine = false;
	private boolean canRetransform = false;


	DefaultAsAgentJar()
	{}


	@Override
	protected AsAgentJar self()
	{
		return this;
	}


	@Override
	public void process(Manifest manifest)
	{
		withManifestHeader(CAN_REDEFINE_CLASSES, Boolean.toString(canRedefine));
		withManifestHeader(CAN_RETRANSFORM_CLASSES, Boolean.toString(canRetransform));

		super.process(manifest);
	}


	/**
	 * <p>
	 * withAgentClass.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @return a {@link io.earcam.instrumental.archive.DefaultAsAgentJar} object.
	 */
	public DefaultAsAgentJar withAgentClass(Class<?> type)
	{
		if(validate()) {
			requireAgentMainMethod(type);
		}
		source.with(type);
		String name = type.getCanonicalName();
		if(hasAgentPreMainMethod(type)) {
			withManifestHeader(PREMAIN_CLASS, name);
		}
		withManifestHeader(AGENT_CLASS, name);
		return this;
	}


	private boolean hasAgentPreMainMethod(Class<?> type)
	{
		return findAgentMethod(type, "premain");
	}


	private boolean findAgentMethod(Class<?> type, String name)
	{
		return Methods.getMethod(type, name, String.class, Instrumentation.class)
				.filter(DefaultAsAgentJar::isPublicStaticVoid)
				.isPresent();
	}


	private void requireAgentMainMethod(Class<?> type)
	{
		if(!findAgentMethod(type, "agentmain")) {
			throw new IllegalArgumentException("'public static void agentmain(String arg0, Instrumentation arg1)' method not found on " + type);
		}
	}


	/**
	 * <p>
	 * canRedefineClasses.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.DefaultAsAgentJar} object.
	 */
	public DefaultAsAgentJar canRedefineClasses()
	{
		canRedefine = true;
		return this;
	}


	/**
	 * <p>
	 * canNotRedefineClasses.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.DefaultAsAgentJar} object.
	 */
	public DefaultAsAgentJar canNotRedefineClasses()
	{
		canRedefine = false;
		return this;
	}


	/**
	 * <p>
	 * canRetransformClasses.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.DefaultAsAgentJar} object.
	 */
	public DefaultAsAgentJar canRetransformClasses()
	{
		canRetransform = true;
		return this;
	}


	/**
	 * <p>
	 * canNotRetransformClasses.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.archive.DefaultAsAgentJar} object.
	 */
	public DefaultAsAgentJar canNotRetransformClasses()
	{
		canRetransform = false;
		return this;
	}
}
