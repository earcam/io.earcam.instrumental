/**
 * @version 11.0.2
 * @package java.sql
 * @package javax.sql
 */
module java.sql {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.logging;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.transaction.xa;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml;
	exports java.sql;
	exports javax.sql;
	uses java.sql.Driver;
}