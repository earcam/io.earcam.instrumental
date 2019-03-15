/**
 * @version 10.0.2
 * @package com.sun.javafx.fxml
 * @package com.sun.javafx.fxml.builder
 * @package com.sun.javafx.fxml.expression
 * @package javafx.fxml
 */
module javafx.fxml {
	requires java.xml;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.logging;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.base;
	requires java.scripting;
	requires javafx.graphics;
	exports javafx.fxml;
}