/**
 * @version 10.0.2
 * @package com.sun.deploy.uitoolkit.impl.fx
 * @package com.sun.deploy.uitoolkit.impl.fx.ui
 * @package com.sun.deploy.uitoolkit.impl.fx.ui.resources
 * @package com.sun.deploy.uitoolkit.impl.fx.ui.resources.image
 * @package com.sun.javafx.applet
 */
module javafx.deploy {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires jdk.deploy;
	requires jdk.jsobject;
	requires jdk.plugin;
	exports com.sun.deploy.uitoolkit.impl.fx to 
		javafx.controls,
		javafx.graphics,
		jdk.plugin;
	exports com.sun.deploy.uitoolkit.impl.fx.ui to 
		jdk.plugin;
	exports com.sun.javafx.applet to 
		jdk.plugin;
}