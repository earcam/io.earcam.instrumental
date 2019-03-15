/**
 * @version 10.0.2
 * @package jdk.internal.netscape.javascript.spi
 * @package netscape.javascript
 */
module jdk.jsobject {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	exports jdk.internal.netscape.javascript.spi to 
		jdk.plugin;
	exports netscape.javascript;
	uses jdk.internal.netscape.javascript.spi.JSObjectProvider;
}