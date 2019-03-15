/**
 * @version 9.0.4
 * @package com.sun.javaws
 * @package com.sun.javaws.exceptions
 * @package com.sun.javaws.jnl
 * @package com.sun.javaws.progress
 * @package com.sun.javaws.security
 * @package com.sun.javaws.ui
 * @package com.sun.javaws.util
 * @package com.sun.javaws.xdg
 * @package com.sun.jnlp
 */
module jdk.javaws {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.datatransfer;
	requires java.desktop;
	requires java.jnlp;
	requires java.logging;
	requires java.prefs;
	requires java.rmi;
	requires java.xml;
	requires jdk.deploy;
	exports com.sun.javaws to 
		jdk.deploy.controlpanel,
		jdk.plugin;
	exports com.sun.javaws.exceptions to 
		jdk.plugin;
	exports com.sun.javaws.jnl to 
		jdk.deploy.controlpanel,
		jdk.plugin,
		jdk.plugin.server;
	exports com.sun.javaws.progress to 
		javafx.deploy,
		jdk.plugin;
	exports com.sun.javaws.security to 
		jdk.plugin;
	exports com.sun.javaws.ui to 
		jdk.plugin;
	exports com.sun.javaws.util to 
		jdk.plugin;
	exports com.sun.jnlp to 
		java.rmi,
		jdk.plugin;
}