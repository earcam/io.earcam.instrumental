/**
 * @version 9.0.4
 * @package sun.security.tools.policytool
 */
module jdk.policytool {
	requires java.management;
	requires java.security.jgss;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.sql;
	requires jdk.security.jgss;
	requires java.logging;
	requires jdk.net;
	requires java.desktop;
}