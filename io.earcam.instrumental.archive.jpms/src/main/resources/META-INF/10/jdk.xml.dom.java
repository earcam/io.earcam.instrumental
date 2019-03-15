/**
 * @version 10.0.2
 * @package org.w3c.dom.css
 * @package org.w3c.dom.html
 * @package org.w3c.dom.stylesheets
 * @package org.w3c.dom.xpath
 */
module jdk.xml.dom {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml;
	exports org.w3c.dom.css;
	exports org.w3c.dom.html;
	exports org.w3c.dom.stylesheets;
	exports org.w3c.dom.xpath;
}