/**
 * @version 9.0.4
 * @package com.sun.jmx.remote.internal.rmi
 * @package com.sun.jmx.remote.protocol.rmi
 * @package javax.management.remote.rmi
 */
module java.management.rmi {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.naming;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.rmi;
	exports javax.management.remote.rmi;
	exports com.sun.jmx.remote.protocol.rmi to 
		java.management;
	exports com.sun.jmx.remote.internal.rmi to 
		jdk.management.agent;
	provides javax.management.remote.JMXConnectorProvider with 
		com.sun.jmx.remote.protocol.rmi.ClientProvider;
	provides javax.management.remote.JMXConnectorServerProvider with 
		com.sun.jmx.remote.protocol.rmi.ServerProvider;
}