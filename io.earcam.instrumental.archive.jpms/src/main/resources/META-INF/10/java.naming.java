/**
 * @version 10.0.2
 * @package com.sun.jndi.ldap
 * @package com.sun.jndi.ldap.ext
 * @package com.sun.jndi.ldap.pool
 * @package com.sun.jndi.ldap.sasl
 * @package com.sun.jndi.toolkit.ctx
 * @package com.sun.jndi.toolkit.dir
 * @package com.sun.jndi.toolkit.url
 * @package com.sun.jndi.url.ldap
 * @package com.sun.jndi.url.ldaps
 * @package com.sun.naming.internal
 * @package javax.naming
 * @package javax.naming.directory
 * @package javax.naming.event
 * @package javax.naming.ldap
 * @package javax.naming.spi
 * @package sun.security.provider.certpath.ldap
 */
module java.naming {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.security.sasl;
	exports com.sun.jndi.toolkit.ctx to 
		jdk.naming.dns;
	exports com.sun.jndi.toolkit.url to 
		jdk.naming.dns,
		jdk.naming.rmi;
	exports javax.naming;
	exports javax.naming.directory;
	exports javax.naming.event;
	exports javax.naming.ldap;
	exports javax.naming.spi;
	uses javax.naming.ldap.StartTlsResponse;
	uses javax.naming.spi.InitialContextFactory;
	provides java.security.Provider with 
		sun.security.provider.certpath.ldap.JdkLDAP;
}