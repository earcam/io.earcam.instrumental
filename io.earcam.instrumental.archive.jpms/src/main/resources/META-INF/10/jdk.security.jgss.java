/**
 * @version 10.0.2
 * @package com.sun.security.jgss
 * @package com.sun.security.sasl.gsskerb
 */
module jdk.security.jgss {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.security.jgss;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.security.sasl;
	requires java.logging;
	exports com.sun.security.jgss;
	provides java.security.Provider with 
		com.sun.security.sasl.gsskerb.JdkSASL;
}