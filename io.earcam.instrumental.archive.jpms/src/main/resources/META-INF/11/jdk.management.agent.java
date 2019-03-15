/**
 * @version 11.0.2
 * @package jdk.internal.agent
 * @package jdk.internal.agent.resources
 * @package jdk.internal.agent.spi
 * @package sun.management.jdp
 * @package sun.management.jmxremote
 */
module jdk.management.agent {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.management;
	requires java.management.rmi;
	exports jdk.internal.agent to 
		jdk.jconsole;
	uses jdk.internal.agent.spi.AgentProvider;
}