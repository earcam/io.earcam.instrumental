/**
 * @version 10.0.2
 * @package org.graalvm.compiler.hotspot.jmx
 */
module jdk.internal.vm.compiler.management {
	requires java.management;
	requires jdk.internal.vm.ci;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.internal.vm.compiler;
	requires jdk.management;
	provides sun.management.spi.PlatformMBeanProvider with 
		org.graalvm.compiler.hotspot.jmx.GraalMBeans;
}