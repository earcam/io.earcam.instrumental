/**
 * @version 9.0.4
 * @package com.sun.javafx.fxml
 * @package com.sun.javafx.fxml.builder
 * @package com.sun.javafx.fxml.expression
 * @package javafx.fxml
 */
module javafx.fxml {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.logging;
	requires java.scripting;
	requires java.xml;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.base;
	requires javafx.graphics;
	exports javafx.fxml;
}