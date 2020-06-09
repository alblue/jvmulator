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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import com.bandlem.jvm.jvmulator.classfile.Member.Field;
import com.bandlem.jvm.jvmulator.classfile.Member.Method;
public class MemberTest {
	@Test
	void testDescriptor() {
		final Class<?>[] types = Method.argumentTypes("(ZSCIJFDLjava/lang/String;Z)V", getClass().getClassLoader());
		assertEquals(9, types.length);
		assertEquals(Boolean.TYPE, types[0]);
		assertEquals(Short.TYPE, types[1]);
		assertEquals(Character.TYPE, types[2]);
		assertEquals(Integer.TYPE, types[3]);
		assertEquals(Long.TYPE, types[4]);
		assertEquals(Float.TYPE, types[5]);
		assertEquals(Double.TYPE, types[6]);
		assertEquals(String.class, types[7]);
		assertEquals(Boolean.TYPE, types[8]);
		assertThrows(IllegalArgumentException.class, () -> Method.argumentTypes("(?)V", null));
		assertThrows(IllegalStateException.class, () -> Method.argumentTypes("(I", null));
		assertThrows(RuntimeException.class, () -> Method.argumentTypes("(Lmissingclass;)V", null));
	}
	@Test
	void testField() {
		final Field field = new Field((short) 12, "MyField", "[I", new Attribute[] {});
		assertEquals(12, field.flags);
		assertEquals("MyField", field.name);
		assertEquals("[I", field.descriptor);
		assertNotNull(field.attributes);
		assertNull(field.getAttribute("Unknown"));
	}
	@Test
	void testMethod() {
		final Attribute[] attributes = new Attribute[] {
				Attribute.of(Attribute.Code.NAME, null, new byte[] {
						0x00, 0x01, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00
				}), Attribute.of("Unknown", null, "Unknown".getBytes(StandardCharsets.UTF_8))
		};
		final Method method = new Method((short) 34, "MyMethod", "(I)V", attributes);
		final Class<?>[] args = method.argumentTypes(MemberTest.class.getClassLoader());
		assertEquals(1, args.length);
		assertEquals(Integer.TYPE, args[0]);
		assertEquals(34, method.flags);
		assertEquals("MyMethod", method.name);
		assertEquals("(I)V", method.descriptor);
		assertNotNull(method.attributes);
		assertEquals("Unknown", method.getAttribute("Unknown").attributeName);
		assertEquals("Code", method.getCodeAttribute().attributeName);
		assertEquals(0, method.getCodeAttribute().getBytecode().length);
	}
}
