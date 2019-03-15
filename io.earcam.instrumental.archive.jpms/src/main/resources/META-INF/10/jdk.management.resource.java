/**
 * @version 10.0.2
 * @package jdk.management.resource
 * @package jdk.management.resource.internal
 * @package jdk.management.resource.internal.inst
 */
module jdk.management.resource {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.instrument;
	exports jdk.management.resource;
	exports jdk.management.resource.internal to 
		java.base;
}