/**
 * @version 9.0.4
 * @package com.sun.istack.internal
 * @package com.sun.istack.internal.localization
 * @package com.sun.istack.internal.logging
 * @package com.sun.xml.internal.bind
 * @package com.sun.xml.internal.bind.annotation
 * @package com.sun.xml.internal.bind.api
 * @package com.sun.xml.internal.bind.api.impl
 * @package com.sun.xml.internal.bind.marshaller
 * @package com.sun.xml.internal.bind.unmarshaller
 * @package com.sun.xml.internal.bind.util
 * @package com.sun.xml.internal.bind.v2
 * @package com.sun.xml.internal.bind.v2.bytecode
 * @package com.sun.xml.internal.bind.v2.model.annotation
 * @package com.sun.xml.internal.bind.v2.model.core
 * @package com.sun.xml.internal.bind.v2.model.impl
 * @package com.sun.xml.internal.bind.v2.model.nav
 * @package com.sun.xml.internal.bind.v2.model.runtime
 * @package com.sun.xml.internal.bind.v2.model.util
 * @package com.sun.xml.internal.bind.v2.runtime
 * @package com.sun.xml.internal.bind.v2.runtime.output
 * @package com.sun.xml.internal.bind.v2.runtime.property
 * @package com.sun.xml.internal.bind.v2.runtime.reflect
 * @package com.sun.xml.internal.bind.v2.runtime.reflect.opt
 * @package com.sun.xml.internal.bind.v2.runtime.unmarshaller
 * @package com.sun.xml.internal.bind.v2.schemagen
 * @package com.sun.xml.internal.bind.v2.schemagen.episode
 * @package com.sun.xml.internal.bind.v2.schemagen.xmlschema
 * @package com.sun.xml.internal.bind.v2.util
 * @package com.sun.xml.internal.fastinfoset
 * @package com.sun.xml.internal.fastinfoset.algorithm
 * @package com.sun.xml.internal.fastinfoset.alphabet
 * @package com.sun.xml.internal.fastinfoset.dom
 * @package com.sun.xml.internal.fastinfoset.org.apache.xerces.util
 * @package com.sun.xml.internal.fastinfoset.resources
 * @package com.sun.xml.internal.fastinfoset.sax
 * @package com.sun.xml.internal.fastinfoset.stax
 * @package com.sun.xml.internal.fastinfoset.stax.events
 * @package com.sun.xml.internal.fastinfoset.stax.factory
 * @package com.sun.xml.internal.fastinfoset.stax.util
 * @package com.sun.xml.internal.fastinfoset.tools
 * @package com.sun.xml.internal.fastinfoset.util
 * @package com.sun.xml.internal.fastinfoset.vocab
 * @package com.sun.xml.internal.org.jvnet.fastinfoset
 * @package com.sun.xml.internal.org.jvnet.fastinfoset.sax
 * @package com.sun.xml.internal.org.jvnet.fastinfoset.sax.helpers
 * @package com.sun.xml.internal.org.jvnet.fastinfoset.stax
 * @package com.sun.xml.internal.org.jvnet.mimepull
 * @package com.sun.xml.internal.org.jvnet.staxex
 * @package com.sun.xml.internal.org.jvnet.staxex.util
 * @package com.sun.xml.internal.txw2
 * @package com.sun.xml.internal.txw2.annotation
 * @package com.sun.xml.internal.txw2.output
 * @package javax.xml.bind
 * @package javax.xml.bind.annotation
 * @package javax.xml.bind.annotation.adapters
 * @package javax.xml.bind.attachment
 * @package javax.xml.bind.helpers
 * @package javax.xml.bind.util
 */
module java.xml.bind {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.activation;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.compiler;
	requires java.desktop;
	requires java.logging;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml;
	requires jdk.unsupported;
	exports com.sun.istack.internal to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	exports com.sun.istack.internal.localization to 
		java.xml.ws,
		jdk.xml.ws;
	exports com.sun.istack.internal.logging to 
		java.xml.ws,
		jdk.xml.ws;
	exports com.sun.xml.internal.bind to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	exports com.sun.xml.internal.bind.annotation to 
		jdk.xml.bind;
	exports com.sun.xml.internal.bind.api to 
		java.xml.ws,
		jdk.xml.bind;
	exports com.sun.xml.internal.bind.api.impl to 
		java.xml.ws,
		jdk.xml.bind;
	exports com.sun.xml.internal.bind.marshaller to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	exports com.sun.xml.internal.bind.unmarshaller to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	exports com.sun.xml.internal.bind.util to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	exports com.sun.xml.internal.bind.v2 to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	exports com.sun.xml.internal.bind.v2.model.annotation to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	exports com.sun.xml.internal.bind.v2.model.core to 
		jdk.xml.bind;
	exports com.sun.xml.internal.bind.v2.model.impl to 
		jdk.xml.bind;
	exports com.sun.xml.internal.bind.v2.model.nav to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	exports com.sun.xml.internal.bind.v2.model.runtime to 
		java.xml.ws;
	exports com.sun.xml.internal.bind.v2.model.util to 
		jdk.xml.bind;
	exports com.sun.xml.internal.bind.v2.runtime to 
		java.xml.ws,
		jdk.xml.bind;
	exports com.sun.xml.internal.bind.v2.runtime.unmarshaller to 
		java.xml.ws;
	exports com.sun.xml.internal.bind.v2.schemagen to 
		java.xml.ws,
		jdk.xml.bind;
	exports com.sun.xml.internal.bind.v2.schemagen.episode to 
		jdk.xml.bind;
	exports com.sun.xml.internal.bind.v2.schemagen.xmlschema to 
		java.xml.ws;
	exports com.sun.xml.internal.bind.v2.util to 
		jdk.xml.bind,
		jdk.xml.ws;
	exports com.sun.xml.internal.fastinfoset to 
		java.xml.ws;
	exports com.sun.xml.internal.fastinfoset.stax to 
		java.xml.ws;
	exports com.sun.xml.internal.fastinfoset.vocab to 
		java.xml.ws;
	exports com.sun.xml.internal.org.jvnet.fastinfoset to 
		java.xml.ws;
	exports com.sun.xml.internal.org.jvnet.mimepull to 
		java.xml.ws;
	exports com.sun.xml.internal.org.jvnet.staxex to 
		java.xml.ws;
	exports com.sun.xml.internal.org.jvnet.staxex.util to 
		java.xml.ws;
	exports com.sun.xml.internal.txw2 to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	exports com.sun.xml.internal.txw2.annotation to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	exports com.sun.xml.internal.txw2.output to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	exports javax.xml.bind;
	exports javax.xml.bind.annotation;
	exports javax.xml.bind.annotation.adapters;
	exports javax.xml.bind.attachment;
	exports javax.xml.bind.helpers;
	exports javax.xml.bind.util;
	opens com.sun.xml.internal.bind.v2.model.nav to 
		java.xml.ws,
		jdk.xml.bind,
		jdk.xml.ws;
	uses javax.xml.bind.JAXBContextFactory;
}