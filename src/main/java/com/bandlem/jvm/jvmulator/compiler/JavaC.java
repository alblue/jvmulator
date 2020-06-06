/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.compiler;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
public class JavaC {
	private final DiagnosticCollector<JavaFileObject> collector;
	private final JavaCompiler compiler;
	private final InMemoryFileManager fileManager;
	public JavaC() {
		this(new DiagnosticCollector<JavaFileObject>(), ToolProvider.getSystemJavaCompiler(),
				new InMemoryFileManager(ToolProvider.getSystemJavaCompiler()));
	}
	public JavaC(final DiagnosticCollector<JavaFileObject> collector, final JavaCompiler compiler,
			final InMemoryFileManager fileManager) {
		this.collector = Objects.requireNonNull(collector);
		this.compiler = Objects.requireNonNull(compiler);
		this.fileManager = Objects.requireNonNull(fileManager);
	}
	public boolean compile(final SourceFile... source) {
		final CompilationTask task = compiler.getTask(null, fileManager, collector, null, null, Arrays.asList(source));
		return task.call();
	}
	public byte[] getBytes(final String name) {
		return fileManager.getBytes(name);
	}
	public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
		return collector.getDiagnostics();
	}
	public ClassLoader newClassLoader() {
		return fileManager.newClassLoader();
	}
}
