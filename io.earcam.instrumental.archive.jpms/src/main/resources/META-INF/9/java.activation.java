/**
 * @version 9.0.4
 * @package com.sun.activation.registries
 * @package javax.activation
 */
module java.activation {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.datatransfer;
	requires java.logging;
	exports javax.activation;
}