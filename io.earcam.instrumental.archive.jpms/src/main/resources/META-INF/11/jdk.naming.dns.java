/**
 * @version 11.0.2
 * @package com.sun.jndi.dns
 * @package com.sun.jndi.url.dns
 */
module jdk.naming.dns {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.naming;
	exports com.sun.jndi.url.dns to 
		java.naming;
	provides javax.naming.spi.InitialContextFactory with 
		com.sun.jndi.dns.DnsContextFactory;
}