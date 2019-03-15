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
	 * @modifiers transitive
	 */
	requires transitive java.xml;
	requires jdk.xml.dom;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.controls;
	requires java.logging;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.base;
	requires jdk.jsobject;
	requires javafx.media;
	requires java.desktop;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.graphics;
	exports javafx.scene.web;
	exports com.sun.javafx.fxml.builder.web to 
		javafx.fxml;
}