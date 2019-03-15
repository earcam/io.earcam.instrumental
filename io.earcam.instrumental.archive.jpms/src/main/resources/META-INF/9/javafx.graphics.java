/**
 * @version 9.0.4
 * @package com.sun.glass.events
 * @package com.sun.glass.ui
 * @package com.sun.glass.ui.delegate
 * @package com.sun.glass.ui.gtk
 * @package com.sun.glass.utils
 * @package com.sun.javafx.animation
 * @package com.sun.javafx.application
 * @package com.sun.javafx.beans.event
 * @package com.sun.javafx.css
 * @package com.sun.javafx.css.parser
 * @package com.sun.javafx.cursor
 * @package com.sun.javafx.effect
 * @package com.sun.javafx.embed
 * @package com.sun.javafx.font
 * @package com.sun.javafx.font.coretext
 * @package com.sun.javafx.font.directwrite
 * @package com.sun.javafx.font.freetype
 * @package com.sun.javafx.font.t2k
 * @package com.sun.javafx.geom
 * @package com.sun.javafx.geom.transform
 * @package com.sun.javafx.geometry
 * @package com.sun.javafx.iio
 * @package com.sun.javafx.iio.bmp
 * @package com.sun.javafx.iio.common
 * @package com.sun.javafx.iio.gif
 * @package com.sun.javafx.iio.ios
 * @package com.sun.javafx.iio.jpeg
 * @package com.sun.javafx.iio.png
 * @package com.sun.javafx.image
 * @package com.sun.javafx.image.impl
 * @package com.sun.javafx.jmx
 * @package com.sun.javafx.menu
 * @package com.sun.javafx.perf
 * @package com.sun.javafx.print
 * @package com.sun.javafx.runtime.async
 * @package com.sun.javafx.runtime.eula
 * @package com.sun.javafx.scene
 * @package com.sun.javafx.scene.canvas
 * @package com.sun.javafx.scene.input
 * @package com.sun.javafx.scene.layout
 * @package com.sun.javafx.scene.layout.region
 * @package com.sun.javafx.scene.paint
 * @package com.sun.javafx.scene.shape
 * @package com.sun.javafx.scene.text
 * @package com.sun.javafx.scene.transform
 * @package com.sun.javafx.scene.traversal
 * @package com.sun.javafx.sg.prism
 * @package com.sun.javafx.stage
 * @package com.sun.javafx.text
 * @package com.sun.javafx.tk
 * @package com.sun.javafx.tk.quantum
 * @package com.sun.javafx.util
 * @package com.sun.marlin
 * @package com.sun.marlin.stats
 * @package com.sun.openpisces
 * @package com.sun.pisces
 * @package com.sun.prism
 * @package com.sun.prism.es2
 * @package com.sun.prism.es2.glsl
 * @package com.sun.prism.image
 * @package com.sun.prism.impl
 * @package com.sun.prism.impl.packrect
 * @package com.sun.prism.impl.paint
 * @package com.sun.prism.impl.ps
 * @package com.sun.prism.impl.shape
 * @package com.sun.prism.j2d
 * @package com.sun.prism.j2d.paint
 * @package com.sun.prism.j2d.print
 * @package com.sun.prism.paint
 * @package com.sun.prism.ps
 * @package com.sun.prism.shader
 * @package com.sun.prism.shape
 * @package com.sun.prism.sw
 * @package com.sun.scenario
 * @package com.sun.scenario.animation
 * @package com.sun.scenario.animation.shared
 * @package com.sun.scenario.effect
 * @package com.sun.scenario.effect.impl
 * @package com.sun.scenario.effect.impl.es2
 * @package com.sun.scenario.effect.impl.es2.glsl
 * @package com.sun.scenario.effect.impl.hw
 * @package com.sun.scenario.effect.impl.prism
 * @package com.sun.scenario.effect.impl.prism.ps
 * @package com.sun.scenario.effect.impl.prism.sw
 * @package com.sun.scenario.effect.impl.state
 * @package com.sun.scenario.effect.impl.sw
 * @package com.sun.scenario.effect.impl.sw.java
 * @package com.sun.scenario.effect.impl.sw.sse
 * @package com.sun.scenario.effect.light
 * @package com.sun.util.reentrant
 * @package javafx.animation
 * @package javafx.application
 * @package javafx.concurrent
 * @package javafx.css
 * @package javafx.css.converter
 * @package javafx.geometry
 * @package javafx.print
 * @package javafx.scene
 * @package javafx.scene.canvas
 * @package javafx.scene.effect
 * @package javafx.scene.image
 * @package javafx.scene.input
 * @package javafx.scene.layout
 * @package javafx.scene.paint
 * @package javafx.scene.shape
 * @package javafx.scene.text
 * @package javafx.scene.transform
 * @package javafx.stage
 */
module javafx.graphics {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	requires java.xml;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.base;
	requires jdk.jsobject;
	exports com.sun.glass.ui to 
		javafx.media,
		javafx.web;
	exports com.sun.glass.utils to 
		javafx.media,
		javafx.web;
	exports com.sun.javafx.application to 
		java.base,
		javafx.controls,
		javafx.deploy,
		javafx.swing,
		javafx.web;
	exports com.sun.javafx.css to 
		javafx.controls,
		javafx.deploy;
	exports com.sun.javafx.css.parser to 
		jdk.packager;
	exports com.sun.javafx.cursor to 
		javafx.swing;
	exports com.sun.javafx.embed to 
		javafx.swing;
	exports com.sun.javafx.font to 
		javafx.web;
	exports com.sun.javafx.geom to 
		javafx.controls,
		javafx.media,
		javafx.swing,
		javafx.web;
	exports com.sun.javafx.geom.transform to 
		javafx.controls,
		javafx.media,
		javafx.swing,
		javafx.web;
	exports com.sun.javafx.iio to 
		javafx.web;
	exports com.sun.javafx.jmx to 
		javafx.media,
		javafx.swing,
		javafx.web;
	exports com.sun.javafx.menu to 
		javafx.controls;
	exports com.sun.javafx.perf to 
		javafx.deploy;
	exports com.sun.javafx.scene to 
		javafx.controls,
		javafx.media,
		javafx.swing,
		javafx.web;
	exports com.sun.javafx.scene.input to 
		javafx.controls,
		javafx.swing,
		javafx.web;
	exports com.sun.javafx.scene.layout to 
		javafx.controls,
		javafx.web;
	exports com.sun.javafx.scene.text to 
		javafx.controls,
		javafx.web;
	exports com.sun.javafx.scene.traversal to 
		javafx.controls,
		javafx.web;
	exports com.sun.javafx.sg.prism to 
		javafx.media,
		javafx.swing,
		javafx.web;
	exports com.sun.javafx.stage to 
		javafx.controls,
		javafx.deploy,
		javafx.swing;
	exports com.sun.javafx.text to 
		javafx.web;
	exports com.sun.javafx.tk to 
		javafx.controls,
		javafx.deploy,
		javafx.media,
		javafx.swing,
		javafx.web;
	exports com.sun.javafx.tk.quantum to 
		javafx.deploy;
	exports com.sun.javafx.util to 
		javafx.controls,
		javafx.fxml,
		javafx.media,
		javafx.swing,
		javafx.web;
	exports com.sun.prism to 
		javafx.media,
		javafx.web;
	exports com.sun.prism.image to 
		javafx.web;
	exports com.sun.prism.paint to 
		javafx.web;
	exports com.sun.scenario.effect to 
		javafx.web;
	exports com.sun.scenario.effect.impl to 
		javafx.web;
	exports com.sun.scenario.effect.impl.prism to 
		javafx.web;
	exports javafx.animation;
	exports javafx.application;
	exports javafx.concurrent;
	exports javafx.css;
	exports javafx.css.converter;
	exports javafx.geometry;
	exports javafx.print;
	exports javafx.scene;
	exports javafx.scene.canvas;
	exports javafx.scene.effect;
	exports javafx.scene.image;
	exports javafx.scene.input;
	exports javafx.scene.layout;
	exports javafx.scene.paint;
	exports javafx.scene.shape;
	exports javafx.scene.text;
	exports javafx.scene.transform;
	exports javafx.stage;
}