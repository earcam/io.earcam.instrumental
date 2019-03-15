/**
 * @version 9.0.4
 * @package com.sun.rowset
 * @package com.sun.rowset.internal
 * @package com.sun.rowset.providers
 * @package javax.sql.rowset
 * @package javax.sql.rowset.serial
 * @package javax.sql.rowset.spi
 */
module java.sql.rowset {
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
	requires transitive java.naming;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.sql;
	exports javax.sql.rowset;
	exports javax.sql.rowset.serial;
	exports javax.sql.rowset.spi;
	uses javax.sql.rowset.RowSetFactory;
}