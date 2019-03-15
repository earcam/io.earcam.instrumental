/**
 * @version 12
 * @package com.sun.java.swing.action
 * @package com.sun.java.swing.ui
 * @package images.toolbarButtonGraphics.general
 * @package sun.jvm.hotspot
 * @package sun.jvm.hotspot.asm
 * @package sun.jvm.hotspot.asm.sparc
 * @package sun.jvm.hotspot.c1
 * @package sun.jvm.hotspot.ci
 * @package sun.jvm.hotspot.classfile
 * @package sun.jvm.hotspot.code
 * @package sun.jvm.hotspot.compiler
 * @package sun.jvm.hotspot.debugger
 * @package sun.jvm.hotspot.debugger.aarch64
 * @package sun.jvm.hotspot.debugger.amd64
 * @package sun.jvm.hotspot.debugger.bsd
 * @package sun.jvm.hotspot.debugger.bsd.amd64
 * @package sun.jvm.hotspot.debugger.bsd.x86
 * @package sun.jvm.hotspot.debugger.cdbg
 * @package sun.jvm.hotspot.debugger.cdbg.basic
 * @package sun.jvm.hotspot.debugger.dummy
 * @package sun.jvm.hotspot.debugger.linux
 * @package sun.jvm.hotspot.debugger.linux.aarch64
 * @package sun.jvm.hotspot.debugger.linux.amd64
 * @package sun.jvm.hotspot.debugger.linux.ppc64
 * @package sun.jvm.hotspot.debugger.linux.sparc
 * @package sun.jvm.hotspot.debugger.linux.x86
 * @package sun.jvm.hotspot.debugger.posix
 * @package sun.jvm.hotspot.debugger.posix.elf
 * @package sun.jvm.hotspot.debugger.ppc64
 * @package sun.jvm.hotspot.debugger.proc
 * @package sun.jvm.hotspot.debugger.proc.aarch64
 * @package sun.jvm.hotspot.debugger.proc.amd64
 * @package sun.jvm.hotspot.debugger.proc.ppc64
 * @package sun.jvm.hotspot.debugger.proc.sparc
 * @package sun.jvm.hotspot.debugger.proc.x86
 * @package sun.jvm.hotspot.debugger.remote
 * @package sun.jvm.hotspot.debugger.remote.aarch64
 * @package sun.jvm.hotspot.debugger.remote.amd64
 * @package sun.jvm.hotspot.debugger.remote.ppc64
 * @package sun.jvm.hotspot.debugger.remote.sparc
 * @package sun.jvm.hotspot.debugger.remote.x86
 * @package sun.jvm.hotspot.debugger.sparc
 * @package sun.jvm.hotspot.debugger.win32.coff
 * @package sun.jvm.hotspot.debugger.windbg
 * @package sun.jvm.hotspot.debugger.windbg.amd64
 * @package sun.jvm.hotspot.debugger.windbg.x86
 * @package sun.jvm.hotspot.debugger.windows.amd64
 * @package sun.jvm.hotspot.debugger.windows.x86
 * @package sun.jvm.hotspot.debugger.x86
 * @package sun.jvm.hotspot.gc.cms
 * @package sun.jvm.hotspot.gc.epsilon
 * @package sun.jvm.hotspot.gc.g1
 * @package sun.jvm.hotspot.gc.parallel
 * @package sun.jvm.hotspot.gc.serial
 * @package sun.jvm.hotspot.gc.shared
 * @package sun.jvm.hotspot.gc.shenandoah
 * @package sun.jvm.hotspot.gc.z
 * @package sun.jvm.hotspot.interpreter
 * @package sun.jvm.hotspot.memory
 * @package sun.jvm.hotspot.oops
 * @package sun.jvm.hotspot.opto
 * @package sun.jvm.hotspot.prims
 * @package sun.jvm.hotspot.runtime
 * @package sun.jvm.hotspot.runtime.aarch64
 * @package sun.jvm.hotspot.runtime.amd64
 * @package sun.jvm.hotspot.runtime.bsd
 * @package sun.jvm.hotspot.runtime.bsd_amd64
 * @package sun.jvm.hotspot.runtime.bsd_x86
 * @package sun.jvm.hotspot.runtime.linux
 * @package sun.jvm.hotspot.runtime.linux_aarch64
 * @package sun.jvm.hotspot.runtime.linux_amd64
 * @package sun.jvm.hotspot.runtime.linux_ppc64
 * @package sun.jvm.hotspot.runtime.linux_sparc
 * @package sun.jvm.hotspot.runtime.linux_x86
 * @package sun.jvm.hotspot.runtime.posix
 * @package sun.jvm.hotspot.runtime.ppc64
 * @package sun.jvm.hotspot.runtime.solaris_amd64
 * @package sun.jvm.hotspot.runtime.solaris_sparc
 * @package sun.jvm.hotspot.runtime.solaris_x86
 * @package sun.jvm.hotspot.runtime.sparc
 * @package sun.jvm.hotspot.runtime.win32_amd64
 * @package sun.jvm.hotspot.runtime.win32_x86
 * @package sun.jvm.hotspot.runtime.x86
 * @package sun.jvm.hotspot.tools
 * @package sun.jvm.hotspot.tools.jcore
 * @package sun.jvm.hotspot.tools.soql
 * @package sun.jvm.hotspot.types
 * @package sun.jvm.hotspot.types.basic
 * @package sun.jvm.hotspot.ui
 * @package sun.jvm.hotspot.ui.action
 * @package sun.jvm.hotspot.ui.classbrowser
 * @package sun.jvm.hotspot.ui.resources
 * @package sun.jvm.hotspot.ui.table
 * @package sun.jvm.hotspot.ui.tree
 * @package sun.jvm.hotspot.ui.treetable
 * @package sun.jvm.hotspot.utilities
 * @package sun.jvm.hotspot.utilities.memo
 * @package sun.jvm.hotspot.utilities.soql
 * @package toolbarButtonGraphics.development
 * @package toolbarButtonGraphics.general
 * @package toolbarButtonGraphics.navigation
 * @package toolbarButtonGraphics.text
 */
module jdk.hotspot.agent {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.rmi;
	requires java.datatransfer;
	requires java.desktop;
	requires java.scripting;
	exports sun.jvm.hotspot.debugger.remote to 
		java.rmi;
}