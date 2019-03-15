/**
 * @version 9.0.4
 * @package com.sun.tools.internal.ws
 * @package com.sun.tools.internal.ws.api
 * @package com.sun.tools.internal.ws.api.wsdl
 * @package com.sun.tools.internal.ws.processor
 * @package com.sun.tools.internal.ws.processor.generator
 * @package com.sun.tools.internal.ws.processor.model
 * @package com.sun.tools.internal.ws.processor.model.exporter
 * @package com.sun.tools.internal.ws.processor.model.java
 * @package com.sun.tools.internal.ws.processor.model.jaxb
 * @package com.sun.tools.internal.ws.processor.modeler
 * @package com.sun.tools.internal.ws.processor.modeler.annotation
 * @package com.sun.tools.internal.ws.processor.modeler.wsdl
 * @package com.sun.tools.internal.ws.processor.util
 * @package com.sun.tools.internal.ws.resources
 * @package com.sun.tools.internal.ws.spi
 * @package com.sun.tools.internal.ws.util
 * @package com.sun.tools.internal.ws.util.xml
 * @package com.sun.tools.internal.ws.wscompile
 * @package com.sun.tools.internal.ws.wscompile.plugin.at_generated
 * @package com.sun.tools.internal.ws.wsdl.document
 * @package com.sun.tools.internal.ws.wsdl.document.http
 * @package com.sun.tools.internal.ws.wsdl.document.jaxws
 * @package com.sun.tools.internal.ws.wsdl.document.mime
 * @package com.sun.tools.internal.ws.wsdl.document.schema
 * @package com.sun.tools.internal.ws.wsdl.document.soap
 * @package com.sun.tools.internal.ws.wsdl.framework
 * @package com.sun.tools.internal.ws.wsdl.parser
 */
module jdk.xml.ws {
	requires jdk.xml.bind;
	requires java.xml;
	requires java.compiler;
	requires java.xml.ws;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.rmi;
	requires java.logging;
	requires java.xml.bind;
	uses com.sun.tools.internal.ws.wscompile.Plugin;
	provides com.sun.tools.internal.ws.wscompile.Plugin with 
		com.sun.tools.internal.ws.wscompile.plugin.at_generated.PluginImpl;
}