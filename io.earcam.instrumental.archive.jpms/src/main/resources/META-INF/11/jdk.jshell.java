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
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.compiler;
	requires java.logging;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.prefs;
	requires jdk.compiler;
	requires jdk.internal.ed;
	requires jdk.internal.le;
	requires jdk.internal.opt;
	/**
	 * @modifiers transitive
	 */
	requires transitive jdk.jdi;
	exports jdk.jshell;
	exports jdk.jshell.execution;
	exports jdk.jshell.spi;
	exports jdk.jshell.tool;
	uses jdk.internal.editor.spi.BuildInEditorProvider;
	uses jdk.jshell.spi.ExecutionControlProvider;
	provides javax.tools.Tool with 
		jdk.internal.jshell.tool.JShellToolProvider;
	provides jdk.jshell.spi.ExecutionControlProvider with 
		jdk.jshell.execution.FailOverExecutionControlProvider,
		jdk.jshell.execution.JdiExecutionControlProvider,
		jdk.jshell.execution.LocalExecutionControlProvider;
}