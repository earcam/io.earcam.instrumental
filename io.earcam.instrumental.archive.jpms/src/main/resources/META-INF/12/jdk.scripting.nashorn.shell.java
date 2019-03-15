/**
 * @version 12
 * @package jdk.nashorn.tools.jjs
 * @package jdk.nashorn.tools.jjs.resources
 */
module jdk.scripting.nashorn.shell {
	/**
	 * @modifiers static
	 */
	requires static java.compiler;
	requires jdk.scripting.nashorn;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.internal.le;
	requires jdk.internal.ed;
	uses jdk.internal.editor.spi.BuildInEditorProvider;
}