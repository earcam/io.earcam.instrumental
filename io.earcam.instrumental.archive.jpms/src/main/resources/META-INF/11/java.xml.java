/**
 * @version 11.0.2
 * @package com.sun.java_cup.internal.runtime
 * @package com.sun.org.apache.bcel.internal
 * @package com.sun.org.apache.bcel.internal.classfile
 * @package com.sun.org.apache.bcel.internal.generic
 * @package com.sun.org.apache.bcel.internal.util
 * @package com.sun.org.apache.xalan.internal
 * @package com.sun.org.apache.xalan.internal.extensions
 * @package com.sun.org.apache.xalan.internal.lib
 * @package com.sun.org.apache.xalan.internal.res
 * @package com.sun.org.apache.xalan.internal.templates
 * @package com.sun.org.apache.xalan.internal.utils
 * @package com.sun.org.apache.xalan.internal.xsltc
 * @package com.sun.org.apache.xalan.internal.xsltc.compiler
 * @package com.sun.org.apache.xalan.internal.xsltc.compiler.util
 * @package com.sun.org.apache.xalan.internal.xsltc.dom
 * @package com.sun.org.apache.xalan.internal.xsltc.runtime
 * @package com.sun.org.apache.xalan.internal.xsltc.runtime.output
 * @package com.sun.org.apache.xalan.internal.xsltc.trax
 * @package com.sun.org.apache.xalan.internal.xsltc.util
 * @package com.sun.org.apache.xerces.internal.dom
 * @package com.sun.org.apache.xerces.internal.dom.events
 * @package com.sun.org.apache.xerces.internal.impl
 * @package com.sun.org.apache.xerces.internal.impl.dtd
 * @package com.sun.org.apache.xerces.internal.impl.dtd.models
 * @package com.sun.org.apache.xerces.internal.impl.dv
 * @package com.sun.org.apache.xerces.internal.impl.dv.dtd
 * @package com.sun.org.apache.xerces.internal.impl.dv.util
 * @package com.sun.org.apache.xerces.internal.impl.dv.xs
 * @package com.sun.org.apache.xerces.internal.impl.io
 * @package com.sun.org.apache.xerces.internal.impl.msg
 * @package com.sun.org.apache.xerces.internal.impl.validation
 * @package com.sun.org.apache.xerces.internal.impl.xpath
 * @package com.sun.org.apache.xerces.internal.impl.xpath.regex
 * @package com.sun.org.apache.xerces.internal.impl.xs
 * @package com.sun.org.apache.xerces.internal.impl.xs.identity
 * @package com.sun.org.apache.xerces.internal.impl.xs.models
 * @package com.sun.org.apache.xerces.internal.impl.xs.opti
 * @package com.sun.org.apache.xerces.internal.impl.xs.traversers
 * @package com.sun.org.apache.xerces.internal.impl.xs.util
 * @package com.sun.org.apache.xerces.internal.jaxp
 * @package com.sun.org.apache.xerces.internal.jaxp.datatype
 * @package com.sun.org.apache.xerces.internal.jaxp.validation
 * @package com.sun.org.apache.xerces.internal.parsers
 * @package com.sun.org.apache.xerces.internal.util
 * @package com.sun.org.apache.xerces.internal.utils
 * @package com.sun.org.apache.xerces.internal.xinclude
 * @package com.sun.org.apache.xerces.internal.xni
 * @package com.sun.org.apache.xerces.internal.xni.grammars
 * @package com.sun.org.apache.xerces.internal.xni.parser
 * @package com.sun.org.apache.xerces.internal.xpointer
 * @package com.sun.org.apache.xerces.internal.xs
 * @package com.sun.org.apache.xerces.internal.xs.datatypes
 * @package com.sun.org.apache.xml.internal.dtm
 * @package com.sun.org.apache.xml.internal.dtm.ref
 * @package com.sun.org.apache.xml.internal.dtm.ref.dom2dtm
 * @package com.sun.org.apache.xml.internal.dtm.ref.sax2dtm
 * @package com.sun.org.apache.xml.internal.res
 * @package com.sun.org.apache.xml.internal.serialize
 * @package com.sun.org.apache.xml.internal.serializer
 * @package com.sun.org.apache.xml.internal.serializer.dom3
 * @package com.sun.org.apache.xml.internal.serializer.utils
 * @package com.sun.org.apache.xml.internal.utils
 * @package com.sun.org.apache.xml.internal.utils.res
 * @package com.sun.org.apache.xpath.internal
 * @package com.sun.org.apache.xpath.internal.axes
 * @package com.sun.org.apache.xpath.internal.compiler
 * @package com.sun.org.apache.xpath.internal.functions
 * @package com.sun.org.apache.xpath.internal.jaxp
 * @package com.sun.org.apache.xpath.internal.objects
 * @package com.sun.org.apache.xpath.internal.operations
 * @package com.sun.org.apache.xpath.internal.patterns
 * @package com.sun.org.apache.xpath.internal.res
 * @package com.sun.xml.internal.stream
 * @package com.sun.xml.internal.stream.dtd
 * @package com.sun.xml.internal.stream.dtd.nonvalidating
 * @package com.sun.xml.internal.stream.events
 * @package com.sun.xml.internal.stream.util
 * @package com.sun.xml.internal.stream.writers
 * @package javax.xml
 * @package javax.xml.catalog
 * @package javax.xml.datatype
 * @package javax.xml.namespace
 * @package javax.xml.parsers
 * @package javax.xml.stream
 * @package javax.xml.stream.events
 * @package javax.xml.stream.util
 * @package javax.xml.transform
 * @package javax.xml.transform.dom
 * @package javax.xml.transform.sax
 * @package javax.xml.transform.stax
 * @package javax.xml.transform.stream
 * @package javax.xml.validation
 * @package javax.xml.xpath
 * @package jdk.xml.internal
 * @package org.w3c.dom
 * @package org.w3c.dom.bootstrap
 * @package org.w3c.dom.events
 * @package org.w3c.dom.ls
 * @package org.w3c.dom.ranges
 * @package org.w3c.dom.traversal
 * @package org.w3c.dom.views
 * @package org.xml.sax
 * @package org.xml.sax.ext
 * @package org.xml.sax.helpers
 */
module java.xml {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	exports javax.xml;
	exports javax.xml.parsers;
	exports javax.xml.transform;
	exports org.xml.sax.helpers;
	exports javax.xml.xpath;
	exports org.xml.sax;
	exports com.sun.org.apache.xpath.internal.objects to 
		java.xml.crypto;
	exports javax.xml.catalog;
	exports javax.xml.transform.stream;
	exports org.w3c.dom.traversal;
	exports com.sun.org.apache.xml.internal.dtm to 
		java.xml.crypto;
	exports org.xml.sax.ext;
	exports javax.xml.namespace;
	exports com.sun.org.apache.xpath.internal.res to 
		java.xml.crypto;
	exports javax.xml.datatype;
	exports com.sun.org.apache.xml.internal.utils to 
		java.xml.crypto;
	exports javax.xml.transform.dom;
	exports org.w3c.dom.bootstrap;
	exports javax.xml.transform.stax;
	exports javax.xml.validation;
	exports com.sun.org.apache.xpath.internal.compiler to 
		java.xml.crypto;
	exports com.sun.org.apache.xpath.internal to 
		java.xml.crypto;
	exports org.w3c.dom.ls;
	exports org.w3c.dom.views;
	exports javax.xml.stream.events;
	exports javax.xml.stream.util;
	exports org.w3c.dom.ranges;
	exports com.sun.org.apache.xpath.internal.functions to 
		java.xml.crypto;
	exports org.w3c.dom;
	exports javax.xml.stream;
	exports org.w3c.dom.events;
	exports javax.xml.transform.sax;
	uses javax.xml.datatype.DatatypeFactory;
	uses javax.xml.parsers.DocumentBuilderFactory;
	uses javax.xml.parsers.SAXParserFactory;
	uses javax.xml.stream.XMLEventFactory;
	uses javax.xml.stream.XMLInputFactory;
	uses javax.xml.stream.XMLOutputFactory;
	uses javax.xml.transform.TransformerFactory;
	uses javax.xml.validation.SchemaFactory;
	uses javax.xml.xpath.XPathFactory;
	uses org.xml.sax.XMLReader;
}