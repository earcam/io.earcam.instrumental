/**
 * @version 9.0.4
 * @package com.sun.nio.file
 * @package sun.misc
 * @package sun.reflect
 */
module jdk.unsupported {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports com.sun.nio.file;
	exports sun.reflect;
	exports sun.misc;
	opens sun.reflect;
	opens sun.misc;
}