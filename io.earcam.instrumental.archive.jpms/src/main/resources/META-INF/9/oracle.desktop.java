/**
 * @version 9.0.4
 * @package com.oracle.awt
 */
module oracle.desktop {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.desktop;
	exports com.oracle.awt;
}