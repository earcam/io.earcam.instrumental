/**
 * @version 11.0.2
 * @package jdk.internal.jshell.debug
 * @package jdk.internal.jshell.tool
 * @package jdk.internal.jshell.tool.resources
 * @package jdk.jshell
 * @package jdk.jshell.execution
 * @package jdk.jshell.resources
 * @package jdk.jshell.spi
 * @package jdk.jshell.tool
 * @package jdk.jshell.tool.resources
 */
module jdk.jshell {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.compiler;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.prefs;
	requires jdk.internal.le;
	requires jdk.internal.opt;
	requires jdk.internal.ed;
	/**
	 * @modifiers transitive
	 */
	requires transitive jdk.jdi;
	requires java.logging;
	requires jdk.compiler;
	exports jdk.jshell.spi;
	exports jdk.jshell;
	exports jdk.jshell.tool;
	exports jdk.jshell.execution;
	uses jdk.internal.editor.spi.BuildInEditorProvider;
	uses jdk.jshell.spi.ExecutionControlProvider;
	provides jdk.jshell.spi.ExecutionControlProvider with 
		jdk.jshell.execution.FailOverExecutionControlProvider,
		jdk.jshell.execution.JdiExecutionControlProvider,
		jdk.jshell.execution.LocalExecutionControlProvider;
	provides javax.tools.Tool with 
		jdk.internal.jshell.tool.JShellToolProvider;
}