/**
 * @version 12
 * @package com.sun.management
 * @package com.sun.management.internal
 */
module jdk.management {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management;
	exports com.sun.management;
	provides sun.management.spi.PlatformMBeanProvider with 
		com.sun.management.internal.PlatformMBeanProviderImpl;
}