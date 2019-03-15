/**
 * @version 10.0.2
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