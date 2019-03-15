/**
 * @version 9.0.4
 * @package jdk.management.resource
 * @package jdk.management.resource.internal
 * @package jdk.management.resource.internal.inst
 */
module jdk.management.resource {
	requires java.instrument;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports jdk.management.resource;
	exports jdk.management.resource.internal to 
		java.base;
}