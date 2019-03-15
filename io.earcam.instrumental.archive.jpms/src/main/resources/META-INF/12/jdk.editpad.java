/**
 * @version 12
 * @package jdk.editpad
 * @package jdk.editpad.resources
 */
module jdk.editpad {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.internal.ed;
	requires java.desktop;
	provides jdk.internal.editor.spi.BuildInEditorProvider with 
		jdk.editpad.EditPadProvider;
}