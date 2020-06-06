/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.compiler;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
public class NotifyingByteArrayOutputStreamTest {
	@Test
	void testNBAOS() throws IOException {
		final byte[][] result = new byte[1][];
		final Consumer<byte[]> listener = (data) -> {
			result[0] = data;
		};
		try (final NotifyingByteArrayOutputStream nbaos = new NotifyingByteArrayOutputStream(0, listener)) {
			final String message = "Hello World!";
			nbaos.write(message.getBytes());
			assertNull(result[0]);
			nbaos.flush();
			assertNull(result[0]);
			nbaos.close();
			assertArrayEquals(message.getBytes(), result[0]);
		}
	}
	@Test
	void testNBAOSNullThrows() {
		assertThrows(IllegalArgumentException.class, () -> new NotifyingByteArrayOutputStream(0, null));
	}
}
