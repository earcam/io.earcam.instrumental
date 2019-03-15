/**
 * @version 11.0.2
 * @package sun.tools.common
 * @package sun.tools.jcmd
 * @package sun.tools.jinfo
 * @package sun.tools.jmap
 * @package sun.tools.jps
 * @package sun.tools.jstack
 * @package sun.tools.jstat
 * @package sun.tools.jstat.resources
 */
module jdk.jcmd {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.attach;
	requires jdk.internal.jvmstat;
}