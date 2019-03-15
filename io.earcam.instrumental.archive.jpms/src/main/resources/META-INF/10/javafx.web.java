/**
 * @version 10.0.2
 * @package com.sun.java.scene.web
 * @package com.sun.javafx.fxml.builder.web
 * @package com.sun.javafx.scene.web
 * @package com.sun.javafx.scene.web.behavior
 * @package com.sun.javafx.sg.prism.web
 * @package com.sun.javafx.webkit
 * @package com.sun.javafx.webkit.prism
 * @package com.sun.javafx.webkit.prism.resources
 * @package com.sun.javafx.webkit.prism.theme
 * @package com.sun.javafx.webkit.theme
 * @package com.sun.webkit
 * @package com.sun.webkit.dom
 * @package com.sun.webkit.event
 * @package com.sun.webkit.graphics
 * @package com.sun.webkit.network
 * @package com.sun.webkit.network.about
 * @package com.sun.webkit.network.data
 * @package com.sun.webkit.perf
 * @package com.sun.webkit.plugin
 * @package com.sun.webkit.text
 * @package javafx.scene.web
 */
module javafx.web {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	requires java.logging;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.controls;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.graphics;
	requires javafx.media;
	requires jdk.jsobject;
	requires jdk.xml.dom;
	exports com.sun.javafx.fxml.builder.web to 
		javafx.fxml;
	exports javafx.scene.web;
}