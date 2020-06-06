/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.compiler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.function.Consumer;
import javax.tools.SimpleJavaFileObject;
public class ClassFile extends SimpleJavaFileObject {
	private final ByteArrayOutputStream baos;
	ClassFile(final String name, final Consumer<byte[]> listener) {
		super(URI.create("memory:///" + name + Kind.CLASS.extension), Kind.CLASS);
		baos = new NotifyingByteArrayOutputStream(1024, listener);
	}
	@Override
	public OutputStream openOutputStream() throws IOException {
		return baos;
	}
}
