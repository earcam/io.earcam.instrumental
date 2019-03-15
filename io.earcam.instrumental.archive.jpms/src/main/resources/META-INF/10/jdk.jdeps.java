/**
 * @version 10.0.2
 * @package com.sun.tools.classfile
 * @package com.sun.tools.javap
 * @package com.sun.tools.javap.resources
 * @package com.sun.tools.jdeprscan
 * @package com.sun.tools.jdeprscan.resources
 * @package com.sun.tools.jdeprscan.scan
 * @package com.sun.tools.jdeps
 * @package com.sun.tools.jdeps.resources
 */
module jdk.jdeps {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	requires java.compiler;
	requires jdk.compiler;
	exports com.sun.tools.classfile to 
		jdk.jlink;
	provides java.util.spi.ToolProvider with 
		com.sun.tools.javap.Main$JavapToolProvider,
		com.sun.tools.jdeps.Main$JDepsToolProvider;
}