/**
 * @version 12
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
	requires jdk.internal.jvmstat;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.attach;
}