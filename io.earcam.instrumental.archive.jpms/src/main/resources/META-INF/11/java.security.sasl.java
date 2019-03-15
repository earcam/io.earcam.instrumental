/**
 * @version 11.0.2
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
	exports javax.security.sasl;
	exports com.sun.security.sasl.util to 
		jdk.security.jgss;
	provides java.security.Provider with 
		com.sun.security.sasl.Provider;
}