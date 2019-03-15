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
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.desktop;
	requires java.xml;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires jdk.deploy;
	requires jdk.javaws;
	exports com.sun.deploy.jcp to 
		javafx.graphics;
	opens com.sun.deploy.jcp.controller to 
		javafx.base,
		javafx.fxml;
	opens com.sun.deploy.jcp.controller.cacheviewer to 
		javafx.base,
		javafx.fxml;
	opens com.sun.deploy.jcp.controls to 
		javafx.fxml;
}