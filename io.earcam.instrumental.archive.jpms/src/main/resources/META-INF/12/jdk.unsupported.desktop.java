/**
 * @version 12
 * @package jdk.swing.interop
 * @package jdk.swing.interop.internal
 */
module jdk.unsupported.desktop {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.desktop;
	exports jdk.swing.interop;
	provides sun.swing.InteropProvider with 
		jdk.swing.interop.internal.InteropProviderImpl;
}