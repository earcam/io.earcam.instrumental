/**
 * @version 9.0.4
 * @package sun.nio.cs.ext
 */
module jdk.charsets {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	provides java.nio.charset.spi.CharsetProvider with 
		sun.nio.cs.ext.ExtendedCharsets;
}