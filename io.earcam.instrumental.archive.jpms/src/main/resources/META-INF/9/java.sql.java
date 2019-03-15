/**
 * @version 9.0.4
 * @package java.sql
 * @package javax.sql
 * @package javax.transaction.xa
 */
module java.sql {
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
	exports javax.transaction.xa;
	uses java.sql.Driver;
}