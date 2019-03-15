/**
 * @version 9.0.4
 * @package sun.security.tools.policytool
 */
module jdk.policytool {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	requires java.logging;
	requires java.management;
	requires java.security.jgss;
	requires java.sql;
	requires jdk.net;
	requires jdk.security.jgss;
}