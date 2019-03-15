/**
 * @version 10.0.2
 * @package jdk.management.jfr
 * @package jdk.management.jfr.internal
 */
module jdk.management.jfr {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management;
	requires jdk.jfr;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.management;
	exports jdk.management.jfr;
	provides sun.management.spi.PlatformMBeanProvider with 
		jdk.management.jfr.internal.FlightRecorderMXBeanProvider;
}