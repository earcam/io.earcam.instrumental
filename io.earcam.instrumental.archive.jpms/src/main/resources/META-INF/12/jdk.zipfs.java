/**
 * @version 12
 * @package jdk.nio.zipfs
 */
module jdk.zipfs {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	provides java.nio.file.spi.FileSystemProvider with 
		jdk.nio.zipfs.ZipFileSystemProvider;
}