/*-
 * #%L
 * io.earcam.instrumental.module.osgi
 * %%
 * Copyright (C) 2018 earcam
 * %%
 * SPDX-License-Identifier: (BSD-3-Clause OR EPL-1.0 OR Apache-2.0 OR MIT)
 * 
 * You <b>must</b> choose to accept, in full - any individual or combination of 
 * the following licenses:
 * <ul>
 * 	<li><a href="https://opensource.org/licenses/BSD-3-Clause">BSD-3-Clause</a></li>
 * 	<li><a href="https://www.eclipse.org/legal/epl-v10.html">EPL-1.0</a></li>
 * 	<li><a href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a></li>
 * 	<li><a href="https://opensource.org/licenses/MIT">MIT</a></li>
 * </ul>
 * #L%
 */
package io.earcam.instrumental.module.jpms.parser;

import org.antlr.v4.gui.TestRig;

public class ModuleRigMain {

	// Chuck text on stdin and hit ^D
	// e.g.
	// Export-Package: com.acme.aye;com.acme.bee;version=1.0.5236;resolution:=mandatory
	public static void main(String[] args) throws Exception
	{
		// String[] args = {getClass().getPackage().getName() + ".Manifest", "exports", "-tokens", "-gui", "-tree"};
		// String[] args = { getClass().getPackage().getName() + ".Manifest", "versionAttribute", "-tokens", "-gui",
		// "-tree" };
		String[] arguments = { ModuleRigMain.class.getPackage().getName() + ".Java9", "moduleDeclaration", "-tokens", "-gui", "-tree" };
		TestRig.main(arguments);

		System.in.read();
	}
}
