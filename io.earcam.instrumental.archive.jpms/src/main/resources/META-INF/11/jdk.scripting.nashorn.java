/**
 * @version 11.0.2
 * @package jdk.nashorn.api.linker
 * @package jdk.nashorn.api.scripting
 * @package jdk.nashorn.api.scripting.resources
 * @package jdk.nashorn.api.tree
 * @package jdk.nashorn.internal
 * @package jdk.nashorn.internal.codegen
 * @package jdk.nashorn.internal.codegen.types
 * @package jdk.nashorn.internal.ir
 * @package jdk.nashorn.internal.ir.annotations
 * @package jdk.nashorn.internal.ir.debug
 * @package jdk.nashorn.internal.ir.visitor
 * @package jdk.nashorn.internal.lookup
 * @package jdk.nashorn.internal.objects
 * @package jdk.nashorn.internal.objects.annotations
 * @package jdk.nashorn.internal.parser
 * @package jdk.nashorn.internal.runtime
 * @package jdk.nashorn.internal.runtime.arrays
 * @package jdk.nashorn.internal.runtime.doubleconv
 * @package jdk.nashorn.internal.runtime.events
 * @package jdk.nashorn.internal.runtime.linker
 * @package jdk.nashorn.internal.runtime.logging
 * @package jdk.nashorn.internal.runtime.options
 * @package jdk.nashorn.internal.runtime.regexp
 * @package jdk.nashorn.internal.runtime.regexp.joni
 * @package jdk.nashorn.internal.runtime.regexp.joni.ast
 * @package jdk.nashorn.internal.runtime.regexp.joni.constants
 * @package jdk.nashorn.internal.runtime.regexp.joni.encoding
 * @package jdk.nashorn.internal.runtime.regexp.joni.exception
 * @package jdk.nashorn.internal.runtime.resources
 * @package jdk.nashorn.internal.runtime.resources.fx
 * @package jdk.nashorn.internal.scripts
 * @package jdk.nashorn.tools
 * @package jdk.nashorn.tools.resources
 */
module jdk.scripting.nashorn {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.logging;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.scripting;
	requires jdk.dynalink;
	exports jdk.nashorn.api.scripting;
	exports jdk.nashorn.api.tree;
	exports jdk.nashorn.internal.objects to 
		jdk.scripting.nashorn.shell;
	exports jdk.nashorn.internal.runtime to 
		jdk.scripting.nashorn.shell;
	exports jdk.nashorn.tools to 
		jdk.scripting.nashorn.shell;
	provides javax.script.ScriptEngineFactory with 
		jdk.nashorn.api.scripting.NashornScriptEngineFactory;
	provides jdk.dynalink.linker.GuardingDynamicLinkerExporter with 
		jdk.nashorn.api.linker.NashornLinkerExporter;
}