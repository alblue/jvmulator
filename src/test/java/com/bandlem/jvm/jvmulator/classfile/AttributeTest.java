/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.classfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import com.bandlem.jvm.jvmulator.classfile.Attribute.Code;
import com.bandlem.jvm.jvmulator.classfile.Attribute.SourceFile;
import com.bandlem.jvm.jvmulator.classfile.Attribute.Unknown;
public class AttributeTest {
	@Test
	void testCodeAttribute() {
		final Code code = (Code) Attribute.of("Code", null, new byte[] {
				0x00, 0x01, 0x00, 0x02, 0x00, 0x00, 0x00, 0x02, (byte) 0xca, (byte) 0xfe
		});
		assertEquals("Code", code.attributeName);
		assertEquals(1, code.getMaxStack());
		assertEquals(2, code.getMaxLocals());
		final byte[] bytecode = code.getBytecode();
		assertEquals(2, bytecode.length);
		assertEquals((byte) 0xca, bytecode[0]);
		assertEquals((byte) 0xfe, bytecode[1]);
	}
	@Test
	void testIncompleteData() {
		assertThrows(IllegalArgumentException.class, () -> Attribute.of("Code", null, new byte[] {}));
	}
	@Test
	void testSoruceFileAttribute() throws IOException {
		final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(new byte[] {
				ConstantPool.UTFConstant.TYPE, 0x00, 0x02, 'O', 'K'
		}));
		final ConstantPool pool = new ConstantPool((short) 2, dis);
		final SourceFile file = (SourceFile) Attribute.of("SourceFile", pool, new byte[] {
				0x00, 0x01
		});
		assertEquals("OK", file.file);
		assertEquals("SourceFile", file.attributeName);
	}
	@Test
	void testUnknown() {
		final byte[] input = new byte[] {
				0x61, 0x6c, 0x62, 0x6c, 0x75, 0x65
		};
		final Unknown attribute = (Unknown) Attribute.of("alblue", null, input);
		assertEquals("alblue", attribute.attributeName);
		assertEquals(6, attribute.data.length);
		for (int i = 0; i < input.length; i++) {
			assertEquals(input[i], attribute.data[i]);
		}
	}
}
