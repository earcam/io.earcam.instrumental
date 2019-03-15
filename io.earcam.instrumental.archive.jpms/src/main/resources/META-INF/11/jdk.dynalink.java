/**
 * @version 11.0.2
 * @package jdk.dynalink
 * @package jdk.dynalink.beans
 * @package jdk.dynalink.internal
 * @package jdk.dynalink.linker
 * @package jdk.dynalink.linker.support
 * @package jdk.dynalink.support
 */
module jdk.dynalink {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.logging;
	exports jdk.dynalink;
	exports jdk.dynalink.beans;
	exports jdk.dynalink.linker;
	exports jdk.dynalink.linker.support;
	exports jdk.dynalink.support;
	uses jdk.dynalink.linker.GuardingDynamicLinkerExporter;
}