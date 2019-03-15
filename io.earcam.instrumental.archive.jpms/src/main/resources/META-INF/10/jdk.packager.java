/**
 * @version 10.0.2
 * @package com.oracle.tools.packager
 * @package com.oracle.tools.packager.jnlp
 * @package com.oracle.tools.packager.linux
 * @package com.oracle.tools.packager.mac
 * @package com.oracle.tools.packager.windows
 * @package com.sun.javafx.tools.packager
 * @package com.sun.javafx.tools.packager.bundlers
 * @package com.sun.javafx.tools.resource
 * @package jdk.packager.internal
 * @package jdk.packager.internal.legacy
 * @package jdk.packager.internal.legacy.builders
 * @package jdk.packager.internal.legacy.builders.linux
 * @package jdk.packager.internal.legacy.builders.mac
 * @package jdk.packager.internal.legacy.builders.windows
 * @package jdk.packager.internal.legacy.mac
 * @package jdk.packager.internal.legacy.windows
 * @package jdk.packager.internal.resources.tools.legacy
 */
module jdk.packager {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	requires java.logging;
	requires java.xml;
	requires jdk.jlink;
	exports com.oracle.tools.packager;
	exports com.sun.javafx.tools.packager;
	exports com.sun.javafx.tools.packager.bundlers;
	exports com.sun.javafx.tools.resource;
	uses com.oracle.tools.packager.Bundler;
	uses com.oracle.tools.packager.Bundlers;
	provides com.oracle.tools.packager.Bundler with 
		com.oracle.tools.packager.jnlp.JNLPBundler,
		com.oracle.tools.packager.linux.LinuxAppBundler,
		com.oracle.tools.packager.linux.LinuxDebBundler,
		com.oracle.tools.packager.linux.LinuxRpmBundler,
		com.oracle.tools.packager.mac.MacAppBundler,
		com.oracle.tools.packager.mac.MacAppStoreBundler,
		com.oracle.tools.packager.mac.MacDmgBundler,
		com.oracle.tools.packager.mac.MacPkgBundler,
		com.oracle.tools.packager.windows.WinAppBundler,
		com.oracle.tools.packager.windows.WinExeBundler,
		com.oracle.tools.packager.windows.WinMsiBundler;
	provides com.oracle.tools.packager.Bundlers with 
		com.oracle.tools.packager.BasicBundlers;
	provides java.util.spi.ToolProvider with 
		jdk.packager.internal.JavaPackagerToolProvider;
}