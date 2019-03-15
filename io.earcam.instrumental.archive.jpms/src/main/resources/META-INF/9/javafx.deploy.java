/**
 * @version 9.0.4
 * @package com.sun.deploy.uitoolkit.impl.fx
 * @package com.sun.deploy.uitoolkit.impl.fx.ui
 * @package com.sun.deploy.uitoolkit.impl.fx.ui.resources
 * @package com.sun.deploy.uitoolkit.impl.fx.ui.resources.image
 * @package com.sun.javafx.applet
 */
module javafx.deploy {
	requires jdk.deploy;
	requires jdk.plugin;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires javafx.controls;
	requires jdk.jsobject;
	requires java.desktop;
	requires javafx.base;
	requires javafx.graphics;
	exports com.sun.deploy.uitoolkit.impl.fx to 
		javafx.controls,
		javafx.graphics,
		jdk.plugin;
	exports com.sun.deploy.uitoolkit.impl.fx.ui to 
		jdk.plugin;
	exports com.sun.javafx.applet to 
		jdk.plugin;
}