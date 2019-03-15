/**
 * @version 10.0.2
 * @package com.sun.codemodel.internal
 * @package com.sun.codemodel.internal.fmt
 * @package com.sun.codemodel.internal.util
 * @package com.sun.codemodel.internal.writer
 * @package com.sun.istack.internal.tools
 * @package com.sun.tools.internal.jxc
 * @package com.sun.tools.internal.jxc.ap
 * @package com.sun.tools.internal.jxc.api
 * @package com.sun.tools.internal.jxc.api.impl.j2s
 * @package com.sun.tools.internal.jxc.gen.config
 * @package com.sun.tools.internal.jxc.model.nav
 * @package com.sun.tools.internal.xjc
 * @package com.sun.tools.internal.xjc.addon.accessors
 * @package com.sun.tools.internal.xjc.addon.at_generated
 * @package com.sun.tools.internal.xjc.addon.code_injector
 * @package com.sun.tools.internal.xjc.addon.episode
 * @package com.sun.tools.internal.xjc.addon.locator
 * @package com.sun.tools.internal.xjc.addon.sync
 * @package com.sun.tools.internal.xjc.api
 * @package com.sun.tools.internal.xjc.api.impl.s2j
 * @package com.sun.tools.internal.xjc.generator.annotation.spec
 * @package com.sun.tools.internal.xjc.generator.bean
 * @package com.sun.tools.internal.xjc.generator.bean.field
 * @package com.sun.tools.internal.xjc.generator.util
 * @package com.sun.tools.internal.xjc.model
 * @package com.sun.tools.internal.xjc.model.nav
 * @package com.sun.tools.internal.xjc.outline
 * @package com.sun.tools.internal.xjc.reader
 * @package com.sun.tools.internal.xjc.reader.dtd
 * @package com.sun.tools.internal.xjc.reader.dtd.bindinfo
 * @package com.sun.tools.internal.xjc.reader.gbind
 * @package com.sun.tools.internal.xjc.reader.internalizer
 * @package com.sun.tools.internal.xjc.reader.relaxng
 * @package com.sun.tools.internal.xjc.reader.xmlschema
 * @package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo
 * @package com.sun.tools.internal.xjc.reader.xmlschema.ct
 * @package com.sun.tools.internal.xjc.reader.xmlschema.parser
 * @package com.sun.tools.internal.xjc.runtime
 * @package com.sun.tools.internal.xjc.util
 * @package com.sun.tools.internal.xjc.writer
 * @package com.sun.xml.internal.dtdparser
 * @package com.sun.xml.internal.dtdparser.resources
 * @package com.sun.xml.internal.org.relaxng.datatype
 * @package com.sun.xml.internal.org.relaxng.datatype.helpers
 * @package com.sun.xml.internal.rngom.ast.builder
 * @package com.sun.xml.internal.rngom.ast.om
 * @package com.sun.xml.internal.rngom.ast.util
 * @package com.sun.xml.internal.rngom.binary
 * @package com.sun.xml.internal.rngom.binary.visitor
 * @package com.sun.xml.internal.rngom.digested
 * @package com.sun.xml.internal.rngom.dt
 * @package com.sun.xml.internal.rngom.dt.builtin
 * @package com.sun.xml.internal.rngom.nc
 * @package com.sun.xml.internal.rngom.parse
 * @package com.sun.xml.internal.rngom.parse.compact
 * @package com.sun.xml.internal.rngom.parse.host
 * @package com.sun.xml.internal.rngom.parse.xml
 * @package com.sun.xml.internal.rngom.util
 * @package com.sun.xml.internal.rngom.xml.sax
 * @package com.sun.xml.internal.rngom.xml.util
 * @package com.sun.xml.internal.xsom
 * @package com.sun.xml.internal.xsom.impl
 * @package com.sun.xml.internal.xsom.impl.parser
 * @package com.sun.xml.internal.xsom.impl.parser.state
 * @package com.sun.xml.internal.xsom.impl.scd
 * @package com.sun.xml.internal.xsom.impl.util
 * @package com.sun.xml.internal.xsom.parser
 * @package com.sun.xml.internal.xsom.util
 * @package com.sun.xml.internal.xsom.visitor
 */
module jdk.xml.bind {
	requires java.activation;
	requires java.xml;
	requires java.compiler;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.logging;
	requires java.xml.bind;
	requires jdk.compiler;
	requires java.desktop;
	exports com.sun.xml.internal.xsom.parser to 
		jdk.xml.ws;
	exports com.sun.tools.internal.xjc.generator.bean to 
		java.xml.bind;
	exports com.sun.tools.internal.xjc.reader to 
		jdk.xml.ws;
	exports com.sun.tools.internal.jxc.ap to 
		jdk.xml.ws;
	exports com.sun.tools.internal.xjc.reader.internalizer to 
		jdk.xml.ws;
	exports com.sun.codemodel.internal to 
		jdk.xml.ws;
	exports com.sun.tools.internal.jxc.model.nav to 
		jdk.xml.ws;
	exports com.sun.codemodel.internal.writer to 
		jdk.xml.ws;
	exports com.sun.tools.internal.xjc to 
		jdk.xml.ws;
	exports com.sun.tools.internal.xjc.api to 
		jdk.xml.ws;
	exports com.sun.tools.internal.xjc.util to 
		jdk.xml.ws;
	exports com.sun.istack.internal.tools to 
		jdk.xml.ws;
	opens com.sun.tools.internal.xjc.reader.xmlschema.bindinfo to 
		java.xml.bind;
	uses com.sun.tools.internal.xjc.Plugin;
	provides com.sun.tools.internal.xjc.Plugin with 
		com.sun.tools.internal.xjc.addon.accessors.PluginImpl,
		com.sun.tools.internal.xjc.addon.at_generated.PluginImpl,
		com.sun.tools.internal.xjc.addon.code_injector.PluginImpl,
		com.sun.tools.internal.xjc.addon.episode.PluginImpl,
		com.sun.tools.internal.xjc.addon.locator.SourceLocationAddOn,
		com.sun.tools.internal.xjc.addon.sync.SynchronizedMethodAddOn;
}