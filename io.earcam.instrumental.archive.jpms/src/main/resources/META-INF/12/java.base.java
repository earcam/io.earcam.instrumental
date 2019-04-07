/**
 * @version 12
 * @package com.sun.crypto.provider
 * @package com.sun.java.util.jar.pack
 * @package com.sun.net.ssl
 * @package com.sun.net.ssl.internal.ssl
 * @package com.sun.net.ssl.internal.www.protocol.https
 * @package com.sun.security.cert.internal.x509
 * @package com.sun.security.ntlm
 * @package java.io
 * @package java.lang
 * @package java.lang.annotation
 * @package java.lang.constant
 * @package java.lang.invoke
 * @package java.lang.module
 * @package java.lang.ref
 * @package java.lang.reflect
 * @package java.math
 * @package java.net
 * @package java.net.spi
 * @package java.nio
 * @package java.nio.channels
 * @package java.nio.channels.spi
 * @package java.nio.charset
 * @package java.nio.charset.spi
 * @package java.nio.file
 * @package java.nio.file.attribute
 * @package java.nio.file.spi
 * @package java.security
 * @package java.security.acl
 * @package java.security.cert
 * @package java.security.interfaces
 * @package java.security.spec
 * @package java.text
 * @package java.text.spi
 * @package java.time
 * @package java.time.chrono
 * @package java.time.format
 * @package java.time.temporal
 * @package java.time.zone
 * @package java.util
 * @package java.util.concurrent
 * @package java.util.concurrent.atomic
 * @package java.util.concurrent.locks
 * @package java.util.function
 * @package java.util.jar
 * @package java.util.regex
 * @package java.util.spi
 * @package java.util.stream
 * @package java.util.zip
 * @package javax.crypto
 * @package javax.crypto.interfaces
 * @package javax.crypto.spec
 * @package javax.net
 * @package javax.net.ssl
 * @package javax.security.auth
 * @package javax.security.auth.callback
 * @package javax.security.auth.login
 * @package javax.security.auth.spi
 * @package javax.security.auth.x500
 * @package javax.security.cert
 * @package jdk.internal
 * @package jdk.internal.access
 * @package jdk.internal.event
 * @package jdk.internal.jimage
 * @package jdk.internal.jimage.decompressor
 * @package jdk.internal.jmod
 * @package jdk.internal.jrtfs
 * @package jdk.internal.loader
 * @package jdk.internal.logger
 * @package jdk.internal.math
 * @package jdk.internal.misc
 * @package jdk.internal.module
 * @package jdk.internal.org.objectweb.asm
 * @package jdk.internal.org.objectweb.asm.commons
 * @package jdk.internal.org.objectweb.asm.signature
 * @package jdk.internal.org.objectweb.asm.tree
 * @package jdk.internal.org.objectweb.asm.tree.analysis
 * @package jdk.internal.org.objectweb.asm.util
 * @package jdk.internal.org.xml.sax
 * @package jdk.internal.org.xml.sax.helpers
 * @package jdk.internal.perf
 * @package jdk.internal.platform
 * @package jdk.internal.platform.cgroupv1
 * @package jdk.internal.ref
 * @package jdk.internal.reflect
 * @package jdk.internal.util
 * @package jdk.internal.util.jar
 * @package jdk.internal.util.xml
 * @package jdk.internal.util.xml.impl
 * @package jdk.internal.vm
 * @package jdk.internal.vm.annotation
 * @package sun.invoke
 * @package sun.invoke.empty
 * @package sun.invoke.util
 * @package sun.launcher
 * @package sun.launcher.resources
 * @package sun.net
 * @package sun.net.dns
 * @package sun.net.ext
 * @package sun.net.ftp
 * @package sun.net.ftp.impl
 * @package sun.net.idn
 * @package sun.net.sdp
 * @package sun.net.smtp
 * @package sun.net.spi
 * @package sun.net.util
 * @package sun.net.www
 * @package sun.net.www.content.text
 * @package sun.net.www.http
 * @package sun.net.www.protocol.file
 * @package sun.net.www.protocol.ftp
 * @package sun.net.www.protocol.http
 * @package sun.net.www.protocol.http.ntlm
 * @package sun.net.www.protocol.https
 * @package sun.net.www.protocol.jar
 * @package sun.net.www.protocol.jmod
 * @package sun.net.www.protocol.jrt
 * @package sun.net.www.protocol.mailto
 * @package sun.nio
 * @package sun.nio.ch
 * @package sun.nio.cs
 * @package sun.nio.fs
 * @package sun.reflect.annotation
 * @package sun.reflect.generics.factory
 * @package sun.reflect.generics.parser
 * @package sun.reflect.generics.reflectiveObjects
 * @package sun.reflect.generics.repository
 * @package sun.reflect.generics.scope
 * @package sun.reflect.generics.tree
 * @package sun.reflect.generics.visitor
 * @package sun.reflect.misc
 * @package sun.security.action
 * @package sun.security.internal.interfaces
 * @package sun.security.internal.spec
 * @package sun.security.jca
 * @package sun.security.pkcs
 * @package sun.security.pkcs10
 * @package sun.security.pkcs12
 * @package sun.security.provider
 * @package sun.security.provider.certpath
 * @package sun.security.provider.certpath.ssl
 * @package sun.security.rsa
 * @package sun.security.ssl
 * @package sun.security.timestamp
 * @package sun.security.tools
 * @package sun.security.tools.keytool
 * @package sun.security.util
 * @package sun.security.util.math
 * @package sun.security.util.math.intpoly
 * @package sun.security.validator
 * @package sun.security.x509
 * @package sun.text
 * @package sun.text.bidi
 * @package sun.text.normalizer
 * @package sun.text.resources
 * @package sun.text.resources.cldr
 * @package sun.text.spi
 * @package sun.usagetracker
 * @package sun.util
 * @package sun.util.calendar
 * @package sun.util.cldr
 * @package sun.util.locale
 * @package sun.util.locale.provider
 * @package sun.util.logging
 * @package sun.util.resources
 * @package sun.util.resources.cldr
 * @package sun.util.spi
 */
module java.base {
	exports com.sun.security.ntlm to 
		java.security.sasl;
	exports java.io;
	exports java.lang;
	exports java.lang.annotation;
	exports java.lang.constant;
	exports java.lang.invoke;
	exports java.lang.module;
	exports java.lang.ref;
	exports java.lang.reflect;
	exports java.math;
	exports java.net;
	exports java.net.spi;
	exports java.nio;
	exports java.nio.channels;
	exports java.nio.channels.spi;
	exports java.nio.charset;
	exports java.nio.charset.spi;
	exports java.nio.file;
	exports java.nio.file.attribute;
	exports java.nio.file.spi;
	exports java.security;
	exports java.security.acl;
	exports java.security.cert;
	exports java.security.interfaces;
	exports java.security.spec;
	exports java.text;
	exports java.text.spi;
	exports java.time;
	exports java.time.chrono;
	exports java.time.format;
	exports java.time.temporal;
	exports java.time.zone;
	exports java.util;
	exports java.util.concurrent;
	exports java.util.concurrent.atomic;
	exports java.util.concurrent.locks;
	exports java.util.function;
	exports java.util.jar;
	exports java.util.regex;
	exports java.util.spi;
	exports java.util.stream;
	exports java.util.zip;
	exports javax.crypto;
	exports javax.crypto.interfaces;
	exports javax.crypto.spec;
	exports javax.net;
	exports javax.net.ssl;
	exports javax.security.auth;
	exports javax.security.auth.callback;
	exports javax.security.auth.login;
	exports javax.security.auth.spi;
	exports javax.security.auth.x500;
	exports javax.security.cert;
	exports jdk.internal to 
		jdk.jfr;
	exports jdk.internal.access to 
		java.desktop,
		java.logging,
		java.management,
		java.naming,
		java.rmi,
		jdk.jlink,
		jdk.net;
	exports jdk.internal.event to 
		jdk.jfr;
	exports jdk.internal.jimage to 
		jdk.jlink;
	exports jdk.internal.jimage.decompressor to 
		jdk.jlink;
	exports jdk.internal.jmod to 
		jdk.compiler,
		jdk.jlink;
	exports jdk.internal.loader to 
		java.instrument,
		java.logging;
	exports jdk.internal.logger to 
		java.logging;
	exports jdk.internal.misc to 
		java.desktop,
		java.logging,
		java.management,
		java.naming,
		java.net.http,
		java.rmi,
		java.security.jgss,
		java.xml,
		jdk.attach,
		jdk.charsets,
		jdk.compiler,
		jdk.internal.vm.ci,
		jdk.jfr,
		jdk.jshell,
		jdk.scripting.nashorn,
		jdk.scripting.nashorn.shell,
		jdk.unsupported;
	exports jdk.internal.module to 
		java.instrument,
		java.management.rmi,
		jdk.jartool,
		jdk.jfr,
		jdk.jlink;
	exports jdk.internal.org.objectweb.asm to 
		jdk.jartool,
		jdk.jfr,
		jdk.jlink,
		jdk.scripting.nashorn;
	exports jdk.internal.org.objectweb.asm.commons to 
		jdk.jfr,
		jdk.scripting.nashorn;
	exports jdk.internal.org.objectweb.asm.signature to 
		jdk.scripting.nashorn;
	exports jdk.internal.org.objectweb.asm.tree to 
		jdk.jfr,
		jdk.jlink;
	exports jdk.internal.org.objectweb.asm.util to 
		jdk.jfr,
		jdk.scripting.nashorn;
	exports jdk.internal.org.xml.sax to 
		jdk.jfr;
	exports jdk.internal.org.xml.sax.helpers to 
		jdk.jfr;
	exports jdk.internal.perf to 
		java.management,
		jdk.internal.jvmstat,
		jdk.management.agent;
	exports jdk.internal.ref to 
		java.desktop;
	exports jdk.internal.reflect to 
		java.logging,
		java.sql,
		java.sql.rowset,
		jdk.dynalink,
		jdk.scripting.nashorn,
		jdk.unsupported;
	exports jdk.internal.util.jar to 
		jdk.jartool;
	exports jdk.internal.util.xml to 
		jdk.jfr;
	exports jdk.internal.util.xml.impl to 
		jdk.jfr;
	exports jdk.internal.vm to 
		jdk.internal.jvmstat,
		jdk.management.agent;
	exports jdk.internal.vm.annotation to 
		jdk.internal.vm.ci,
		jdk.unsupported;
	exports sun.net to 
		java.net.http,
		jdk.naming.dns;
	exports sun.net.dns to 
		java.security.jgss,
		jdk.naming.dns;
	exports sun.net.ext to 
		jdk.net;
	exports sun.net.util to 
		java.desktop,
		java.net.http,
		jdk.jconsole;
	exports sun.net.www to 
		java.net.http,
		jdk.jartool;
	exports sun.net.www.protocol.http to 
		java.security.jgss;
	exports sun.nio.ch to 
		java.management,
		jdk.crypto.cryptoki,
		jdk.net,
		jdk.sctp;
	exports sun.nio.cs to 
		java.desktop,
		jdk.charsets;
	exports sun.reflect.annotation to 
		jdk.compiler;
	exports sun.reflect.generics.reflectiveObjects to 
		java.desktop;
	exports sun.reflect.misc to 
		java.datatransfer,
		java.desktop,
		java.management,
		java.management.rmi,
		java.rmi,
		java.sql.rowset;
	exports sun.security.action to 
		java.desktop,
		java.security.jgss;
	exports sun.security.internal.interfaces to 
		jdk.crypto.cryptoki;
	exports sun.security.internal.spec to 
		jdk.crypto.cryptoki;
	exports sun.security.jca to 
		java.smartcardio,
		jdk.crypto.cryptoki,
		jdk.crypto.ec,
		jdk.naming.dns;
	exports sun.security.pkcs to 
		jdk.crypto.ec,
		jdk.jartool;
	exports sun.security.provider to 
		java.rmi,
		java.security.jgss,
		jdk.crypto.cryptoki,
		jdk.security.auth;
	exports sun.security.provider.certpath to 
		java.naming;
	exports sun.security.rsa to 
		jdk.crypto.cryptoki;
	exports sun.security.timestamp to 
		jdk.jartool;
	exports sun.security.tools to 
		jdk.jartool;
	exports sun.security.util to 
		java.desktop,
		java.naming,
		java.rmi,
		java.security.jgss,
		java.security.sasl,
		java.smartcardio,
		java.xml.crypto,
		jdk.crypto.cryptoki,
		jdk.crypto.ec,
		jdk.jartool,
		jdk.security.auth,
		jdk.security.jgss;
	exports sun.security.util.math to 
		jdk.crypto.ec;
	exports sun.security.util.math.intpoly to 
		jdk.crypto.ec;
	exports sun.security.validator to 
		jdk.jartool;
	exports sun.security.x509 to 
		jdk.crypto.cryptoki,
		jdk.crypto.ec,
		jdk.jartool;
	exports sun.util.cldr to 
		jdk.jlink;
	exports sun.util.locale.provider to 
		java.desktop,
		jdk.jlink,
		jdk.localedata;
	exports sun.util.logging to 
		java.desktop,
		java.logging,
		java.prefs;
	exports sun.util.resources to 
		jdk.localedata;
	uses java.lang.System$LoggerFinder;
	uses java.net.ContentHandlerFactory;
	uses java.net.spi.URLStreamHandlerProvider;
	uses java.nio.channels.spi.AsynchronousChannelProvider;
	uses java.nio.channels.spi.SelectorProvider;
	uses java.nio.charset.spi.CharsetProvider;
	uses java.nio.file.spi.FileSystemProvider;
	uses java.nio.file.spi.FileTypeDetector;
	uses java.security.Provider;
	uses java.text.spi.BreakIteratorProvider;
	uses java.text.spi.CollatorProvider;
	uses java.text.spi.DateFormatProvider;
	uses java.text.spi.DateFormatSymbolsProvider;
	uses java.text.spi.DecimalFormatSymbolsProvider;
	uses java.text.spi.NumberFormatProvider;
	uses java.time.chrono.AbstractChronology;
	uses java.time.chrono.Chronology;
	uses java.time.zone.ZoneRulesProvider;
	uses java.util.spi.CalendarDataProvider;
	uses java.util.spi.CalendarNameProvider;
	uses java.util.spi.CurrencyNameProvider;
	uses java.util.spi.LocaleNameProvider;
	uses java.util.spi.ResourceBundleControlProvider;
	uses java.util.spi.ResourceBundleProvider;
	uses java.util.spi.TimeZoneNameProvider;
	uses java.util.spi.ToolProvider;
	uses javax.security.auth.spi.LoginModule;
	uses jdk.internal.logger.DefaultLoggerFinder;
	uses sun.text.spi.JavaTimeDateTimePatternProvider;
	uses sun.util.locale.provider.LocaleDataMetaInfo;
	uses sun.util.resources.LocaleData$CommonResourceBundleProvider;
	uses sun.util.resources.LocaleData$SupplementaryResourceBundleProvider;
	uses sun.util.spi.CalendarProvider;
	provides java.nio.file.spi.FileSystemProvider with 
		jdk.internal.jrtfs.JrtFileSystemProvider;
}