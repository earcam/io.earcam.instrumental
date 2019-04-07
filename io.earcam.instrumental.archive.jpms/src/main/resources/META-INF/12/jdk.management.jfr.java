/**
 * @version 12
 * @package jdk.management.jfr
 * @package jdk.management.jfr.internal
 */
module jdk.management.jfr {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management;
	requires jdk.jfr;
	requires jdk.management;
	exports jdk.management.jfr;
	provides sun.management.spi.PlatformMBeanProvider with 
		jdk.management.jfr.internal.FlightRecorderMXBeanProvider;
}