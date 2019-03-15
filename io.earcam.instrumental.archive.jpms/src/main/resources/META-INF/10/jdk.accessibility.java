/**
 * @version 10.0.2
 * @package com.sun.java.accessibility.util
 * @package com.sun.java.accessibility.util.internal
 */
module jdk.accessibility {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.desktop;
	exports com.sun.java.accessibility.util;
}