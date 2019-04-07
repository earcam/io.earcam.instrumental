/**
 * @version 12
 * @package java.lang.instrument
 * @package sun.instrument
 */
module java.instrument {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports java.lang.instrument;
	exports sun.instrument to 
		java.base;
}