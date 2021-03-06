/**
 * @version 9.0.4
 * @package com.sun.jmx.snmp
 * @package com.sun.jmx.snmp.IPAcl
 * @package com.sun.jmx.snmp.agent
 * @package com.sun.jmx.snmp.daemon
 * @package com.sun.jmx.snmp.defaults
 * @package com.sun.jmx.snmp.internal
 * @package com.sun.jmx.snmp.mpm
 * @package com.sun.jmx.snmp.tasks
 * @package sun.management.snmp
 * @package sun.management.snmp.jvminstr
 * @package sun.management.snmp.jvmmib
 * @package sun.management.snmp.resources
 * @package sun.management.snmp.util
 */
module jdk.snmp {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.logging;
	requires java.management;
	requires jdk.management.agent;
	provides jdk.internal.agent.spi.AgentProvider with 
		sun.management.snmp.SnmpAgentProvider;
}