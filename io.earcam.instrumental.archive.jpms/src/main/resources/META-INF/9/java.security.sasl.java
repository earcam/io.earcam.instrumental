/**
 * @version 9.0.4
 * @package com.sun.security.sasl
 * @package com.sun.security.sasl.digest
 * @package com.sun.security.sasl.ntlm
 * @package com.sun.security.sasl.util
 * @package javax.security.sasl
 */
module java.security.sasl {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.logging;
	exports com.sun.security.sasl.util to 
		jdk.security.jgss;
	exports javax.security.sasl;
	provides java.security.Provider with 
		com.sun.security.sasl.Provider;
}