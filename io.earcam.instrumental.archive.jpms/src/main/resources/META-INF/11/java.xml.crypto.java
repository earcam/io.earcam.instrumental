/**
 * @version 11.0.2
 * @package com.sun.org.apache.xml.internal.security
 * @package com.sun.org.apache.xml.internal.security.algorithms
 * @package com.sun.org.apache.xml.internal.security.algorithms.implementations
 * @package com.sun.org.apache.xml.internal.security.c14n
 * @package com.sun.org.apache.xml.internal.security.c14n.helper
 * @package com.sun.org.apache.xml.internal.security.c14n.implementations
 * @package com.sun.org.apache.xml.internal.security.exceptions
 * @package com.sun.org.apache.xml.internal.security.keys
 * @package com.sun.org.apache.xml.internal.security.keys.content
 * @package com.sun.org.apache.xml.internal.security.keys.content.keyvalues
 * @package com.sun.org.apache.xml.internal.security.keys.content.x509
 * @package com.sun.org.apache.xml.internal.security.keys.keyresolver
 * @package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations
 * @package com.sun.org.apache.xml.internal.security.keys.storage
 * @package com.sun.org.apache.xml.internal.security.keys.storage.implementations
 * @package com.sun.org.apache.xml.internal.security.resource
 * @package com.sun.org.apache.xml.internal.security.signature
 * @package com.sun.org.apache.xml.internal.security.signature.reference
 * @package com.sun.org.apache.xml.internal.security.transforms
 * @package com.sun.org.apache.xml.internal.security.transforms.implementations
 * @package com.sun.org.apache.xml.internal.security.transforms.params
 * @package com.sun.org.apache.xml.internal.security.utils
 * @package com.sun.org.apache.xml.internal.security.utils.resolver
 * @package com.sun.org.apache.xml.internal.security.utils.resolver.implementations
 * @package com.sun.org.slf4j.internal
 * @package javax.xml.crypto
 * @package javax.xml.crypto.dom
 * @package javax.xml.crypto.dsig
 * @package javax.xml.crypto.dsig.dom
 * @package javax.xml.crypto.dsig.keyinfo
 * @package javax.xml.crypto.dsig.spec
 * @package org.jcp.xml.dsig.internal
 * @package org.jcp.xml.dsig.internal.dom
 */
module java.xml.crypto {
	/**
	 * @modifiers transitive
	 */
	requires transitive java.xml;
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.logging;
	exports javax.xml.crypto.dsig.dom;
	exports javax.xml.crypto.dsig.keyinfo;
	exports javax.xml.crypto.dsig;
	exports javax.xml.crypto;
	exports javax.xml.crypto.dom;
	exports javax.xml.crypto.dsig.spec;
	provides java.security.Provider with 
		org.jcp.xml.dsig.internal.dom.XMLDSigRI;
}