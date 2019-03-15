/**
 * @version 10.0.2
 * @package com.sun.security.auth
 * @package com.sun.security.auth.callback
 * @package com.sun.security.auth.login
 * @package com.sun.security.auth.module
 */
module jdk.security.auth {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.naming;
	requires java.security.jgss;
	exports com.sun.security.auth;
	exports com.sun.security.auth.callback;
	exports com.sun.security.auth.login;
	exports com.sun.security.auth.module;
	provides javax.security.auth.spi.LoginModule with 
		com.sun.security.auth.module.JndiLoginModule,
		com.sun.security.auth.module.KeyStoreLoginModule,
		com.sun.security.auth.module.Krb5LoginModule,
		com.sun.security.auth.module.LdapLoginModule,
		com.sun.security.auth.module.NTLoginModule,
		com.sun.security.auth.module.UnixLoginModule;
}