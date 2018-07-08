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

import static io.earcam.instrumental.compile.ByteArrayJavaFileObject.uriFromName;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;

import io.earcam.unexceptional.Exceptional;

public interface FileObjectProvider {

	public static final class CustomJavaFileObject extends SimpleJavaFileObject {

		private final String binaryName;
		private Supplier<byte[]> bytes;


		public CustomJavaFileObject(String binaryName, Kind kind, Path file)
		{
			super(file.toUri(), kind);
			this.binaryName = binaryName;
			bytes = () -> Exceptional.apply(Files::readAllBytes, file);
		}


		public CustomJavaFileObject(String binaryName, Kind kind, byte[] array)
		{
			super(uriFromName(binaryName, kind), kind);
			this.binaryName = binaryName;
			this.bytes = () -> array;
		}


		public String inferredBinaryName()
		{
			return binaryName;
		}


		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException
		{
			return new String(bytes.get(), UTF_8);
		}


		@Override
		public InputStream openInputStream() throws IOException
		{
			return new ByteArrayInputStream(bytes.get());
		}
	}


	public abstract List<CustomJavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException;

}
