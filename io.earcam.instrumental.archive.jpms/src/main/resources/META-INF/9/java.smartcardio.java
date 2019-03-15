/**
 * @version 9.0.4
 * @package javax.smartcardio
 * @package sun.security.smartcardio
 */
module java.smartcardio {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports javax.smartcardio;
	provides java.security.Provider with 
		sun.security.smartcardio.SunPCSC;
}