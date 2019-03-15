/**
 * @version 9.0.4
 * @package java.lang.instrument
 * @package jdk.internal.instrumentation
 * @package sun.instrument
 */
module java.instrument {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports java.lang.instrument;
	exports jdk.internal.instrumentation to 
		jdk.jfr,
		jdk.management.resource;
	exports sun.instrument to 
		java.base;
}