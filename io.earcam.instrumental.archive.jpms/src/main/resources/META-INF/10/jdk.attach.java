/**
 * @version 10.0.2
 * @package com.sun.tools.attach
 * @package com.sun.tools.attach.spi
 * @package sun.tools.attach
 */
module jdk.attach {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.internal.jvmstat;
	exports com.sun.tools.attach;
	exports com.sun.tools.attach.spi;
	exports sun.tools.attach to 
		jdk.jcmd;
	uses com.sun.tools.attach.spi.AttachProvider;
	provides com.sun.tools.attach.spi.AttachProvider with 
		sun.tools.attach.AttachProviderImpl;
}