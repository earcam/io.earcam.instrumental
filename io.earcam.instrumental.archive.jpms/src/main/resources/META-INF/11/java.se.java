/**
 * @version 11.0.2
 */
module java.se {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.transaction.xa;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.compiler;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.security.jgss;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.instrument;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.naming;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.sql;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.security.sasl;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.logging;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.datatransfer;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.desktop;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.net.http;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml.crypto;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.prefs;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.rmi;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.sql.rowset;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.management.rmi;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.scripting;
}