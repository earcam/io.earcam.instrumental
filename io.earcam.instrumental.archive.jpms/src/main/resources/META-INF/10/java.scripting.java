/**
 * @version 10.0.2
 * @package com.sun.tools.script.shell
 * @package javax.script
 */
module java.scripting {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports javax.script;
	uses javax.script.ScriptEngineFactory;
}