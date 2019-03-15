/**
 * @version 9.0.4
 * @package javax.transaction
 */
module java.transaction {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.rmi;
	exports javax.transaction;
}