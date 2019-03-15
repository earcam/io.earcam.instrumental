/**
 * @version 12
 * @package com.sun.management
 * @package com.sun.management.internal
 */
module jdk.management {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports com.sun.management;
	provides sun.management.spi.PlatformMBeanProvider with 
		com.sun.management.internal.PlatformMBeanProviderImpl;
}