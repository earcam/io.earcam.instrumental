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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

import io.earcam.instrumental.compile.FileObjectProvider.CustomJavaFileObject;

final class CustomJavaFileManager extends StandardForwardingJavaFileManager {

	private List<FileObjectProvider> fileProviders;


	CustomJavaFileManager(StandardJavaFileManager fileManager, List<FileObjectProvider> fileProviders)
	{
		super(fileManager);
		this.fileProviders = fileProviders;
	}


	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException
	{
		for(FileObjectProvider provider : fileProviders) {
			List<CustomJavaFileObject> list = provider.list(location, packageName, kinds, recurse);
			if(!list.isEmpty()) {
				return new ArrayList<>(list);
			}
		}
		return super.list(location, packageName, kinds, recurse);
	}


	@Override
	public String inferBinaryName(Location location, JavaFileObject file)
	{
		if(file instanceof CustomJavaFileObject) {
			return ((CustomJavaFileObject) file).inferredBinaryName();

		}
		return super.inferBinaryName(location, file);
	}
}
