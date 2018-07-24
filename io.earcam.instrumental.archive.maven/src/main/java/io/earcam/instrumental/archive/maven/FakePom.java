/*-
 * #%L
 * io.earcam.instrumental.archive.jpms
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
package io.earcam.instrumental.archive.maven;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;

final class FakePom {

	private FakePom()
	{}


	public static void createPom(MavenArtifact artifact, MavenArtifact[] dependencies, OutputStream os) throws IOException
	{
		writeHead(os);
		writeCoordinates(os, artifact);
		writeDependencies(os, dependencies);
		writeFoot(os);
	}


	private static void writeHead(OutputStream os) throws IOException
	{
		os.write(bytes(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
						"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
						"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
						"	<modelVersion>4.0.0</modelVersion>\n\n"));
	}


	private static byte[] bytes(String text)
	{
		return text.getBytes(UTF_8);
	}


	private static void writeCoordinates(OutputStream os, MavenArtifact artifact) throws IOException
	{
		writeGav(os, "\t", artifact);
		writeTag(os, "\t", "packaging", artifact.extension());
		writeNL(os);
		writeNL(os);
	}


	private static void writeGav(OutputStream os, String indent, MavenArtifact artifact) throws IOException
	{
		writeTag(os, indent, "groupId", artifact.groupId());
		writeNL(os);
		writeTag(os, indent, "artifactId", artifact.artifactId());
		writeNL(os);
		writeTag(os, indent, "version", artifact.baseVersion());
		writeNL(os);
	}


	private static void writeNL(OutputStream os) throws IOException
	{
		os.write(bytes("\n"));
	}


	private static void writeTag(OutputStream os, String indent, String tag, String content) throws IOException
	{
		openTag(os, indent, tag);
		os.write(bytes(content));
		closeTag(os, "", tag);
	}


	private static void openTag(OutputStream os, String indent, String tag) throws IOException
	{
		os.write(bytes(indent));
		os.write(bytes("<"));
		os.write(bytes(tag));
		os.write(bytes(">"));
	}


	private static void closeTag(OutputStream os, String indent, String tag) throws IOException
	{
		os.write(bytes(indent));
		os.write(bytes("</"));
		os.write(bytes(tag));
		os.write(bytes(">"));
	}


	private static void writeDependencies(OutputStream os, MavenArtifact[] dependencies) throws IOException
	{
		openTag(os, "\t", "dependencies");
		writeNL(os);
		for(MavenArtifact dependency : dependencies) {
			writeDependency(os, dependency);
			writeNL(os);
		}
		closeTag(os, "\t", "dependencies");
		writeNL(os);
	}


	private static void writeDependency(OutputStream os, MavenArtifact dependency) throws IOException
	{
		openTag(os, "\t\t", "dependency");
		writeNL(os);
		writeGav(os, "\t\t\t", dependency);
		closeTag(os, "\t\t", "dependency");
	}


	private static void writeFoot(OutputStream os) throws IOException
	{
		os.write(bytes("</project>\n"));
	}

}
