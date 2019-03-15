/**
 * @version 9.0.4
 * @package sun.jvmstat.monitor.remote
 * @package sun.jvmstat.perfdata.monitor.protocol.rmi
 * @package sun.tools.jstatd
 */
module jdk.jstatd {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.rmi;
	requires jdk.internal.jvmstat;
	exports sun.jvmstat.monitor.remote to 
		java.rmi;
	provides sun.jvmstat.monitor.MonitoredHostService with 
		sun.jvmstat.perfdata.monitor.protocol.rmi.MonitoredHostRmiService;
}