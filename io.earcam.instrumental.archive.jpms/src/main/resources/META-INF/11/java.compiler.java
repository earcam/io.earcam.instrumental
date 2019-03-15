/**
 * @version 11.0.2
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
	exports javax.annotation.processing;
	exports javax.lang.model;
	exports javax.lang.model.element;
	exports javax.lang.model.type;
	exports javax.lang.model.util;
	exports javax.tools;
	uses javax.tools.DocumentationTool;
	uses javax.tools.JavaCompiler;
}