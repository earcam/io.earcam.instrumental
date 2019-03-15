/**
 * @version 12
 * @package java.awt.datatransfer
 * @package sun.datatransfer
 * @package sun.datatransfer.resources
 */
module java.datatransfer {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports java.awt.datatransfer;
	exports sun.datatransfer to 
		java.desktop;
	uses sun.datatransfer.DesktopDatatransferService;
}