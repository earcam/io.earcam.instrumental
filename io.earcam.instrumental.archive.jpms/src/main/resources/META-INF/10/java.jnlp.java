/**
 * @version 10.0.2
 * @package javax.jnlp
 */
module java.jnlp {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.datatransfer;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.desktop;
	exports javax.jnlp;
}