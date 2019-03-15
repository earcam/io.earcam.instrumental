/**
 * @version 10.0.2
 * @package jdk.tools.jaotc
 * @package jdk.tools.jaotc.amd64
 * @package jdk.tools.jaotc.binformat
 * @package jdk.tools.jaotc.binformat.elf
 * @package jdk.tools.jaotc.binformat.macho
 * @package jdk.tools.jaotc.binformat.pecoff
 * @package jdk.tools.jaotc.collect
 * @package jdk.tools.jaotc.collect.classname
 * @package jdk.tools.jaotc.collect.directory
 * @package jdk.tools.jaotc.collect.jar
 * @package jdk.tools.jaotc.collect.module
 * @package jdk.tools.jaotc.utils
 */
module jdk.aot {
	requires jdk.internal.vm.ci;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.internal.vm.compiler;
	requires jdk.management;
}