/**
 * @version 11.0.2
 * @package java.util.prefs
 */
module java.prefs {
	requires java.xml;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports java.util.prefs;
	uses java.util.prefs.PreferencesFactory;
}