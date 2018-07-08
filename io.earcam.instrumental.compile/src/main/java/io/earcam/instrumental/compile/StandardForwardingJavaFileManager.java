/*-
 * #%L
 * io.earcam.instrumental.compile
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
package io.earcam.instrumental.compile;

import java.io.File;
import java.io.IOException;

import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

class StandardForwardingJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> implements StandardJavaFileManager {

	protected StandardForwardingJavaFileManager(StandardJavaFileManager fileManager)
	{
		super(fileManager);
	}


	@Override
	public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> files)
	{
		return fileManager.getJavaFileObjectsFromFiles(files);
	}


	@Override
	public Iterable<? extends JavaFileObject> getJavaFileObjects(File... files)
	{
		return fileManager.getJavaFileObjects(files);
	}


	@Override
	public Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(Iterable<String> names)
	{
		return fileManager.getJavaFileObjectsFromStrings(names);
	}


	@Override
	public Iterable<? extends JavaFileObject> getJavaFileObjects(String... names)
	{
		return fileManager.getJavaFileObjects(names);
	}


	@Override
	public void setLocation(Location location, Iterable<? extends File> path) throws IOException
	{
		fileManager.setLocation(location, path);
	}


	@Override
	public Iterable<? extends File> getLocation(Location location)
	{
		return fileManager.getLocation(location);
	}
}
