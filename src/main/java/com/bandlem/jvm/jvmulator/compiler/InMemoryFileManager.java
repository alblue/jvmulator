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
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
public class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
	private final Map<String, byte[]> classes = Collections.synchronizedMap(new HashMap<>());
	public InMemoryFileManager() {
		this(ToolProvider.getSystemJavaCompiler());
	}
	public InMemoryFileManager(final JavaCompiler javaCompiler) {
		super(javaCompiler.getStandardFileManager(null, null, StandardCharsets.UTF_8));
	}
	public byte[] getBytes(final String name) {
		return classes.get(name);
	}
	@Override
	public JavaFileObject getJavaFileForOutput(final Location location, final String className,
			final JavaFileObject.Kind kind, final FileObject sibling) throws IOException {
		return new ClassFile(className, bytes -> classes.put(className, bytes));
	}
	public ClassLoader newClassLoader() {
		return new InMemoryClassLoader(classes, getClass().getClassLoader());
	}
}
