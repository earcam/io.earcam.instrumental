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

import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.instrumental.module.jpms.ModuleInfoBuilder;
import io.earcam.instrumental.module.jpms.RequireModifier;
import io.earcam.instrumental.module.jpms.parser.Java9BaseListener;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.ExportsDirectiveContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.ModuleDeclarationContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.ModuleNameContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.OpensDirectiveContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.ProvidesDirectiveContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.RequiresDirectiveContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.RequiresModifierContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.UsesDirectiveContext;

// TODO annotations, and possibly comments?
/**
 * <p>
 * ModuleInfoParser class.
 * </p>
 *
 */
class AntlrParser extends Java9BaseListener {

	private final ModuleInfoBuilder builder = ModuleInfo.moduleInfo();


	/**
	 * <p>
	 * builder.
	 * </p>
	 *
	 * @return a {@link io.earcam.instrumental.module.jpms.ModuleInfoBuilder} object.
	 */
	public ModuleInfoBuilder builder()
	{
		return builder;
	}


	@Override
	public void enterModuleDeclaration(ModuleDeclarationContext ctx)
	{
		super.enterModuleDeclaration(ctx);
		builder.named(ctx.moduleName().getText());
	}


	@Override
	public void enterRequiresDirective(RequiresDirectiveContext ctx)
	{
		super.enterRequiresDirective(ctx);
		Set<RequireModifier> modifiers = ctx.requiresModifier().stream()
				.map(RequiresModifierContext::getText)
				.map(m -> m.toUpperCase(ROOT))
				.map(RequireModifier::valueOf)
				.collect(toSet());

		builder.requiring(ctx.moduleName().getText(), modifiers, null);
	}


	@Override
	public void enterExportsDirective(ExportsDirectiveContext ctx)
	{
		super.enterExportsDirective(ctx);
		String[] to = toModuleNames(ctx.moduleName().stream());
		builder.exporting(ctx.packageName().getText(), to);
	}


	private String[] toModuleNames(Stream<ModuleNameContext> stream)
	{
		return stream
				.map(ModuleNameContext::getText)
				.toArray(s -> new String[s]);
	}


	@Override
	public void enterOpensDirective(OpensDirectiveContext ctx)
	{
		super.enterOpensDirective(ctx);
		String[] to = toModuleNames(ctx.moduleName().stream());
		builder.opening(ctx.packageName().getText(), to);
	}


	@Override
	public void enterUsesDirective(UsesDirectiveContext ctx)
	{
		super.enterUsesDirective(ctx);
		builder.using(ctx.getChild(1).getText());
	}


	@Override
	public void enterProvidesDirective(ProvidesDirectiveContext ctx)
	{
		super.enterProvidesDirective(ctx);

		TreeSet<String> concretes = new TreeSet<>();
		for(int i = 3; i < ctx.getChildCount(); i += 2) {
			concretes.add(ctx.getChild(i).getText());
		}

		builder.providing(ctx.getChild(1).getText(), concretes);
	}
}
