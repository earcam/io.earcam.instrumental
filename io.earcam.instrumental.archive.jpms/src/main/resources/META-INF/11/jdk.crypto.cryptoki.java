/**
 * @version 11.0.2
 * @package sun.security.pkcs11
 * @package sun.security.pkcs11.wrapper
 */
module jdk.crypto.cryptoki {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.crypto.ec;
	provides java.security.Provider with 
		sun.security.pkcs11.SunPKCS11;
}