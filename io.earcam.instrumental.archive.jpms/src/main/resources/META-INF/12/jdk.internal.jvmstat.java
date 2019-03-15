/**
 * @version 12
 * @package sun.jvmstat
 * @package sun.jvmstat.monitor
 * @package sun.jvmstat.monitor.event
 * @package sun.jvmstat.perfdata.monitor
 * @package sun.jvmstat.perfdata.monitor.protocol.file
 * @package sun.jvmstat.perfdata.monitor.protocol.local
 * @package sun.jvmstat.perfdata.monitor.v1_0
 * @package sun.jvmstat.perfdata.monitor.v2_0
 * @package sun.jvmstat.perfdata.resources
 */
module jdk.internal.jvmstat {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports sun.jvmstat.monitor to 
		jdk.attach,
		jdk.jcmd,
		jdk.jconsole,
		jdk.jstatd;
	exports sun.jvmstat.monitor.event to 
		jdk.jcmd,
		jdk.jstatd;
	exports sun.jvmstat.perfdata.monitor to 
		jdk.jstatd;
	uses sun.jvmstat.monitor.MonitoredHostService;
	provides sun.jvmstat.monitor.MonitoredHostService with 
		sun.jvmstat.perfdata.monitor.protocol.file.MonitoredHostFileService,
		sun.jvmstat.perfdata.monitor.protocol.local.MonitoredHostLocalService;
}