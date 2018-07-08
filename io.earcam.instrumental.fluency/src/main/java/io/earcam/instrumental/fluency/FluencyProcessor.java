/*-
 * #%L
 * io.earcam.instrumental.fluency
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
package io.earcam.instrumental.fluency;

import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import io.earcam.instrumental.fluent.Fluent;

/**
 * <p>
 * FluencyProcessor class.
 * </p>
 *
 */
@SupportedOptions({ FluencyProcessor.OPTION_NAME })
public class FluencyProcessor extends AbstractProcessor {

	static final String OPTION_NAME = "name";

	private class FluentMethod {

		// TODO type parameters
		// TODO We need to know if package-private or protected - then stuff into same package

		// TODO constructors should also be permitted - this would allow package-private classes exposure

		final ExecutableElement methodElement;
		final String comment;


		FluentMethod(ExecutableElement methodElement)
		{
			this.methodElement = methodElement;
			this.comment = nabComment();
		}


		Name methodName()
		{
			return methodElement.getSimpleName();
		}


		Name returnTypeName()
		{
			return typeName(returnType());
		}


		TypeMirror returnType()
		{
			return methodElement.getReturnType();
		}


		private Name typeName(TypeMirror type)
		{
			return type.getKind().isPrimitive() ? processingEnv.getElementUtils().getName(type.getKind().name().toLowerCase(ROOT))
					: ((TypeElement) processingEnv.getTypeUtils().asElement(type)).getQualifiedName();
		}


		Name ownerTypeSimpleName()
		{
			return ownerType().getSimpleName();
		}


		TypeElement ownerType()
		{
			return (TypeElement) methodElement.getEnclosingElement();
		}


		PackageElement pkg()
		{
			return (PackageElement) ownerType().getEnclosingElement();
		}


		Name packageName()
		{
			return pkg().getQualifiedName();
		}


		List<Map.Entry<Name, Name>> parameterNames()
		{
			return methodElement.getParameters().stream()
					.map(p -> new AbstractMap.SimpleEntry<>(p.getSimpleName(), typeName(p.asType())))
					.collect(toList());
		}


		String javadoc()
		{
			return comment;
		}


		private String nabComment()
		{
			String javadoc = processingEnv.getElementUtils().getDocComment(methodElement);
			return (javadoc == null) ? "" : makeComment(javadoc);
		}


		private String makeComment(String javadoc)
		{
			return "\n\t/**" + javadoc.replace("\n", "\n\t *") + "\n\t*/\n";
		}


		public Appendable appendTo(Appendable text) throws IOException
		{
			text.append(javadoc())
					.append('\t')
					.append("public static final ")
					.append(returnTypeName())
					.append(' ')
					.append(methodName())
					.append(parametersToString())
					.append("\n\t{\n\t\t");

			if(!returnTypeName().contentEquals("void")) {
				text.append("return ");
			}
			return text.append(packageName())
					.append('.')
					.append(ownerTypeSimpleName())
					.append('.')
					.append(methodName())
					.append(argumentsToString())
					.append(";\n\t}\n");
		}


		private String parametersToString()
		{
			return parameterNames().stream().map(e -> e.getValue() + " " + e.getKey()).collect(joining(", ", "(", ")"));
		}


		private String argumentsToString()
		{
			return parameterNames().stream().map(Map.Entry::getKey).collect(joining(", ", "(", ")"));
		}
	}

	private final List<FluentMethod> methods = new ArrayList<>();
	private String name;


	/** {@inheritDoc} */
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv)
	{
		super.init(processingEnv);

		name = processingEnv.getOptions().getOrDefault(OPTION_NAME, "FluentApi");
	}


	/** {@inheritDoc} */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		processingEnv.getMessager().printMessage(Kind.NOTE, "starting processing round ...");
		// needs to be a one-per package where a package-private member is annotated ...

		for(Element element : roundEnv.getElementsAnnotatedWith(Fluent.class)) {
			ExecutableElement method = (ExecutableElement) element;

			Set<Modifier> modifiers = method.getModifiers();
			if(!modifiers.contains(STATIC) || modifiers.contains(PRIVATE)) {
				processingEnv.getMessager().printMessage(Kind.WARNING, "Skipping as private or not static, modifiers:" + modifiers, element);
				continue;
			}

			FluentMethod fluentMethod = new FluentMethod(method);

			methods.add(fluentMethod);
		}

		if(roundEnv.processingOver()) {
			generateSource();
		}

		return true;
	}


	private void generateSource()
	{
		String paquet = "com.acme";
		String fqn = paquet + '.' + name;
		JavaFileObject jfo;
		try {
			jfo = processingEnv.getFiler().createSourceFile(fqn);
			try(BufferedWriter bw = new BufferedWriter(jfo.openWriter())) {
				bw.append("package ").append(paquet).append(";\n\n");

				// TODO date must be optional (as otherwise "different" source each time)
				// JAVA9 FAILS due to split package issues with `javax.annotation`
				// bw.append('@').append("javax.annotation.Generated")
				// .append("(value=\"").append(FluencyProcessor.class.getCanonicalName()).append('"')
				// .append(")\n")

				bw.append("public final class ").append(name).append(" {\n\n");

				for(FluentMethod method : methods) {
					method.appendTo(bw);
				}

				bw.append("\n}\n");
			}
		} catch(IOException e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			processingEnv.getMessager().printMessage(Kind.ERROR, writer.toString());
		}
	}


	/** {@inheritDoc} */
	@Override
	public Set<String> getSupportedAnnotationTypes()
	{
		return Collections.singleton(Fluent.class.getCanonicalName());
	}


	/** {@inheritDoc} */
	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}
}
