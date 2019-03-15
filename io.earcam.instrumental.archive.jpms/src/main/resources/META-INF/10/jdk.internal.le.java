/**
 * @version 10.0.2
 * @package jdk.internal.jline
 * @package jdk.internal.jline.console
 * @package jdk.internal.jline.console.completer
 * @package jdk.internal.jline.console.history
 * @package jdk.internal.jline.console.internal
 * @package jdk.internal.jline.extra
 * @package jdk.internal.jline.internal
 */
module jdk.internal.le {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports jdk.internal.jline to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.jline.console to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.jline.console.completer to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.jline.console.history to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.jline.extra to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.jline.internal to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
}