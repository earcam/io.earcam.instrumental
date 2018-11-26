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
package io.earcam.instrumental.module.osgi.parser;

import static io.earcam.instrumental.module.osgi.ClauseParameters.EMPTY_PARAMETERS;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import io.earcam.instrumental.module.manifest.ManifestInfoBuilder;
import io.earcam.instrumental.module.osgi.BundleInfoBuilder;
import io.earcam.instrumental.module.osgi.ClauseParameters;
import io.earcam.instrumental.module.osgi.parser.ManifestParser.*;

class AntlrParser extends ManifestBaseListener {

	private final BundleInfoBuilder builder = BundleInfoBuilder.bundle();

	private static class State {
		SortedSet<String> uniqueNames = new TreeSet<>();
		ClauseParameters parameters = new ClauseParameters();
		String parameterKey;
		BiConsumer<String, String> parameterAssign;


		public void clear()
		{
			uniqueNames = new TreeSet<>();
			if(!parameters.isEmpty()) {
				parameters = new ClauseParameters();
			}
		}


		public ClauseParameters completeParameters()
		{
			return parameters.isEmpty() ? EMPTY_PARAMETERS : parameters;
		}
	}

	State state = new State();


	/**
	 * <p>
	 * builder.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public BundleInfoBuilder builder()
	{
		return builder;
	}


	/**
	 * <p>
	 * parse.
	 * </p>
	 *
	 * @param parser a {@link io.earcam.instrumental.module.osgi.parser.ManifestParser} object.
	 * @return a {@link io.earcam.instrumental.module.osgi.BundleInfoBuilder} object.
	 */
	public static BundleInfoBuilder parse(ManifestParser parser)
	{
		AntlrParser listener = new AntlrParser();
		Parsing.walk(parser.manifest(), listener);
		return listener.builder();
	}


	@Override
	public void exitExports(ExportsContext ctx)
	{
		super.exitExports(ctx);
		builder.exportPackages(state.uniqueNames, state.completeParameters());
		state.clear();
	}


	@Override
	public void enterPaquet(PaquetContext ctx)
	{
		super.enterPaquet(ctx);
		state.uniqueNames.add(ctx.getText());
	}


	@Override
	public void enterAttribute(AttributeContext ctx)
	{
		super.enterAttribute(ctx);
		state.parameterKey = ctx.getChild(0).getText();
		state.parameterAssign = state.parameters::attribute;
	}


	@Override
	public void enterDirective(DirectiveContext ctx)
	{
		super.enterDirective(ctx);
		state.parameterKey = ctx.getChild(0).getText();
		state.parameterAssign = state.parameters::directive;
	}


	@Override
	public void enterArgument(ArgumentContext ctx)
	{
		super.enterArgument(ctx);
		state.parameterAssign.accept(state.parameterKey, ctx.getText());
	}


	@Override
	public void exitImports(ImportsContext ctx)
	{
		super.exitImports(ctx);
		builder.importPackages(state.uniqueNames, state.completeParameters());
		state.clear();
	}


	@Override
	public void enterDynamicDescription(DynamicDescriptionContext ctx)
	{
		super.enterDynamicDescription(ctx);
		state.uniqueNames.add(ctx.getText());
	}


	@Override
	public void exitDynamicImports(DynamicImportsContext ctx)
	{
		super.exitDynamicImports(ctx);
		builder.dynamicImportPackages(state.uniqueNames, state.completeParameters());
		state.clear();
	}


	@Override
	public void enterSymbolicName(SymbolicNameContext ctx)
	{
		super.enterSymbolicName(ctx);
		state.uniqueNames.add(ctx.getChild(2).getText());
	}


	@Override
	public void exitSymbolicName(SymbolicNameContext ctx)
	{
		super.exitSymbolicName(ctx);
		builder.symbolicName(state.uniqueNames.first(), state.completeParameters());
		state.clear();
	}


	@Override
	public void enterFragmentHost(FragmentHostContext ctx)
	{
		super.enterFragmentHost(ctx);
		state.uniqueNames.add(ctx.getChild(2).getText());
	}


	@Override
	public void exitFragmentHost(FragmentHostContext ctx)
	{
		super.exitFragmentHost(ctx);
		builder.fragmentHost(state.uniqueNames.first(), state.completeParameters());
		state.clear();
	}


	@Override
	public void enterBundleManifestVersion(BundleManifestVersionContext ctx)
	{
		super.enterBundleManifestVersion(ctx);
		builder.bundleManifestVersion(ctx.getChild(2).getText());
	}


	@Override
	public void enterBundleActivator(BundleActivatorContext ctx)
	{
		super.enterBundleActivator(ctx);
		builder.activator(ctx.getChild(2).getText());
	}


	@Override
	public void enterGenericManifestEntry(GenericManifestEntryContext ctx)
	{
		super.enterGenericManifestEntry(ctx);
		builder.manifestMain(ManifestInfoBuilder.attribute(ctx.getChild(0).getText(), ctx.getChild(2).getText()));
	}
}
