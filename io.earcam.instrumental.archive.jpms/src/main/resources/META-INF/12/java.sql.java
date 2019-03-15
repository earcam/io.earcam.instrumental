/**
 * @version 12
 * @package java.sql
 * @package javax.sql
 */
module java.sql {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.transaction.xa;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.logging;
	exports javax.sql;
	exports java.sql;
	uses java.sql.Driver;
}