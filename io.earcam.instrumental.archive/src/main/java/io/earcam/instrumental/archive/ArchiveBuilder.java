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

import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.FINAL;
import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.INITIAL;
import static io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle.PRE_MANIFEST;
import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import javax.annotation.WillClose;

import io.earcam.instrumental.archive.ArchiveResourceSource.ResourceSourceLifecycle;

class ArchiveBuilder implements ArchiveConfiguration, ArchiveRegistrar {

	private final BasicArchiveResourceSource basicSource = new BasicArchiveResourceSource();
	private final List<ArchiveResourceSource> sources = new ArrayList<>();
	private final List<ArchiveResourceListener> listeners = new ArrayList<>();
	private final List<ManifestProcessor> manifestProcessors = new ArrayList<>();

	private final List<ArchiveResourceFilter> filters = new ArrayList<>();


	ArchiveBuilder()
	{
		registerResourceSource(basicSource);
	}


	@Override
	public ArchiveRegistrar registerResourceSource(ArchiveResourceSource source)
	{
		sources.add(source);
		return this;
	}


	@Override
	public ArchiveRegistrar registerResourceListener(ArchiveResourceListener listener)
	{
		listeners.add(listener);
		return this;
	}


	@Override
	public ArchiveRegistrar registerResourceFilter(ArchiveResourceFilter filter)
	{
		filters.add(filter);
		return this;
	}


	@Override
	public ArchiveRegistrar registerManifestProcessor(ManifestProcessor processor)
	{
		manifestProcessors.add(processor);
		return this;
	}


	@Override
	public ArchiveConfiguration configured(ArchiveConfigurationPlugin plugin)
	{
		plugin.attach(this);
		return this;
	}


	@Override
	public ArchiveConstruction filteredBy(ArchiveResourceFilter filter)
	{
		registerResourceFilter(filter);
		return this;
	}


	@Override
	public ArchiveConstruction sourcing(ArchiveResourceSource source)
	{
		registerResourceSource(source);
		return this;
	}


	@Override
	public ArchiveConstruction with(Class<?> type)
	{
		basicSource.with(type);
		return this;
	}


	@Override
	public ArchiveConstruction with(String name, InputStream resource)
	{
		basicSource.with(name, resource);
		return this;
	}


	@Override
	public ArchiveConstruction with(String name, byte[] contents)
	{
		basicSource.with(name, contents);
		return this;
	}


	@Override
	public Archive toObjectModel()
	{
		// @ this point no resources will have been filtered/listened

		// so we need to call all the resource-sources

		List<ArchiveResource> list = processRound(INITIAL);
		updateListeners(list);

		List<ArchiveResource> plist = processRound(PRE_MANIFEST);
		updateListeners(plist);
		list.addAll(plist);
		// Then invoke the manifestProcessors

		Manifest manifest = new Manifest();
		for(ManifestProcessor processor : manifestProcessors) {
			processor.process(manifest);
		}

		// Then final call for resources post-manifest
		plist = processRound(FINAL);
		updateListeners(plist);
		list.addAll(plist);

		return new DefaultArchive(optional(manifest), list);
	}


	private void updateListeners(List<ArchiveResource> list)
	{
		for(ArchiveResource resource : list) {
			for(ArchiveResourceListener listener : listeners) {
				listener.added(resource);
			}
		}
	}


	private List<ArchiveResource> processRound(ResourceSourceLifecycle stage)
	{
		Stream<ArchiveResource> current = Stream.empty();
		for(ArchiveResourceSource source : sources) {
			current = Stream.concat(current, source.drain(stage));
		}
		for(ArchiveResourceFilter filter : filters) {
			current = current.map(filter::apply);
		}
		return current.filter(Objects::nonNull).collect(toList());
	}


	private static Optional<Manifest> optional(Manifest manifest)
	{
		return isEmpty(manifest) ? Optional.empty() : Optional.of(manifest);
	}


	static boolean isEmpty(Manifest manifest)
	{
		return manifest.getMainAttributes().isEmpty()
				&& manifest.getEntries().isEmpty();
	}


	@Override
	public <R> R to(Function<Archive, R> transformer)
	{
		return transformer.apply(toObjectModel());
	}


	@Override
	public void to(@WillClose OutputStream os)
	{
		toObjectModel().to(os);
	}


	@Override
	public Path explodeTo(Path directory, boolean merge)
	{
		return toObjectModel().explodeTo(directory, merge);
	}
}
