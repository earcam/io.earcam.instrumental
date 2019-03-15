/**
 * @version 9.0.4
 * @package com.sun.java.browser.plugin2
 * @package sun.plugin.dom
 * @package sun.plugin.dom.core
 * @package sun.plugin.dom.css
 * @package sun.plugin.dom.exception
 * @package sun.plugin.dom.html
 * @package sun.plugin.dom.html.common
 * @package sun.plugin.dom.stylesheets
 * @package sun.plugin.dom.views
 */
module jdk.plugin.dom {
	requires java.xml;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive jdk.xml.dom;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.desktop;
	/**
	 * @modifiers transitive
	 */
	requires transitive jdk.jsobject;
	exports com.sun.java.browser.plugin2;
}