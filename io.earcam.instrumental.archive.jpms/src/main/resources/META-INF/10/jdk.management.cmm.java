/**
 * @version 10.0.2
 * @package jdk.management.cmm
 * @package jdk.management.cmm.internal
 */
module jdk.management.cmm {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management;
	requires jdk.management;
	exports jdk.management.cmm;
	provides sun.management.spi.PlatformMBeanProvider with 
		jdk.management.cmm.internal.PlatformMBeanProviderImpl;
}