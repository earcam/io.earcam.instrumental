/**
 * @version 10.0.2
 * @package com.sun.accessibility.internal.resources
 * @package com.sun.awt
 * @package com.sun.beans
 * @package com.sun.beans.decoder
 * @package com.sun.beans.editors
 * @package com.sun.beans.finder
 * @package com.sun.beans.infos
 * @package com.sun.beans.introspect
 * @package com.sun.beans.util
 * @package com.sun.imageio.plugins.bmp
 * @package com.sun.imageio.plugins.common
 * @package com.sun.imageio.plugins.gif
 * @package com.sun.imageio.plugins.jpeg
 * @package com.sun.imageio.plugins.png
 * @package com.sun.imageio.plugins.tiff
 * @package com.sun.imageio.plugins.wbmp
 * @package com.sun.imageio.spi
 * @package com.sun.imageio.stream
 * @package com.sun.java.swing
 * @package com.sun.java.swing.plaf.gtk
 * @package com.sun.java.swing.plaf.gtk.icons
 * @package com.sun.java.swing.plaf.gtk.resources
 * @package com.sun.java.swing.plaf.motif
 * @package com.sun.java.swing.plaf.motif.icons
 * @package com.sun.java.swing.plaf.motif.resources
 * @package com.sun.media.sound
 * @package com.sun.swing.internal.plaf.basic.resources
 * @package com.sun.swing.internal.plaf.metal.resources
 * @package com.sun.swing.internal.plaf.synth.resources
 * @package java.applet
 * @package java.awt
 * @package java.awt.color
 * @package java.awt.desktop
 * @package java.awt.dnd
 * @package java.awt.dnd.peer
 * @package java.awt.event
 * @package java.awt.font
 * @package java.awt.geom
 * @package java.awt.im
 * @package java.awt.im.spi
 * @package java.awt.image
 * @package java.awt.image.renderable
 * @package java.awt.peer
 * @package java.awt.print
 * @package java.beans
 * @package java.beans.beancontext
 * @package javax.accessibility
 * @package javax.imageio
 * @package javax.imageio.event
 * @package javax.imageio.metadata
 * @package javax.imageio.plugins.bmp
 * @package javax.imageio.plugins.jpeg
 * @package javax.imageio.plugins.tiff
 * @package javax.imageio.spi
 * @package javax.imageio.stream
 * @package javax.print
 * @package javax.print.attribute
 * @package javax.print.attribute.standard
 * @package javax.print.event
 * @package javax.sound.midi
 * @package javax.sound.midi.spi
 * @package javax.sound.sampled
 * @package javax.sound.sampled.spi
 * @package javax.swing
 * @package javax.swing.beaninfo.images
 * @package javax.swing.border
 * @package javax.swing.colorchooser
 * @package javax.swing.event
 * @package javax.swing.filechooser
 * @package javax.swing.plaf
 * @package javax.swing.plaf.basic
 * @package javax.swing.plaf.basic.icons
 * @package javax.swing.plaf.metal
 * @package javax.swing.plaf.metal.icons
 * @package javax.swing.plaf.metal.icons.ocean
 * @package javax.swing.plaf.metal.sounds
 * @package javax.swing.plaf.multi
 * @package javax.swing.plaf.nimbus
 * @package javax.swing.plaf.synth
 * @package javax.swing.table
 * @package javax.swing.text
 * @package javax.swing.text.html
 * @package javax.swing.text.html.parser
 * @package javax.swing.text.rtf
 * @package javax.swing.text.rtf.charsets
 * @package javax.swing.tree
 * @package javax.swing.undo
 * @package sun.applet
 * @package sun.applet.resources
 * @package sun.awt
 * @package sun.awt.X11
 * @package sun.awt.datatransfer
 * @package sun.awt.dnd
 * @package sun.awt.event
 * @package sun.awt.geom
 * @package sun.awt.im
 * @package sun.awt.image
 * @package sun.awt.resources
 * @package sun.awt.resources.cursors
 * @package sun.awt.shell
 * @package sun.awt.util
 * @package sun.awt.www.content
 * @package sun.awt.www.content.audio
 * @package sun.awt.www.content.image
 * @package sun.font
 * @package sun.font.lookup
 * @package sun.java2d
 * @package sun.java2d.cmm
 * @package sun.java2d.cmm.lcms
 * @package sun.java2d.cmm.profiles
 * @package sun.java2d.loops
 * @package sun.java2d.marlin
 * @package sun.java2d.marlin.stats
 * @package sun.java2d.opengl
 * @package sun.java2d.pipe
 * @package sun.java2d.pipe.hw
 * @package sun.java2d.x11
 * @package sun.java2d.xr
 * @package sun.print
 * @package sun.print.resources
 * @package sun.swing
 * @package sun.swing.icon
 * @package sun.swing.plaf
 * @package sun.swing.plaf.synth
 * @package sun.swing.table
 * @package sun.swing.text
 * @package sun.swing.text.html
 */
module java.desktop {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml;
	requires java.prefs;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.datatransfer;
	exports sun.awt.image to 
		javafx.swing;
	exports javax.swing.text.html.parser;
	exports sun.swing to 
		javafx.swing;
	exports java.awt.im.spi;
	exports javax.imageio.plugins.jpeg;
	exports java.awt.geom;
	exports sun.awt to 
		javafx.swing,
		jdk.accessibility,
		oracle.desktop;
	exports javax.imageio.spi;
	exports java.awt.dnd.peer to 
		javafx.swing;
	exports javax.imageio.metadata;
	exports javax.swing.text.html;
	exports javax.swing.plaf.multi;
	exports javax.swing.undo;
	exports sun.font.lookup to 
		javafx.graphics;
	exports java.awt.image;
	exports javax.print.event;
	exports java.awt.font;
	exports java.awt.event;
	exports javax.swing.table;
	exports javax.sound.midi;
	exports javax.imageio;
	exports javax.swing.plaf.metal;
	exports javax.imageio.event;
	exports javax.print.attribute.standard;
	exports javax.swing.plaf.synth;
	exports java.awt.print;
	exports javax.swing.colorchooser;
	exports javax.sound.sampled.spi;
	exports sun.java2d to 
		javafx.swing,
		oracle.desktop;
	exports javax.imageio.plugins.tiff;
	exports sun.print to 
		javafx.graphics;
	exports javax.swing;
	exports sun.awt.dnd to 
		javafx.swing;
	exports javax.swing.plaf.basic;
	exports java.awt.im;
	exports javax.sound.midi.spi;
	exports java.applet;
	exports javax.swing.filechooser;
	exports javax.print.attribute;
	exports javax.print;
	exports javax.swing.event;
	exports java.awt.dnd;
	exports javax.imageio.plugins.bmp;
	exports javax.imageio.stream;
	exports javax.swing.text.rtf;
	exports javax.swing.border;
	exports java.beans;
	exports javax.swing.text;
	exports javax.swing.tree;
	exports javax.swing.plaf.nimbus;
	exports java.awt.color;
	exports javax.swing.plaf;
	exports java.awt.desktop;
	exports java.beans.beancontext;
	exports javax.accessibility;
	exports java.awt.image.renderable;
	exports javax.sound.sampled;
	exports java.awt;
	opens javax.swing.plaf.basic to 
		jdk.jconsole;
	uses java.awt.im.spi.InputMethodDescriptor;
	uses javax.accessibility.AccessibilityProvider;
	uses javax.imageio.spi.ImageInputStreamSpi;
	uses javax.imageio.spi.ImageOutputStreamSpi;
	uses javax.imageio.spi.ImageReaderSpi;
	uses javax.imageio.spi.ImageTranscoderSpi;
	uses javax.imageio.spi.ImageWriterSpi;
	uses javax.print.PrintServiceLookup;
	uses javax.print.StreamPrintServiceFactory;
	uses javax.sound.midi.spi.MidiDeviceProvider;
	uses javax.sound.midi.spi.MidiFileReader;
	uses javax.sound.midi.spi.MidiFileWriter;
	uses javax.sound.midi.spi.SoundbankReader;
	uses javax.sound.sampled.spi.AudioFileReader;
	uses javax.sound.sampled.spi.AudioFileWriter;
	uses javax.sound.sampled.spi.FormatConversionProvider;
	uses javax.sound.sampled.spi.MixerProvider;
	provides javax.print.StreamPrintServiceFactory with 
		sun.print.PSStreamPrinterFactory;
	provides sun.datatransfer.DesktopDatatransferService with 
		sun.awt.datatransfer.DesktopDatatransferServiceImpl;
	provides javax.sound.sampled.spi.MixerProvider with 
		com.sun.media.sound.DirectAudioDeviceProvider,
		com.sun.media.sound.PortMixerProvider;
	provides javax.sound.midi.spi.MidiFileReader with 
		com.sun.media.sound.StandardMidiFileReader;
	provides javax.sound.midi.spi.SoundbankReader with 
		com.sun.media.sound.AudioFileSoundbankReader,
		com.sun.media.sound.DLSSoundbankReader,
		com.sun.media.sound.JARSoundbankReader,
		com.sun.media.sound.SF2SoundbankReader;
	provides javax.sound.midi.spi.MidiDeviceProvider with 
		com.sun.media.sound.MidiInDeviceProvider,
		com.sun.media.sound.MidiOutDeviceProvider,
		com.sun.media.sound.RealTimeSequencerProvider,
		com.sun.media.sound.SoftProvider;
	provides javax.sound.midi.spi.MidiFileWriter with 
		com.sun.media.sound.StandardMidiFileWriter;
	provides java.net.ContentHandlerFactory with 
		sun.awt.www.content.MultimediaContentHandlers;
	provides javax.print.PrintServiceLookup with 
		sun.print.PrintServiceLookupProvider;
	provides javax.sound.sampled.spi.FormatConversionProvider with 
		com.sun.media.sound.AlawCodec,
		com.sun.media.sound.AudioFloatFormatConverter,
		com.sun.media.sound.PCMtoPCMCodec,
		com.sun.media.sound.UlawCodec;
	provides javax.sound.sampled.spi.AudioFileReader with 
		com.sun.media.sound.AiffFileReader,
		com.sun.media.sound.AuFileReader,
		com.sun.media.sound.SoftMidiAudioFileReader,
		com.sun.media.sound.WaveExtensibleFileReader,
		com.sun.media.sound.WaveFileReader,
		com.sun.media.sound.WaveFloatFileReader;
	provides javax.sound.sampled.spi.AudioFileWriter with 
		com.sun.media.sound.AiffFileWriter,
		com.sun.media.sound.AuFileWriter,
		com.sun.media.sound.WaveFileWriter,
		com.sun.media.sound.WaveFloatFileWriter;
}