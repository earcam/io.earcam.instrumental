/*-
 * #%L
 * io.earcam.instrumental.agent
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
package io.earcam.instrumental.agent;

import static java.io.File.separatorChar;
import static java.nio.charset.Charset.defaultCharset;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.sun.tools.attach.AgentInitializationException;  //NOSONAR these are @jdk.Exported
import com.sun.tools.attach.AgentLoadException;            //NOSONAR
import com.sun.tools.attach.AttachNotSupportedException;   //NOSONAR
import com.sun.tools.attach.VirtualMachine;                //NOSONAR

import io.earcam.instrumental.reflect.Resources;
import io.earcam.unexceptional.Closing;
import io.earcam.unexceptional.Closing.AutoClosed;
import io.earcam.utilitarian.io.IoStreams;
import io.earcam.unexceptional.Exceptional;

final class Attach {

	/**
	 * IFF {@code true}, then always attach via a separate VM
	 */
	public static final String PROPERTY_FORCE_ATTACH_TO_SELF = "io.earcam.instrumental.agent" + "forceAttachSelf";


	private Attach()
	{}


	static void attach(URI jar, String agentArguments)
			throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException, InterruptedException
	{
		if(!forceAttachToSelf() && (isJava8() || allowsAttachToSelf())) {
			VirtualMachine machine = attachToSelf();
			doLoad(machine, jar, agentArguments);
		} else {
			spawnHack(jar, agentArguments);
		}
	}


	private static void doLoad(VirtualMachine machine, URI jar, String agentArguments) throws AgentLoadException, AgentInitializationException, IOException
	{
		try(AutoClosed<VirtualMachine, IOException> vm = Closing.autoClosing(machine, VirtualMachine::detach)) {
			vm.get().loadAgent(Resources.removeJarUrlDecoration(jar), agentArguments);
		}
	}


	private static void spawnHack(URI jar, String agentArguments) throws IOException, InterruptedException
	{
		List<String> cmd = buildCommand(jar, agentArguments);

		@SuppressWarnings("squid:S4721")
		ProcessBuilder pb = new ProcessBuilder(cmd)
				.directory(new File(System.getProperty("user.dir")));

		Process process = pb.start();
		int exitCode = process.waitFor();
		if(exitCode != 0) {
			String stdout = new String(IoStreams.readAllBytes(process.getInputStream()), defaultCharset());
			String stderr = new String(IoStreams.readAllBytes(process.getErrorStream()), defaultCharset());
			throw new IllegalStateException("Failed to spawn VM (for attach-to-self work around),\nstdout: "
					+ stdout + "\nstderr: " + stderr);
		}
	}


	private static List<String> buildCommand(URI jar, String agentArguments)
	{
		String javaHome = System.getProperty("java.home");
		String classPath = System.getProperty("java.class.path");

		String exec = javaHome + separatorChar + "bin" + separatorChar + "java";

		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();

		List<String> cmd = new ArrayList<>();
		cmd.add(exec);
		cmd.add("-classpath");
		cmd.add(classPath);
		cmd.addAll(runtimeMxBean.getInputArguments());

		cmd.add(Attach.class.getCanonicalName());
		cmd.add(pid());
		cmd.add(jar.toString());
		cmd.add(agentArguments);
		return cmd;
	}


	/**
	 * <p>
	 * main.
	 * </p>
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 * @throws java.lang.Exception if any.
	 */
	public static void main(String[] args) throws Exception
	{
		String pid = args[0];
		URI jar = Exceptional.uri(args[1]);
		String agentArguments = args[2];

		doLoad(attachTo(pid), jar, agentArguments);
	}


	private static boolean forceAttachToSelf()
	{
		return Boolean.valueOf(System.getProperty(PROPERTY_FORCE_ATTACH_TO_SELF));
	}


	private static boolean allowsAttachToSelf()
	{
		return Boolean.valueOf(System.getProperty("jdk.attach.allowAttachSelf"));
	}


	private static boolean isJava8()
	{
		return System.getProperty("java.version").startsWith("1.8");
	}


	private static VirtualMachine attachToSelf() throws AttachNotSupportedException, IOException
	{
		return attachTo(pid());
	}


	private static VirtualMachine attachTo(String pid) throws AttachNotSupportedException, IOException
	{
		return VirtualMachine.attach(pid);
	}


	// Waiting on http://openjdk.java.net/jeps/102 or consider http://stackoverflow.com/a/7303433/573057
	private static String pid()
	{
		String name = ManagementFactory.getRuntimeMXBean().getName();
		return name.substring(0, name.indexOf('@'));
	}

}
