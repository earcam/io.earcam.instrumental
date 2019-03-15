/**
 * @version 10.0.2
 * @package sun.plugin2.server.jvm
 * @package sun.plugin2.server.main
 * @package sun.plugin2.server.util
 */
module jdk.plugin.server {
	requires jdk.deploy;
	requires jdk.plugin;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.javaws;
	requires jdk.jsobject;
}