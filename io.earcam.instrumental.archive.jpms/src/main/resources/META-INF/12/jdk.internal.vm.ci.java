/**
 * @version 12
 * @package jdk.vm.ci.aarch64
 * @package jdk.vm.ci.amd64
 * @package jdk.vm.ci.code
 * @package jdk.vm.ci.code.site
 * @package jdk.vm.ci.code.stack
 * @package jdk.vm.ci.common
 * @package jdk.vm.ci.hotspot
 * @package jdk.vm.ci.hotspot.aarch64
 * @package jdk.vm.ci.hotspot.amd64
 * @package jdk.vm.ci.hotspot.sparc
 * @package jdk.vm.ci.meta
 * @package jdk.vm.ci.runtime
 * @package jdk.vm.ci.services
 * @package jdk.vm.ci.sparc
 */
module jdk.internal.vm.ci {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports jdk.vm.ci.runtime to 
		jdk.internal.vm.compiler,
		jdk.internal.vm.compiler.management;
	exports jdk.vm.ci.services to 
		jdk.internal.vm.compiler;
	uses jdk.vm.ci.hotspot.HotSpotJVMCIBackendFactory;
	uses jdk.vm.ci.services.JVMCIServiceLocator;
	provides jdk.vm.ci.hotspot.HotSpotJVMCIBackendFactory with 
		jdk.vm.ci.hotspot.aarch64.AArch64HotSpotJVMCIBackendFactory,
		jdk.vm.ci.hotspot.amd64.AMD64HotSpotJVMCIBackendFactory,
		jdk.vm.ci.hotspot.sparc.SPARCHotSpotJVMCIBackendFactory;
}