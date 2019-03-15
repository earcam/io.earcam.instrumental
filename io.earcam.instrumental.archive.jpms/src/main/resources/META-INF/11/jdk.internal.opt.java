/**
 * @version 11.0.2
 * @package jdk.internal.joptsimple
 * @package jdk.internal.joptsimple.internal
 * @package jdk.internal.joptsimple.util
 */
module jdk.internal.opt {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports jdk.internal.joptsimple to 
		jdk.jlink,
		jdk.jshell;
}