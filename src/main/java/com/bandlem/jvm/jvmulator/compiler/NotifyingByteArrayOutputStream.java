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
import java.util.function.Consumer;
public class NotifyingByteArrayOutputStream extends ByteArrayOutputStream {
	private final Consumer<byte[]> listener;
	public NotifyingByteArrayOutputStream(final int intialSize, final Consumer<byte[]> listener) {
		super(intialSize);
		if (listener == null) {
			throw new IllegalArgumentException("Listener must be supplied");
		}
		this.listener = listener;
	}
	@Override
	public void close() throws IOException {
		super.close();
		listener.accept(toByteArray());
	}
}
