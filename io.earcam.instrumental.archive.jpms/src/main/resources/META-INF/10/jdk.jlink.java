/**
 * @version 10.0.2
 * @package jdk.tools.jimage
 * @package jdk.tools.jimage.resources
 * @package jdk.tools.jlink.builder
 * @package jdk.tools.jlink.internal
 * @package jdk.tools.jlink.internal.packager
 * @package jdk.tools.jlink.internal.plugins
 * @package jdk.tools.jlink.plugin
 * @package jdk.tools.jlink.resources
 * @package jdk.tools.jmod
 * @package jdk.tools.jmod.resources
 */
module jdk.jlink {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires jdk.internal.opt;
	requires jdk.jdeps;
	exports jdk.tools.jlink.internal.packager to 
		jdk.packager;
	uses jdk.tools.jlink.plugin.Plugin;
	provides java.util.spi.ToolProvider with 
		jdk.tools.jlink.internal.Main$JlinkToolProvider,
		jdk.tools.jmod.Main$JmodToolProvider;
	provides jdk.tools.jlink.plugin.Plugin with 
		jdk.tools.jlink.internal.plugins.ClassForNamePlugin,
		jdk.tools.jlink.internal.plugins.DefaultCompressPlugin,
		jdk.tools.jlink.internal.plugins.ExcludeFilesPlugin,
		jdk.tools.jlink.internal.plugins.ExcludeJmodSectionPlugin,
		jdk.tools.jlink.internal.plugins.ExcludePlugin,
		jdk.tools.jlink.internal.plugins.ExcludeVMPlugin,
		jdk.tools.jlink.internal.plugins.GenerateJLIClassesPlugin,
		jdk.tools.jlink.internal.plugins.IncludeLocalesPlugin,
		jdk.tools.jlink.internal.plugins.LegalNoticeFilePlugin,
		jdk.tools.jlink.internal.plugins.OrderResourcesPlugin,
		jdk.tools.jlink.internal.plugins.ReleaseInfoPlugin,
		jdk.tools.jlink.internal.plugins.StripDebugPlugin,
		jdk.tools.jlink.internal.plugins.StripNativeCommandsPlugin,
		jdk.tools.jlink.internal.plugins.SystemModulesPlugin;
}