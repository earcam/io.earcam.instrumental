/**
 * @version 9.0.4
 * @package sun.security.ec
 */
module jdk.crypto.ec {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	provides java.security.Provider with 
		sun.security.ec.SunEC;
}