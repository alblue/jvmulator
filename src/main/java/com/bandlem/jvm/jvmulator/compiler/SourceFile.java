/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.compiler;
import java.io.IOException;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;
public class SourceFile extends SimpleJavaFileObject {
	private final String source;
	public SourceFile(final String name, final String source) {
		super(URI.create("memory:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
		this.source = source;
	}
	@Override
	public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws IOException {
		return source;
	}
}
