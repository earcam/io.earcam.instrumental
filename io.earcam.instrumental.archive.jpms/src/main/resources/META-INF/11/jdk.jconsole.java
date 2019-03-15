/**
 * @version 11.0.2
 * @package com.sun.tools.jconsole
 * @package sun.tools.jconsole
 * @package sun.tools.jconsole.inspector
 * @package sun.tools.jconsole.resources
 */
module jdk.jconsole {
	requires jdk.internal.jvmstat;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.rmi;
	requires jdk.management.agent;
	requires java.management.rmi;
	requires jdk.management;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.desktop;
	requires jdk.attach;
	exports com.sun.tools.jconsole;
	uses com.sun.tools.jconsole.JConsolePlugin;
}