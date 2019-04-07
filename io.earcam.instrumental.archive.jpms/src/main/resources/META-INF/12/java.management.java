/**
 * @version 12
 * @package com.sun.jmx.defaults
 * @package com.sun.jmx.interceptor
 * @package com.sun.jmx.mbeanserver
 * @package com.sun.jmx.remote.internal
 * @package com.sun.jmx.remote.security
 * @package com.sun.jmx.remote.util
 * @package java.lang.management
 * @package javax.management
 * @package javax.management.loading
 * @package javax.management.modelmbean
 * @package javax.management.monitor
 * @package javax.management.openmbean
 * @package javax.management.relation
 * @package javax.management.remote
 * @package javax.management.timer
 * @package sun.management
 * @package sun.management.counter
 * @package sun.management.counter.perf
 * @package sun.management.spi
 */
module java.management {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports com.sun.jmx.remote.internal to 
		java.management.rmi,
		jdk.management.agent;
	exports com.sun.jmx.remote.security to 
		java.management.rmi,
		jdk.management.agent;
	exports com.sun.jmx.remote.util to 
		java.management.rmi;
	exports java.lang.management;
	exports javax.management;
	exports javax.management.loading;
	exports javax.management.modelmbean;
	exports javax.management.monitor;
	exports javax.management.openmbean;
	exports javax.management.relation;
	exports javax.management.remote;
	exports javax.management.timer;
	exports sun.management to 
		jdk.jconsole,
		jdk.management,
		jdk.management.agent;
	exports sun.management.counter to 
		jdk.management.agent;
	exports sun.management.counter.perf to 
		jdk.management.agent;
	exports sun.management.spi to 
		jdk.management,
		jdk.management.jfr;
	uses javax.management.remote.JMXConnectorProvider;
	uses javax.management.remote.JMXConnectorServerProvider;
	uses sun.management.spi.PlatformMBeanProvider;
	provides javax.security.auth.spi.LoginModule with 
		com.sun.jmx.remote.security.FileLoginModule;
}