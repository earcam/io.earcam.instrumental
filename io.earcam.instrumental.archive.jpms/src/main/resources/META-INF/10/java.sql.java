/**
 * @version 10.0.2
 * @package java.sql
 * @package javax.sql
 * @package javax.transaction.xa
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
	requires transitive java.xml;
	exports java.sql;
	exports javax.sql;
	exports javax.transaction.xa;
	uses java.sql.Driver;
}