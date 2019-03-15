/**
 * @version 11.0.2
 * @package java.util.prefs
 */
module java.prefs {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.xml;
	exports java.util.prefs;
	uses java.util.prefs.PreferencesFactory;
}