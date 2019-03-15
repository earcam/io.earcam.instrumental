/**
 * @version 9.0.4
 * @package com.oracle.deploy.update
 * @package com.sun.applet2
 * @package com.sun.applet2.preloader
 * @package com.sun.applet2.preloader.event
 * @package com.sun.deploy
 * @package com.sun.deploy.appcontext
 * @package com.sun.deploy.association
 * @package com.sun.deploy.association.utility
 * @package com.sun.deploy.cache
 * @package com.sun.deploy.config
 * @package com.sun.deploy.jardiff
 * @package com.sun.deploy.model
 * @package com.sun.deploy.nativesandbox
 * @package com.sun.deploy.net
 * @package com.sun.deploy.net.cookie
 * @package com.sun.deploy.net.offline
 * @package com.sun.deploy.net.protocol
 * @package com.sun.deploy.net.protocol.about
 * @package com.sun.deploy.net.protocol.chrome
 * @package com.sun.deploy.net.protocol.https
 * @package com.sun.deploy.net.protocol.jar
 * @package com.sun.deploy.net.protocol.javascript
 * @package com.sun.deploy.net.protocol.jnlp
 * @package com.sun.deploy.net.protocol.jnlps
 * @package com.sun.deploy.net.proxy
 * @package com.sun.deploy.net.proxy.pac
 * @package com.sun.deploy.net.socket
 * @package com.sun.deploy.ref
 * @package com.sun.deploy.registration
 * @package com.sun.deploy.resources
 * @package com.sun.deploy.resources.image
 * @package com.sun.deploy.security
 * @package com.sun.deploy.security.ruleset
 * @package com.sun.deploy.services
 * @package com.sun.deploy.si
 * @package com.sun.deploy.trace
 * @package com.sun.deploy.ui
 * @package com.sun.deploy.uitoolkit
 * @package com.sun.deploy.uitoolkit.impl.awt
 * @package com.sun.deploy.uitoolkit.impl.awt.ui
 * @package com.sun.deploy.uitoolkit.ui
 * @package com.sun.deploy.util
 * @package com.sun.deploy.xdg
 * @package com.sun.deploy.xml
 */
module jdk.deploy {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	requires java.logging;
	requires java.management;
	requires java.naming;
	requires java.prefs;
	requires java.rmi;
	requires java.scripting;
	requires java.xml;
	requires jdk.unsupported;
	exports com.oracle.deploy.update to 
		jdk.deploy.controlpanel;
	exports com.sun.applet2 to 
		javafx.deploy,
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.applet2.preloader to 
		javafx.deploy,
		jdk.javaws,
		jdk.plugin;
	exports com.sun.applet2.preloader.event to 
		javafx.deploy,
		jdk.javaws,
		jdk.plugin;
	exports com.sun.deploy to 
		javafx.deploy,
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.deploy.appcontext to 
		javafx.deploy,
		jdk.javaws,
		jdk.plugin;
	exports com.sun.deploy.association to 
		jdk.javaws;
	exports com.sun.deploy.association.utility to 
		jdk.javaws;
	exports com.sun.deploy.cache to 
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin;
	exports com.sun.deploy.config to 
		javafx.deploy,
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.deploy.model to 
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin;
	exports com.sun.deploy.nativesandbox to 
		jdk.javaws,
		jdk.plugin;
	exports com.sun.deploy.net to 
		javafx.deploy,
		jdk.javaws,
		jdk.plugin;
	exports com.sun.deploy.net.cookie to 
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.deploy.net.offline to 
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.deploy.net.proxy to 
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.deploy.net.socket to 
		jdk.plugin;
	exports com.sun.deploy.ref to 
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.deploy.registration to 
		jdk.deploy.controlpanel;
	exports com.sun.deploy.resources to 
		javafx.deploy,
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin;
	exports com.sun.deploy.security to 
		javafx.deploy,
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.deploy.security.ruleset to 
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.deploy.services to 
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.deploy.si to 
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin;
	exports com.sun.deploy.trace to 
		javafx.deploy,
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.deploy.ui to 
		javafx.deploy,
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin;
	exports com.sun.deploy.uitoolkit to 
		javafx.deploy,
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin;
	exports com.sun.deploy.uitoolkit.impl.awt to 
		jdk.plugin;
	exports com.sun.deploy.uitoolkit.impl.awt.ui to 
		jdk.plugin;
	exports com.sun.deploy.uitoolkit.ui to 
		javafx.deploy,
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin;
	exports com.sun.deploy.util to 
		javafx.deploy,
		jdk.deploy.controlpanel,
		jdk.javaws,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.deploy.xdg to 
		jdk.javaws;
	exports com.sun.deploy.xml to 
		jdk.javaws;
	provides java.security.Provider with 
		com.sun.deploy.security.MozillaJSSProvider;
}