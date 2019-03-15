/**
 * @version 9.0.4
 * @package jdk.internal.editor.external
 * @package jdk.internal.editor.spi
 */
module jdk.internal.ed {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports jdk.internal.editor.spi to 
		jdk.editpad,
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.editor.external to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
}