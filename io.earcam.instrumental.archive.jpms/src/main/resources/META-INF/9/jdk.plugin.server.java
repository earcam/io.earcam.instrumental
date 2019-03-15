/**
 * @version 9.0.4
 * @package sun.plugin2.server.jvm
 * @package sun.plugin2.server.main
 * @package sun.plugin2.server.util
 */
module jdk.plugin.server {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.deploy;
	requires jdk.javaws;
	requires jdk.jsobject;
	requires jdk.plugin;
}