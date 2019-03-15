/**
 * @version 9.0.4
 * @package jdk.editpad
 * @package jdk.editpad.resources
 */
module jdk.editpad {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	requires jdk.internal.ed;
	provides jdk.internal.editor.spi.BuildInEditorProvider with 
		jdk.editpad.EditPadProvider;
}