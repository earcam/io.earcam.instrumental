/**
 * @version 9.0.4
 * @package jdk.management.cmm
 * @package jdk.management.cmm.internal
 */
module jdk.management.cmm {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.management;
	exports jdk.management.cmm;
	provides sun.management.spi.PlatformMBeanProvider with 
		jdk.management.cmm.internal.PlatformMBeanProviderImpl;
}