/**
 * @version 10.0.2
 * @package com.sun.javafx
 * @package com.sun.javafx.beans
 * @package com.sun.javafx.binding
 * @package com.sun.javafx.collections
 * @package com.sun.javafx.event
 * @package com.sun.javafx.logging
 * @package com.sun.javafx.property
 * @package com.sun.javafx.property.adapter
 * @package com.sun.javafx.reflect
 * @package com.sun.javafx.runtime
 * @package javafx.beans
 * @package javafx.beans.binding
 * @package javafx.beans.property
 * @package javafx.beans.property.adapter
 * @package javafx.beans.value
 * @package javafx.collections
 * @package javafx.collections.transformation
 * @package javafx.event
 * @package javafx.util
 * @package javafx.util.converter
 */
module javafx.base {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	exports javafx.beans.property.adapter;
	exports javafx.util;
	exports com.sun.javafx.runtime to 
		javafx.graphics;
	exports com.sun.javafx to 
		javafx.controls,
		javafx.fxml,
		javafx.graphics,
		javafx.swing;
	exports javafx.event;
	exports com.sun.javafx.property to 
		javafx.controls;
	exports javafx.collections.transformation;
	exports com.sun.javafx.logging to 
		javafx.graphics;
	exports javafx.util.converter;
	exports com.sun.javafx.beans to 
		javafx.controls,
		javafx.fxml,
		javafx.graphics;
	exports javafx.beans;
	exports com.sun.javafx.reflect to 
		javafx.fxml,
		javafx.web;
	exports com.sun.javafx.binding to 
		javafx.controls,
		javafx.graphics;
	exports com.sun.javafx.collections to 
		javafx.controls,
		javafx.graphics,
		javafx.media,
		javafx.swing;
	exports com.sun.javafx.event to 
		javafx.controls,
		javafx.graphics;
	exports javafx.beans.binding;
	exports javafx.collections;
	exports javafx.beans.value;
	exports javafx.beans.property;
}