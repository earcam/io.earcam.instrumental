/**
 * @version 9.0.4
 * @package sun.rmi.rmic
 * @package sun.rmi.rmic.iiop
 * @package sun.rmi.rmic.newrmic
 * @package sun.rmi.rmic.newrmic.jrmp
 * @package sun.rmi.rmic.resources
 * @package sun.tools.asm
 * @package sun.tools.java
 * @package sun.tools.javac
 * @package sun.tools.javac.resources
 * @package sun.tools.tree
 * @package sun.tools.util
 */
module jdk.rmic {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.corba;
	requires jdk.compiler;
	requires jdk.javadoc;
}