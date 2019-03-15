/**
 * @version 10.0.2
 * @package com.sun.javafx.charts
 * @package com.sun.javafx.scene.control
 * @package com.sun.javafx.scene.control.behavior
 * @package com.sun.javafx.scene.control.inputmap
 * @package com.sun.javafx.scene.control.skin
 * @package com.sun.javafx.scene.control.skin.caspian
 * @package com.sun.javafx.scene.control.skin.caspian.images
 * @package com.sun.javafx.scene.control.skin.modena
 * @package com.sun.javafx.scene.control.skin.resources
 * @package javafx.scene.chart
 * @package javafx.scene.control
 * @package javafx.scene.control.cell
 * @package javafx.scene.control.skin
 */
module javafx.controls {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.graphics;
	exports javafx.scene.control.skin;
	exports com.sun.javafx.scene.control.behavior to 
		javafx.web;
	exports com.sun.javafx.scene.control.skin to 
		javafx.graphics,
		javafx.web;
	exports javafx.scene.control.cell;
	exports javafx.scene.chart;
	exports com.sun.javafx.scene.control to 
		javafx.web;
	exports com.sun.javafx.scene.control.inputmap to 
		javafx.web;
	exports javafx.scene.control;
}