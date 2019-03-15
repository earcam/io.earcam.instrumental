/**
 * @version 11.0.2
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
	exports sun.misc;
	exports sun.reflect;
	opens sun.misc;
	opens sun.reflect;
}