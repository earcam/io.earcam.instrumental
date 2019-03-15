/**
 * @version 12
 * @package sun.security.ec
 * @package sun.security.ec.point
 */
module jdk.crypto.ec {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	provides java.security.Provider with 
		sun.security.ec.SunEC;
}