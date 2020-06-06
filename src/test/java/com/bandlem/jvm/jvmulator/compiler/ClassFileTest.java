/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.compiler;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import org.junit.jupiter.api.Test;
public class ClassFileTest {
	@Test
	void testClassFile() throws IOException {
		final byte[][] datas = new byte[1][1];
		final ClassFile classFile = new ClassFile("Output", (data) -> {
			datas[0] = data;
		});
		final Writer writer = classFile.openWriter();
		final String message = "Hello World!";
		writer.write(message);
		writer.close();
		assertEquals(message.getBytes().length, datas[0].length);
	}
	@Test
	void testSourceFile() throws IOException {
		final String depressedMoose = "Depressed Moose";
		final SourceFile file = new SourceFile("Input", depressedMoose);
		final String line = new BufferedReader(file.openReader(true)).readLine();
		assertEquals(depressedMoose, line);
	}
}
