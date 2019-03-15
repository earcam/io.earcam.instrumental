/**
 * @version 10.0.2
 * @package com.sun.activation.registries
 * @package javax.activation
 */
module java.activation {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.logging;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.datatransfer;
	exports javax.activation;
}