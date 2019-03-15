/**
 * @version 10.0.2
 * @package jdk.packager.services
 * @package jdk.packager.services.singleton
 * @package jdk.packager.services.userjvmoptions
 */
module jdk.packager.services {
	requires java.prefs;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	exports jdk.packager.services.singleton;
	exports jdk.packager.services;
	uses jdk.packager.services.UserJvmOptionsService;
	provides jdk.packager.services.UserJvmOptionsService with 
		jdk.packager.services.userjvmoptions.LauncherUserJvmOptions;
}