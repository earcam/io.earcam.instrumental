/**
 * @version 12
 * @package sun.jvmstat.monitor.remote
 * @package sun.jvmstat.perfdata.monitor.protocol.rmi
 * @package sun.tools.jstatd
 */
module jdk.jstatd {
	requires jdk.internal.jvmstat;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.rmi;
	exports sun.jvmstat.monitor.remote to 
		java.rmi;
	provides sun.jvmstat.monitor.MonitoredHostService with 
		sun.jvmstat.perfdata.monitor.protocol.rmi.MonitoredHostRmiService;
}