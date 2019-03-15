/**
 * @version 11.0.2
 * @package javax.security.auth.kerberos
 * @package org.ietf.jgss
 * @package sun.net.www.protocol.http.spnego
 * @package sun.security.jgss
 * @package sun.security.jgss.krb5
 * @package sun.security.jgss.spi
 * @package sun.security.jgss.spnego
 * @package sun.security.jgss.wrapper
 * @package sun.security.krb5
 * @package sun.security.krb5.internal
 * @package sun.security.krb5.internal.ccache
 * @package sun.security.krb5.internal.crypto
 * @package sun.security.krb5.internal.crypto.dk
 * @package sun.security.krb5.internal.ktab
 * @package sun.security.krb5.internal.rcache
 * @package sun.security.krb5.internal.util
 */
module java.security.jgss {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.naming;
	exports sun.security.krb5.internal.ktab to 
		jdk.security.auth;
	exports sun.security.jgss to 
		jdk.security.jgss;
	exports sun.security.jgss.krb5 to 
		jdk.security.auth;
	exports sun.security.krb5 to 
		jdk.security.auth;
	exports sun.security.krb5.internal to 
		jdk.security.jgss;
	exports javax.security.auth.kerberos;
	exports org.ietf.jgss;
	opens sun.net.www.protocol.http.spnego to 
		java.base;
	provides java.security.Provider with 
		sun.security.jgss.SunProvider;
}