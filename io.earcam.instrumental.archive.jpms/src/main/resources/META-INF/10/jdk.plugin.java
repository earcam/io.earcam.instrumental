/**
 * @version 10.0.2
 * @package com.sun.java.browser.plugin2.liveconnect.v1
 * @package sun.plugin
 * @package sun.plugin.javascript
 * @package sun.plugin.security
 * @package sun.plugin.services
 * @package sun.plugin.util
 * @package sun.plugin.viewer
 * @package sun.plugin.viewer.context
 * @package sun.plugin2.applet
 * @package sun.plugin2.applet.context
 * @package sun.plugin2.applet.viewer
 * @package sun.plugin2.applet.viewer.util
 * @package sun.plugin2.applet2
 * @package sun.plugin2.fxhooks
 * @package sun.plugin2.gluegen.runtime
 * @package sun.plugin2.ipc
 * @package sun.plugin2.ipc.unix
 * @package sun.plugin2.ipc.windows
 * @package sun.plugin2.jvm
 * @package sun.plugin2.liveconnect
 * @package sun.plugin2.main
 * @package sun.plugin2.main.client
 * @package sun.plugin2.message
 * @package sun.plugin2.message.helper
 * @package sun.plugin2.message.transport
 * @package sun.plugin2.os.windows
 * @package sun.plugin2.uitoolkit
 * @package sun.plugin2.uitoolkit.impl.awt
 * @package sun.plugin2.uitoolkit.ui
 * @package sun.plugin2.util
 */
module jdk.plugin {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	requires java.jnlp;
	requires java.logging;
	requires java.rmi;
	requires java.xml;
	requires jdk.deploy;
	requires jdk.javaws;
	requires jdk.jsobject;
	exports sun.plugin.services to 
		jdk.plugin.server;
	exports sun.plugin2.applet to 
		javafx.deploy,
		jdk.plugin.server;
	exports sun.plugin2.applet.viewer to 
		jdk.javaws;
	exports sun.plugin2.applet2 to 
		javafx.deploy;
	exports sun.plugin2.fxhooks to 
		javafx.deploy;
	exports sun.plugin2.ipc to 
		jdk.plugin.server;
	exports sun.plugin2.ipc.windows to 
		jdk.plugin.server;
	exports sun.plugin2.jvm to 
		jdk.plugin.server;
	exports sun.plugin2.liveconnect to 
		jdk.plugin.server;
	exports sun.plugin2.main.client to 
		javafx.deploy,
		jdk.javaws,
		jdk.plugin.server;
	exports sun.plugin2.message to 
		javafx.deploy,
		jdk.plugin.server;
	exports sun.plugin2.message.transport to 
		jdk.plugin.server;
	exports sun.plugin2.uitoolkit to 
		javafx.deploy,
		jdk.deploy;
	exports sun.plugin2.uitoolkit.impl.awt to 
		jdk.deploy;
	exports sun.plugin2.uitoolkit.ui to 
		javafx.deploy;
	exports sun.plugin2.util to 
		javafx.deploy,
		jdk.plugin.server;
	provides jdk.internal.netscape.javascript.spi.JSObjectProvider with 
		sun.plugin2.main.client.PluginJSObjectProvider;
}