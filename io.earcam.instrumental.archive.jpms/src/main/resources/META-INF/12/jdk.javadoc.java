/**
 * @version 12
 * @package com.sun.javadoc
 * @package com.sun.tools.doclets.standard
 * @package com.sun.tools.javadoc
 * @package com.sun.tools.javadoc.main
 * @package com.sun.tools.javadoc.resources
 * @package jdk.javadoc.doclet
 * @package jdk.javadoc.internal.api
 * @package jdk.javadoc.internal.doclets.formats.html
 * @package jdk.javadoc.internal.doclets.formats.html.markup
 * @package jdk.javadoc.internal.doclets.formats.html.resources
 * @package jdk.javadoc.internal.doclets.formats.html.resources.jquery
 * @package jdk.javadoc.internal.doclets.formats.html.resources.jquery.external.jquery
 * @package jdk.javadoc.internal.doclets.formats.html.resources.jquery.images
 * @package jdk.javadoc.internal.doclets.formats.html.resources.jquery.jszip.dist
 * @package jdk.javadoc.internal.doclets.toolkit
 * @package jdk.javadoc.internal.doclets.toolkit.builders
 * @package jdk.javadoc.internal.doclets.toolkit.resources
 * @package jdk.javadoc.internal.doclets.toolkit.taglets
 * @package jdk.javadoc.internal.doclets.toolkit.util
 * @package jdk.javadoc.internal.doclets.toolkit.util.links
 * @package jdk.javadoc.internal.tool
 * @package jdk.javadoc.internal.tool.resources
 */
module jdk.javadoc {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.compiler;
	requires java.xml;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive jdk.compiler;
	exports com.sun.tools.javadoc;
	exports com.sun.javadoc;
	exports jdk.javadoc.doclet;
	provides javax.tools.Tool with 
		jdk.javadoc.internal.api.JavadocTool;
	provides javax.tools.DocumentationTool with 
		jdk.javadoc.internal.api.JavadocTool;
	provides java.util.spi.ToolProvider with 
		jdk.javadoc.internal.tool.JavadocToolProvider;
}