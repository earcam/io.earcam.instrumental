/**
 * @version 10.0.2
 * @package jdk.packager.services
 * @package jdk.packager.services.singleton
 * @package jdk.packager.services.userjvmoptions
 */
module jdk.packager.services {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	requires java.prefs;
	exports jdk.packager.services;
	exports jdk.packager.services.singleton;
	uses jdk.packager.services.UserJvmOptionsService;
	provides jdk.packager.services.UserJvmOptionsService with 
		jdk.packager.services.userjvmoptions.LauncherUserJvmOptions;
}