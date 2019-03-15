/**
 * @version 10.0.2
 * @package com.sun.jdi
 * @package com.sun.jdi.connect
 * @package com.sun.jdi.connect.spi
 * @package com.sun.jdi.event
 * @package com.sun.jdi.request
 * @package com.sun.tools.example.debug.expr
 * @package com.sun.tools.example.debug.tty
 * @package com.sun.tools.jdi
 * @package com.sun.tools.jdi.resources
 */
module jdk.jdi {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.jdwp.agent;
	requires jdk.attach;
	exports com.sun.jdi.connect.spi;
	exports com.sun.jdi.connect;
	exports com.sun.jdi.request;
	exports com.sun.jdi;
	exports com.sun.jdi.event;
	uses com.sun.jdi.connect.Connector;
	uses com.sun.jdi.connect.spi.TransportService;
	provides com.sun.jdi.connect.Connector with 
		com.sun.tools.jdi.ProcessAttachingConnector,
		com.sun.tools.jdi.RawCommandLineLauncher,
		com.sun.tools.jdi.SocketAttachingConnector,
		com.sun.tools.jdi.SocketListeningConnector,
		com.sun.tools.jdi.SunCommandLineLauncher;
}