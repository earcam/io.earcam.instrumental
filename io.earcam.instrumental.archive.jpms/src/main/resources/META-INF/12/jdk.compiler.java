/**
 * @version 12
 * @package com.sun.source.doctree
 * @package com.sun.source.tree
 * @package com.sun.source.util
 * @package com.sun.tools.doclint
 * @package com.sun.tools.doclint.resources
 * @package com.sun.tools.javac
 * @package com.sun.tools.javac.api
 * @package com.sun.tools.javac.code
 * @package com.sun.tools.javac.comp
 * @package com.sun.tools.javac.file
 * @package com.sun.tools.javac.jvm
 * @package com.sun.tools.javac.launcher
 * @package com.sun.tools.javac.main
 * @package com.sun.tools.javac.model
 * @package com.sun.tools.javac.parser
 * @package com.sun.tools.javac.platform
 * @package com.sun.tools.javac.processing
 * @package com.sun.tools.javac.resources
 * @package com.sun.tools.javac.tree
 * @package com.sun.tools.javac.util
 * @package com.sun.tools.sjavac
 * @package com.sun.tools.sjavac.client
 * @package com.sun.tools.sjavac.comp
 * @package com.sun.tools.sjavac.comp.dependencies
 * @package com.sun.tools.sjavac.options
 * @package com.sun.tools.sjavac.pubapi
 * @package com.sun.tools.sjavac.server
 * @package com.sun.tools.sjavac.server.log
 * @package jdk.internal.shellsupport.doc
 * @package jdk.internal.shellsupport.doc.resources
 * @package sun.tools.serialver
 * @package sun.tools.serialver.resources
 */
module jdk.compiler {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.compiler;
	exports com.sun.source.doctree;
	exports com.sun.source.tree;
	exports com.sun.source.util;
	exports com.sun.tools.doclint to 
		jdk.javadoc;
	exports com.sun.tools.javac;
	exports com.sun.tools.javac.api to 
		jdk.javadoc,
		jdk.jshell;
	exports com.sun.tools.javac.code to 
		jdk.javadoc,
		jdk.jshell;
	exports com.sun.tools.javac.comp to 
		jdk.javadoc,
		jdk.jshell;
	exports com.sun.tools.javac.file to 
		jdk.javadoc,
		jdk.jdeps;
	exports com.sun.tools.javac.jvm to 
		jdk.javadoc;
	exports com.sun.tools.javac.main to 
		jdk.javadoc,
		jdk.jshell;
	exports com.sun.tools.javac.model to 
		jdk.javadoc;
	exports com.sun.tools.javac.parser to 
		jdk.jshell;
	exports com.sun.tools.javac.platform to 
		jdk.javadoc,
		jdk.jdeps;
	exports com.sun.tools.javac.resources to 
		jdk.jshell;
	exports com.sun.tools.javac.tree to 
		jdk.javadoc,
		jdk.jshell;
	exports com.sun.tools.javac.util to 
		jdk.javadoc,
		jdk.jdeps,
		jdk.jshell;
	exports jdk.internal.shellsupport.doc to 
		jdk.jshell,
		jdk.scripting.nashorn.shell;
	uses com.sun.source.util.Plugin;
	uses com.sun.tools.javac.platform.PlatformProvider;
	uses javax.annotation.processing.Processor;
	provides com.sun.tools.javac.platform.PlatformProvider with 
		com.sun.tools.javac.platform.JDKPlatformProvider;
	provides java.util.spi.ToolProvider with 
		com.sun.tools.javac.main.JavacToolProvider;
	provides javax.tools.JavaCompiler with 
		com.sun.tools.javac.api.JavacTool;
	provides javax.tools.Tool with 
		com.sun.tools.javac.api.JavacTool;
}