/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.compiler;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.io.IOException;
import org.junit.jupiter.api.Test;
public class InMemoryFileManagerTest {
	@Test
	void testInMemory() throws IOException {
		try (final InMemoryFileManager inMemory = new InMemoryFileManager()) {
			assertNull(inMemory.getBytes("Missing"));
		}
	}
}
