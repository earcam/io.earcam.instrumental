/**
 * @version 11.0.2
 * @package com.sun.jarsigner
 * @package jdk.security.jarsigner
 * @package sun.security.tools.jarsigner
 * @package sun.tools.jar
 * @package sun.tools.jar.resources
 */
module jdk.jartool {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports com.sun.jarsigner;
	exports jdk.security.jarsigner;
	provides java.util.spi.ToolProvider with 
		sun.tools.jar.JarToolProvider;
}