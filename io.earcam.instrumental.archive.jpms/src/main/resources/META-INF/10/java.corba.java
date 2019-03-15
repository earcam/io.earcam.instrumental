/**
 * @version 10.0.2
 * @package com.sun.corba.se.impl.activation
 * @package com.sun.corba.se.impl.copyobject
 * @package com.sun.corba.se.impl.corba
 * @package com.sun.corba.se.impl.dynamicany
 * @package com.sun.corba.se.impl.encoding
 * @package com.sun.corba.se.impl.interceptors
 * @package com.sun.corba.se.impl.io
 * @package com.sun.corba.se.impl.ior
 * @package com.sun.corba.se.impl.ior.iiop
 * @package com.sun.corba.se.impl.javax.rmi
 * @package com.sun.corba.se.impl.javax.rmi.CORBA
 * @package com.sun.corba.se.impl.legacy.connection
 * @package com.sun.corba.se.impl.logging
 * @package com.sun.corba.se.impl.monitoring
 * @package com.sun.corba.se.impl.naming.cosnaming
 * @package com.sun.corba.se.impl.naming.namingutil
 * @package com.sun.corba.se.impl.naming.pcosnaming
 * @package com.sun.corba.se.impl.oa
 * @package com.sun.corba.se.impl.oa.poa
 * @package com.sun.corba.se.impl.oa.toa
 * @package com.sun.corba.se.impl.orb
 * @package com.sun.corba.se.impl.orbutil
 * @package com.sun.corba.se.impl.orbutil.closure
 * @package com.sun.corba.se.impl.orbutil.concurrent
 * @package com.sun.corba.se.impl.orbutil.fsm
 * @package com.sun.corba.se.impl.orbutil.graph
 * @package com.sun.corba.se.impl.orbutil.resources
 * @package com.sun.corba.se.impl.orbutil.threadpool
 * @package com.sun.corba.se.impl.presentation.rmi
 * @package com.sun.corba.se.impl.protocol
 * @package com.sun.corba.se.impl.protocol.giopmsgheaders
 * @package com.sun.corba.se.impl.resolver
 * @package com.sun.corba.se.impl.transport
 * @package com.sun.corba.se.impl.util
 * @package com.sun.corba.se.internal.CosNaming
 * @package com.sun.corba.se.internal.Interceptors
 * @package com.sun.corba.se.internal.POA
 * @package com.sun.corba.se.internal.corba
 * @package com.sun.corba.se.internal.iiop
 * @package com.sun.corba.se.org.omg.CORBA
 * @package com.sun.corba.se.pept.broker
 * @package com.sun.corba.se.pept.encoding
 * @package com.sun.corba.se.pept.protocol
 * @package com.sun.corba.se.pept.transport
 * @package com.sun.corba.se.spi.activation
 * @package com.sun.corba.se.spi.activation.InitialNameServicePackage
 * @package com.sun.corba.se.spi.activation.LocatorPackage
 * @package com.sun.corba.se.spi.activation.RepositoryPackage
 * @package com.sun.corba.se.spi.copyobject
 * @package com.sun.corba.se.spi.encoding
 * @package com.sun.corba.se.spi.extension
 * @package com.sun.corba.se.spi.ior
 * @package com.sun.corba.se.spi.ior.iiop
 * @package com.sun.corba.se.spi.legacy.connection
 * @package com.sun.corba.se.spi.legacy.interceptor
 * @package com.sun.corba.se.spi.logging
 * @package com.sun.corba.se.spi.monitoring
 * @package com.sun.corba.se.spi.oa
 * @package com.sun.corba.se.spi.orb
 * @package com.sun.corba.se.spi.orbutil.closure
 * @package com.sun.corba.se.spi.orbutil.fsm
 * @package com.sun.corba.se.spi.orbutil.proxy
 * @package com.sun.corba.se.spi.orbutil.threadpool
 * @package com.sun.corba.se.spi.presentation.rmi
 * @package com.sun.corba.se.spi.protocol
 * @package com.sun.corba.se.spi.resolver
 * @package com.sun.corba.se.spi.servicecontext
 * @package com.sun.corba.se.spi.transport
 * @package com.sun.jndi.cosnaming
 * @package com.sun.jndi.toolkit.corba
 * @package com.sun.jndi.url.corbaname
 * @package com.sun.jndi.url.iiop
 * @package com.sun.jndi.url.iiopname
 * @package com.sun.org.omg.CORBA
 * @package com.sun.org.omg.CORBA.ValueDefPackage
 * @package com.sun.org.omg.CORBA.portable
 * @package com.sun.org.omg.SendingContext
 * @package com.sun.org.omg.SendingContext.CodeBasePackage
 * @package com.sun.tools.corba.se.idl
 * @package com.sun.tools.corba.se.idl.constExpr
 * @package com.sun.tools.corba.se.idl.som.cff
 * @package com.sun.tools.corba.se.idl.som.idlemit
 * @package com.sun.tools.corba.se.idl.toJavaPortable
 * @package javax.activity
 * @package javax.rmi
 * @package javax.rmi.CORBA
 * @package org.omg.CORBA
 * @package org.omg.CORBA.DynAnyPackage
 * @package org.omg.CORBA.ORBPackage
 * @package org.omg.CORBA.TypeCodePackage
 * @package org.omg.CORBA.portable
 * @package org.omg.CORBA_2_3
 * @package org.omg.CORBA_2_3.portable
 * @package org.omg.CosNaming
 * @package org.omg.CosNaming.NamingContextExtPackage
 * @package org.omg.CosNaming.NamingContextPackage
 * @package org.omg.Dynamic
 * @package org.omg.DynamicAny
 * @package org.omg.DynamicAny.DynAnyFactoryPackage
 * @package org.omg.DynamicAny.DynAnyPackage
 * @package org.omg.IOP
 * @package org.omg.IOP.CodecFactoryPackage
 * @package org.omg.IOP.CodecPackage
 * @package org.omg.Messaging
 * @package org.omg.PortableInterceptor
 * @package org.omg.PortableInterceptor.ORBInitInfoPackage
 * @package org.omg.PortableServer
 * @package org.omg.PortableServer.CurrentPackage
 * @package org.omg.PortableServer.POAManagerPackage
 * @package org.omg.PortableServer.POAPackage
 * @package org.omg.PortableServer.ServantLocatorPackage
 * @package org.omg.PortableServer.portable
 * @package org.omg.SendingContext
 * @package org.omg.stub.java.rmi
 * @package sun.corba
 */
module java.corba {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.desktop;
	requires java.logging;
	requires java.naming;
	/**
	 * @modifiers transitive
	 */
	requires transitive java.rmi;
	requires java.transaction;
	requires jdk.unsupported;
	exports com.sun.corba.se.impl.util to 
		jdk.rmic;
	exports com.sun.jndi.url.corbaname to 
		java.naming;
	exports com.sun.jndi.url.iiop to 
		java.naming;
	exports com.sun.jndi.url.iiopname to 
		java.naming;
	exports javax.activity;
	exports javax.rmi;
	exports javax.rmi.CORBA;
	exports org.omg.CORBA;
	exports org.omg.CORBA.DynAnyPackage;
	exports org.omg.CORBA.ORBPackage;
	exports org.omg.CORBA.TypeCodePackage;
	exports org.omg.CORBA.portable;
	exports org.omg.CORBA_2_3;
	exports org.omg.CORBA_2_3.portable;
	exports org.omg.CosNaming;
	exports org.omg.CosNaming.NamingContextExtPackage;
	exports org.omg.CosNaming.NamingContextPackage;
	exports org.omg.Dynamic;
	exports org.omg.DynamicAny;
	exports org.omg.DynamicAny.DynAnyFactoryPackage;
	exports org.omg.DynamicAny.DynAnyPackage;
	exports org.omg.IOP;
	exports org.omg.IOP.CodecFactoryPackage;
	exports org.omg.IOP.CodecPackage;
	exports org.omg.Messaging;
	exports org.omg.PortableInterceptor;
	exports org.omg.PortableInterceptor.ORBInitInfoPackage;
	exports org.omg.PortableServer;
	exports org.omg.PortableServer.CurrentPackage;
	exports org.omg.PortableServer.POAManagerPackage;
	exports org.omg.PortableServer.POAPackage;
	exports org.omg.PortableServer.ServantLocatorPackage;
	exports org.omg.PortableServer.portable;
	exports org.omg.SendingContext;
	exports org.omg.stub.java.rmi;
	opens com.sun.jndi.cosnaming to 
		java.naming;
}