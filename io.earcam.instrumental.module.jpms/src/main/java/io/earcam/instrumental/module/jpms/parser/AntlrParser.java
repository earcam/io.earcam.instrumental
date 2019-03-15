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

import static io.earcam.instrumental.module.jpms.ExportModifier.MANDATED;
import static java.util.Locale.ROOT;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import io.earcam.instrumental.module.jpms.ExportModifier;
import io.earcam.instrumental.module.jpms.Modifier;
import io.earcam.instrumental.module.jpms.ModuleInfo;
import io.earcam.instrumental.module.jpms.ModuleInfoBuilder;
import io.earcam.instrumental.module.jpms.ModuleModifier;
import io.earcam.instrumental.module.jpms.RequireModifier;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.ExportsDirectiveContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.ModuleDeclarationContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.ModuleNameContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.OpensDirectiveContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.ProvidesDirectiveContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.RequiresDirectiveContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.RequiresModifierContext;
import io.earcam.instrumental.module.jpms.parser.Java9Parser.UsesDirectiveContext;

/**
 * <p>
 * ModuleInfoParser; reads {@code module-info.java} source
 * </p>
 *
 */
class AntlrParser extends Java9BaseListener {

	private static final Pattern VERSION_PATTERN = Pattern.compile("@version\\s+([^\\s]+)", DOTALL | MULTILINE);
	private static final Pattern PACKAGE_PATTERN = Pattern.compile(".*?@package\\s+([^\\s]+)", DOTALL | MULTILINE);
	private static final Pattern MODIFIERS_PATTERN = Pattern.compile("@modifiers\\s+(.*?)\\s*\r?\n", MULTILINE);

	private final ModuleInfoBuilder builder = ModuleInfo.moduleInfo();
	private CommonTokenStream tokenStream;


	public AntlrParser(TokenStream tokenStream)
	{
		this.tokenStream = (CommonTokenStream) tokenStream;
	}


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
		String comment = processComments(ctx);
		String version = processVersionComment(comment);
		SortedSet<CharSequence> packages = processPackagesComment(comment);
		Set<ModuleModifier> modifiers = processModifiersComment(ModuleModifier.class, comment);
		builder.versioned(version);
		builder.packaging(packages);
		builder.withAccess(modifiers);

		builder.named(ctx.moduleName().getText());
	}


	private String processVersionComment(String comment)
	{
		Matcher matcher = VERSION_PATTERN.matcher(comment);
		return matcher.find() ? matcher.group(1) : null;
	}


	private SortedSet<CharSequence> processPackagesComment(String comment)
	{
		Matcher matcher = PACKAGE_PATTERN.matcher(comment);
		SortedSet<CharSequence> packages = new TreeSet<>();

		while(matcher.find()) {
			String paq = matcher.group(1);
			packages.add(paq);
		}
		return packages;
	}


	private <M extends Enum<M> & Modifier<M>> Set<M> processModifiersComment(Class<M> type, String comment)
	{
		Matcher matcher = MODIFIERS_PATTERN.matcher(comment);
		if(matcher.find()) {
			return Modifier.from(type, matcher.group(1));
		}
		return Collections.emptySet();
	}


	@Override
	public void enterRequiresDirective(RequiresDirectiveContext ctx)
	{
		String comment = processComments(ctx);
		String version = processVersionComment(comment);

		Set<RequireModifier> modifiers = ctx.requiresModifier().stream()
				.map(RequiresModifierContext::getText)
				.map(m -> m.toUpperCase(ROOT))
				.map(RequireModifier::valueOf)
				.collect(toSet());

		modifiers.addAll(processModifiersComment(RequireModifier.class, comment));

		builder.requiring(ctx.moduleName().getText(), modifiers, version);
	}


	@Override
	public void enterExportsDirective(ExportsDirectiveContext ctx)
	{
		String comment = processComments(ctx);
		Set<ExportModifier> modifiers = processExportsModifiersComment(comment);

		String[] to = toModuleNames(ctx.moduleName().stream());
		builder.exporting(ctx.packageName().getText(), modifiers, to);
	}


	private Set<ExportModifier> processExportsModifiersComment(String comment)
	{
		Set<ExportModifier> modifiers = processModifiersComment(ExportModifier.class, comment);
		return modifiers.isEmpty() ? EnumSet.of(MANDATED) : modifiers;
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
		String comment = processComments(ctx);
		Set<ExportModifier> modifiers = processExportsModifiersComment(comment);

		String[] to = toModuleNames(ctx.moduleName().stream());
		builder.opening(ctx.packageName().getText(), modifiers, to);
	}


	@Override
	public void enterUsesDirective(UsesDirectiveContext ctx)
	{
		builder.using(ctx.getChild(1).getText());
	}


	@Override
	public void enterProvidesDirective(ProvidesDirectiveContext ctx)
	{
		TreeSet<String> concretes = new TreeSet<>();
		for(int i = 3; i < ctx.getChildCount(); i += 2) {
			concretes.add(ctx.getChild(i).getText());
		}

		builder.providing(ctx.getChild(1).getText(), concretes);
	}


	private String processComments(ParserRuleContext ctx)
	{
		List<Token> comment = tokenStream.getHiddenTokensToLeft(ctx.getStart().getTokenIndex());
		return (comment == null || comment.isEmpty()) ? "" : comment.get(0).getText();
	}
}
