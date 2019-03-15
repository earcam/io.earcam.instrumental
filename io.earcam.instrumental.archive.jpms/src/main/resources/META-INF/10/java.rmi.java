/**
 * @version 10.0.2
 * @package com.sun.rmi.rmid
 * @package java.rmi
 * @package java.rmi.activation
 * @package java.rmi.dgc
 * @package java.rmi.registry
 * @package java.rmi.server
 * @package javax.rmi.ssl
 * @package sun.rmi.log
 * @package sun.rmi.registry
 * @package sun.rmi.registry.resources
 * @package sun.rmi.runtime
 * @package sun.rmi.server
 * @package sun.rmi.server.resources
 * @package sun.rmi.transport
 * @package sun.rmi.transport.tcp
 */
module java.rmi {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.logging;
	exports com.sun.rmi.rmid to 
		java.base;
	exports java.rmi;
	exports java.rmi.activation;
	exports java.rmi.dgc;
	exports java.rmi.registry;
	exports java.rmi.server;
	exports javax.rmi.ssl;
	exports sun.rmi.registry to 
		jdk.management.agent;
	exports sun.rmi.server to 
		java.management.rmi,
		jdk.jconsole,
		jdk.management.agent;
	exports sun.rmi.transport to 
		java.management.rmi,
		jdk.jconsole,
		jdk.management.agent;
	uses java.rmi.server.RMIClassLoaderSpi;
}