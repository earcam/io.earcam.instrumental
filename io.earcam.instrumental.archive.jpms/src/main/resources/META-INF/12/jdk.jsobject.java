/**
 * @version 12
 * @package jdk.internal.netscape.javascript.spi
 * @package netscape.javascript
 */
module jdk.jsobject {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	exports netscape.javascript;
	uses jdk.internal.netscape.javascript.spi.JSObjectProvider;
}