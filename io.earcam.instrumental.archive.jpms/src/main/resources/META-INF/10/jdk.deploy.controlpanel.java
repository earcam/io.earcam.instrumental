/**
 * @version 10.0.2
 * @package com.sun.deploy.jcp
 * @package com.sun.deploy.jcp.controller
 * @package com.sun.deploy.jcp.controller.cacheviewer
 * @package com.sun.deploy.jcp.controls
 * @package com.sun.deploy.jcp.dialog
 * @package com.sun.deploy.jcp.fxml
 * @package com.sun.deploy.jcp.resources
 * @package com.sun.deploy.jcp.resources.images
 * @package com.sun.deploy.jcp.selector
 */
module jdk.deploy.controlpanel {
	requires java.xml;
	requires jdk.deploy;
	requires javafx.fxml;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires javafx.controls;
	requires javafx.base;
	requires jdk.javaws;
	requires java.desktop;
	requires javafx.graphics;
	exports com.sun.deploy.jcp to 
		javafx.graphics;
	opens com.sun.deploy.jcp.controller.cacheviewer to 
		javafx.base,
		javafx.fxml;
	opens com.sun.deploy.jcp.controls to 
		javafx.fxml;
	opens com.sun.deploy.jcp.controller to 
		javafx.base,
		javafx.fxml;
}