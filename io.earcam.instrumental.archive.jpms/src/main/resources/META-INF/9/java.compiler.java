/**
 * @version 9.0.4
 * @package javax.annotation.processing
 * @package javax.lang.model
 * @package javax.lang.model.element
 * @package javax.lang.model.type
 * @package javax.lang.model.util
 * @package javax.tools
 */
module java.compiler {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports javax.lang.model.type;
	exports javax.lang.model.element;
	exports javax.annotation.processing;
	exports javax.tools;
	exports javax.lang.model;
	exports javax.lang.model.util;
	uses javax.tools.DocumentationTool;
	uses javax.tools.JavaCompiler;
}