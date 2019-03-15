/**
 * @version 12
 * @package jdk.internal.org.jline.keymap
 * @package jdk.internal.org.jline.reader
 * @package jdk.internal.org.jline.reader.impl
 * @package jdk.internal.org.jline.reader.impl.completer
 * @package jdk.internal.org.jline.reader.impl.history
 * @package jdk.internal.org.jline.terminal
 * @package jdk.internal.org.jline.terminal.impl
 * @package jdk.internal.org.jline.terminal.spi
 * @package jdk.internal.org.jline.utils
 */
module jdk.internal.le {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports jdk.internal.org.jline.reader.impl.history to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.org.jline.reader.impl.completer to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.org.jline.utils to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.org.jline.terminal.impl to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.org.jline.terminal.spi to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.org.jline.keymap to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.org.jline.reader to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.org.jline.terminal to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	exports jdk.internal.org.jline.reader.impl to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	uses jdk.internal.org.jline.terminal.spi.JnaSupport;
}