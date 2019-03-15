/**
 * @version 9.0.4
 * @package com.sun.net.httpserver
 * @package com.sun.net.httpserver.spi
 * @package sun.net.httpserver
 */
module jdk.httpserver {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports com.sun.net.httpserver;
	exports com.sun.net.httpserver.spi;
	uses com.sun.net.httpserver.spi.HttpServerProvider;
}