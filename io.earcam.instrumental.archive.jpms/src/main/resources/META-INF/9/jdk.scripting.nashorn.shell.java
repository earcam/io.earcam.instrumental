/**
 * @version 9.0.4
 * @package jdk.nashorn.tools.jjs
 */
module jdk.scripting.nashorn.shell {
	requires java.compiler;
	requires jdk.scripting.nashorn;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.internal.le;
	requires java.desktop;
}