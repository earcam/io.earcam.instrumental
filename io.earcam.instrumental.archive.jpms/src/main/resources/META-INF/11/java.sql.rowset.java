/**
 * @version 11.0.2
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
	requires transitive java.naming;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.sql;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.logging;
	exports javax.sql.rowset.serial;
	exports javax.sql.rowset.spi;
	exports javax.sql.rowset;
	uses javax.sql.rowset.RowSetFactory;
}