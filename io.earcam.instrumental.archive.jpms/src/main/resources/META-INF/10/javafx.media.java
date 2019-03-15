/**
 * @version 10.0.2
 * @package com.sun.javafx.media
 * @package com.sun.javafx.scene.media
 * @package com.sun.media.jfxmedia
 * @package com.sun.media.jfxmedia.control
 * @package com.sun.media.jfxmedia.effects
 * @package com.sun.media.jfxmedia.events
 * @package com.sun.media.jfxmedia.locator
 * @package com.sun.media.jfxmedia.logging
 * @package com.sun.media.jfxmedia.track
 * @package com.sun.media.jfxmediaimpl
 * @package com.sun.media.jfxmediaimpl.platform
 * @package com.sun.media.jfxmediaimpl.platform.gstreamer
 * @package com.sun.media.jfxmediaimpl.platform.java
 * @package javafx.scene.media
 */
module javafx.media {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive javafx.graphics;
	exports com.sun.javafx.media to 
		javafx.web;
	exports com.sun.media.jfxmedia to 
		javafx.web;
	exports com.sun.media.jfxmedia.control to 
		javafx.web;
	exports com.sun.media.jfxmedia.events to 
		javafx.web;
	exports com.sun.media.jfxmedia.locator to 
		javafx.web;
	exports com.sun.media.jfxmedia.track to 
		javafx.web;
	exports javafx.scene.media;
}