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
package io.earcam.instrumental.module.osgi;

import static io.earcam.instrumental.module.manifest.ManifestInfoBuilder.attribute;
import static io.earcam.instrumental.module.osgi.BundleManifestHeaders.BUNDLE_MANIFESTVERSION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Attributes.Name;
import java.util.Map.Entry;

import io.earcam.instrumental.module.manifest.AbstractManifestBuilder;
import io.earcam.unexceptional.Exceptional;

class DefaultBundleInfo extends AbstractManifestBuilder<BundleInfoBuilder> implements BundleInfo, BundleInfoBuilder {

	private static final String BUNDLE_MANIFESTVERSION_RELEASE_4 = "2";
	private final Map<Name, List<Clause>> clauses = new HashMap<>();


	/** {@inheritDoc} */
	@Override
	public boolean equals(Object other)
	{
		return super.equals(other);
	}


	/** {@inheritDoc} */
	@Override
	public boolean equals(AbstractManifestBuilder<?> that)
	{
		return that instanceof DefaultBundleInfo
				&& same(that)
				&& Objects.deepEquals(((DefaultBundleInfo) that).clauses, this.clauses);
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), clauses);
	}


	/** {@inheritDoc} */
	@Override
	public BundleInfoBuilder headerClause(Name header, Clause clause)
	{
		clauses.computeIfAbsent(header, h -> new ArrayList<>()).add(clause);
		return self();
	}


	/** {@inheritDoc} */
	@Override
	protected DefaultBundleInfo self()
	{
		return this;
	}


	/** {@inheritDoc} */
	@Override
	protected void preBuildHook()
	{
		if(!mainAttributes.containsKey(BUNDLE_MANIFESTVERSION.header())) {
			bundleManifestVersion(BUNDLE_MANIFESTVERSION_RELEASE_4);
		}

		for(Entry<Name, List<Clause>> e : clauses.entrySet()) {
			StringBuilder santa = new StringBuilder();
			List<Clause> claus = e.getValue();
			for(int i = 0; i < claus.size(); i++) {
				Exceptional.accept(claus.get(i)::appendTo, santa);
				if(i < claus.size() - 1) {
					santa.append(';');
				}
			}

			manifestMain(attribute(e.getKey(), santa.toString()));
		}
	}


	/** {@inheritDoc} */
	@Override
	public BundleInfo construct()
	{
		hook();
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public BundleInfoBuilder deconstruct()
	{
		unhook();
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public Map<Name, List<Clause>> allHeaderClauses()
	{
		return clauses;
	}
}
