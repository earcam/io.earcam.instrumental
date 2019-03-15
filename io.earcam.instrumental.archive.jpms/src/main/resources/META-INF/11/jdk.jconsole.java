/**
 * @version 11.0.2
 * @package com.sun.tools.jconsole
 * @package sun.tools.jconsole
 * @package sun.tools.jconsole.inspector
 * @package sun.tools.jconsole.resources
 */
module jdk.jconsole {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.desktop;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management;
	requires java.management.rmi;
	requires java.rmi;
	requires jdk.attach;
	requires jdk.internal.jvmstat;
	requires jdk.management;
	requires jdk.management.agent;
	exports com.sun.tools.jconsole;
	uses com.sun.tools.jconsole.JConsolePlugin;
}