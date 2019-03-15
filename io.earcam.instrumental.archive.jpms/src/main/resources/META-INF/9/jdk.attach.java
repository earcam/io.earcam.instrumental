/**
 * @version 9.0.4
 * @package com.sun.tools.attach
 * @package com.sun.tools.attach.spi
 * @package sun.tools.attach
 */
module jdk.attach {
	requires jdk.internal.jvmstat;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports com.sun.tools.attach;
	exports sun.tools.attach to 
		jdk.jcmd;
	exports com.sun.tools.attach.spi;
	uses com.sun.tools.attach.spi.AttachProvider;
	provides com.sun.tools.attach.spi.AttachProvider with 
		sun.tools.attach.AttachProviderImpl;
}