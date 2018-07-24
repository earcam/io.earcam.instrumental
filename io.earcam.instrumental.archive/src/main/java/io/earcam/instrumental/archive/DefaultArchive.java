/*-
 * #%L
 * io.earcam.instrumental.archive
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
package io.earcam.instrumental.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import io.earcam.unexceptional.Closing;
import io.earcam.unexceptional.Exceptional;

/**
 * <p>
 * Default implementation of {@link Archive}.
 * </p>
 *
 */
class DefaultArchive implements Archive {

	private final Optional<Manifest> manifest;
	private final List<ArchiveResource> contents;


	DefaultArchive(Optional<Manifest> manifest, List<ArchiveResource> contents)
	{
		this.manifest = manifest;
		this.contents = contents;
	}


	@Override
	public Optional<Manifest> manifest()
	{
		return manifest;
	}


	@Override
	public List<ArchiveResource> contents()
	{
		return contents;
	}


	@Override
	public <R> R to(Function<Archive, R> transformer)
	{
		return transformer.apply(this);
	}


	@Override
	public void to(OutputStream os)
	{
		JarOutputStream out = manifest()
				.map(m -> Exceptional.apply(JarOutputStream::new, os, m))
				.orElseGet(() -> Exceptional.apply(JarOutputStream::new, os));

		Closing.closeAfterAccepting(out, o -> contents()
				.stream()
				.forEach(e -> addEntry(e, o)));
	}


	private void addEntry(ArchiveResource resource, JarOutputStream target)
	{
		JarEntry entry = new JarEntry(resource.name());
		if(resource.knownSize() != -1) {
			entry.setSize(resource.knownSize());
		}
		Exceptional.accept(target::putNextEntry, entry);
		resource.write(target);
	}


	@Override
	public Path explodeTo(Path directory, boolean merge)
	{
		if(!merge && directory.toFile().exists()) {
			RecursiveDeleteVisitor.delete(directory);
		}
		directory.toFile().mkdirs();
		Path metaInf = directory.resolve("META-INF");
		metaInf.toFile().mkdirs();
		manifest()
				.ifPresent(m -> Exceptional.run(() -> m.write(new FileOutputStream(metaInf.resolve("MANIFEST.MF").toFile()))));

		contents().forEach(r -> {
			Path resource = directory.resolve(r.name());
			File dir = resource.getParent().toFile();
			dir.mkdirs();

			Closing.closeAfterAccepting(FileOutputStream::new, resource.toFile(), r::write);
		});

		return directory;
	}
}
