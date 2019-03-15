/**
 * @version 9.0.4
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
	requires jdk.management.agent;
	requires java.rmi;
	requires java.management.rmi;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.desktop;
	requires jdk.management;
	requires jdk.attach;
	exports com.sun.tools.jconsole;
	uses com.sun.tools.jconsole.JConsolePlugin;
}