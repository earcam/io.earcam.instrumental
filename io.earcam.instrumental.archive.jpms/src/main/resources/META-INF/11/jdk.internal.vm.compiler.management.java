/**
 * @version 11.0.2
 * @package org.graalvm.compiler.hotspot.management
 */
module jdk.internal.vm.compiler.management {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.management;
	requires jdk.internal.vm.ci;
	requires jdk.internal.vm.compiler;
	requires jdk.management;
}