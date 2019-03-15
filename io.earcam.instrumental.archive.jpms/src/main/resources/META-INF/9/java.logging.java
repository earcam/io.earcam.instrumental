/**
 * @version 9.0.4
 * @package java.util.logging
 * @package sun.net.www.protocol.http.logging
 * @package sun.util.logging.internal
 * @package sun.util.logging.resources
 */
module java.logging {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports java.util.logging;
	provides jdk.internal.logger.DefaultLoggerFinder with 
		sun.util.logging.internal.LoggingProviderImpl;
}