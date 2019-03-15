/**
 * @version 11.0.2
 * @package jdk.nashorn.tools.jjs
 * @package jdk.nashorn.tools.jjs.resources
 */
module jdk.scripting.nashorn.shell {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers static
	 */
	requires static java.compiler;
	requires jdk.internal.ed;
	requires jdk.internal.le;
	requires jdk.scripting.nashorn;
	uses jdk.internal.editor.spi.BuildInEditorProvider;
}