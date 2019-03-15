/**
 * @version 11.0.2
 * @package jdk.jfr
 * @package jdk.jfr.consumer
 * @package jdk.jfr.events
 * @package jdk.jfr.internal
 * @package jdk.jfr.internal.cmd
 * @package jdk.jfr.internal.consumer
 * @package jdk.jfr.internal.dcmd
 * @package jdk.jfr.internal.handlers
 * @package jdk.jfr.internal.instrument
 * @package jdk.jfr.internal.jfc
 * @package jdk.jfr.internal.management
 * @package jdk.jfr.internal.settings
 * @package jdk.jfr.internal.test
 * @package jdk.jfr.internal.types
 */
module jdk.jfr {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports jdk.jfr.internal.management to 
		jdk.management.jfr;
	exports jdk.jfr;
	exports jdk.jfr.consumer;
}