/**
 * @version 10.0.2
 * @package com.sun.jndi.rmi.registry
 * @package com.sun.jndi.url.rmi
 */
module jdk.naming.rmi {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.naming;
	requires java.rmi;
	exports com.sun.jndi.rmi.registry to 
		java.rmi;
	exports com.sun.jndi.url.rmi to 
		java.naming;
	provides javax.naming.spi.InitialContextFactory with 
		com.sun.jndi.rmi.registry.RegistryContextFactory;
}