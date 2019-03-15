/**
 * @version 11.0.2
 * @package com.sun.jmx.remote.internal.rmi
 * @package com.sun.jmx.remote.protocol.rmi
 * @package javax.management.remote.rmi
 */
module java.management.rmi {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management;
	requires java.naming;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.rmi;
	exports com.sun.jmx.remote.internal.rmi to 
		jdk.management.agent;
	exports com.sun.jmx.remote.protocol.rmi to 
		java.management;
	exports javax.management.remote.rmi;
	provides javax.management.remote.JMXConnectorProvider with 
		com.sun.jmx.remote.protocol.rmi.ClientProvider;
	provides javax.management.remote.JMXConnectorServerProvider with 
		com.sun.jmx.remote.protocol.rmi.ServerProvider;
}