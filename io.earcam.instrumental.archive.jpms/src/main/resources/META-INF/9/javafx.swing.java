/**
 * @version 9.0.4
 * @package com.sun.javafx.embed.swing
 * @package javafx.embed.swing
 */
module javafx.swing {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.datatransfer;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.desktop;
	requires javafx.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.graphics;
	exports com.sun.javafx.embed.swing to 
		javafx.graphics;
	exports javafx.embed.swing;
}