/**
 * @version 12
 * @package org.graalvm.compiler.hotspot.management
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
	provides org.graalvm.compiler.serviceprovider.JMXService with 
		org.graalvm.compiler.hotspot.management.JMXServiceProvider;
	provides org.graalvm.compiler.hotspot.HotSpotGraalManagementRegistration with 
		org.graalvm.compiler.hotspot.management.HotSpotGraalManagement;
}