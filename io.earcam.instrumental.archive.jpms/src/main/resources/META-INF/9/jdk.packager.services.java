/**
 * @version 9.0.4
 * @package jdk.packager.services
 * @package jdk.packager.services.userjvmoptions
 */
module jdk.packager.services {
	requires java.prefs;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports jdk.packager.services;
	uses jdk.packager.services.UserJvmOptionsService;
	provides jdk.packager.services.UserJvmOptionsService with 
		jdk.packager.services.userjvmoptions.LauncherUserJvmOptions;
}