/**
 * @version 9.0.4
 * @package jdk.internal.agent
 * @package jdk.internal.agent.resources
 * @package jdk.internal.agent.spi
 * @package sun.management.jdp
 * @package sun.management.jmxremote
 */
module jdk.management.agent {
	requires java.management;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.management.rmi;
	exports jdk.internal.agent to 
		jdk.jconsole;
	exports jdk.internal.agent.spi to 
		jdk.snmp;
	uses jdk.internal.agent.spi.AgentProvider;
}