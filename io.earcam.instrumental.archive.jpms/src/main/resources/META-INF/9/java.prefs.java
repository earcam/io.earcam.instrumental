/**
 * @version 9.0.4
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