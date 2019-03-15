/**
 * @version 10.0.2
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