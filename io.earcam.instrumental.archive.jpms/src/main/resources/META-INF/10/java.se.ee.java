/**
 * @version 10.0.2
 */
module java.se.ee {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.activation;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.corba;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml.ws;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.se;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.transaction;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml.ws.annotation;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml.bind;
}