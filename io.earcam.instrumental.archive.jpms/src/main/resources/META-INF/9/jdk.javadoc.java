/**
 * @version 9.0.4
 * @package com.sun.javadoc
 * @package com.sun.tools.doclets
 * @package com.sun.tools.doclets.formats.html
 * @package com.sun.tools.doclets.formats.html.markup
 * @package com.sun.tools.doclets.formats.html.resources
 * @package com.sun.tools.doclets.internal.toolkit
 * @package com.sun.tools.doclets.internal.toolkit.builders
 * @package com.sun.tools.doclets.internal.toolkit.resources
 * @package com.sun.tools.doclets.internal.toolkit.resources.jquery
 * @package com.sun.tools.doclets.internal.toolkit.resources.jquery.external.jquery
 * @package com.sun.tools.doclets.internal.toolkit.resources.jquery.images
 * @package com.sun.tools.doclets.internal.toolkit.resources.jquery.jszip.dist
 * @package com.sun.tools.doclets.internal.toolkit.taglets
 * @package com.sun.tools.doclets.internal.toolkit.util
 * @package com.sun.tools.doclets.internal.toolkit.util.links
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
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.compiler;
	requires java.xml;
	/**
	 * @modifiers transitive
	 */
	requires transitive jdk.compiler;
	exports com.sun.javadoc;
	exports com.sun.tools.doclets;
	exports com.sun.tools.doclets.standard;
	exports com.sun.tools.javadoc;
	exports jdk.javadoc.doclet;
	provides java.util.spi.ToolProvider with 
		jdk.javadoc.internal.tool.JavadocToolProvider;
	provides javax.tools.DocumentationTool with 
		jdk.javadoc.internal.api.JavadocTool;
	provides javax.tools.Tool with 
		jdk.javadoc.internal.api.JavadocTool;
}