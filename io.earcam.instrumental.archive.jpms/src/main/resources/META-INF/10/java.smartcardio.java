/**
 * @version 10.0.2
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