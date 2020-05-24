/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
import static com.bandlem.jvm.jvmulator.JVMType.BOOLEAN;
import static com.bandlem.jvm.jvmulator.JVMType.BYTE;
import static com.bandlem.jvm.jvmulator.JVMType.CHAR;
import static com.bandlem.jvm.jvmulator.JVMType.DOUBLE;
import static com.bandlem.jvm.jvmulator.JVMType.INTEGER;
import static com.bandlem.jvm.jvmulator.JVMType.LONG;
import static com.bandlem.jvm.jvmulator.JVMType.SHORT;
import static com.bandlem.jvm.jvmulator.JVMType.VOID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
public class JVMTypeTest {
	@Test
	void testTypeIDs() {
		assertEquals('D', DOUBLE.id);
		assertEquals('L', LONG.id);
		assertEquals('I', INTEGER.id);
		assertEquals('S', SHORT.id);
		assertEquals('C', CHAR.id);
		assertEquals('B', BYTE.id);
		assertEquals('Z', BOOLEAN.id);
		assertEquals('V', VOID.id);
	}
	@Test
	void testTypeSizes() {
		assertEquals(64, DOUBLE.size);
		assertEquals(64, LONG.size);
		assertEquals(32, INTEGER.size);
		assertEquals(16, SHORT.size);
		assertEquals(16, CHAR.size);
		assertEquals(8, BYTE.size);
		assertEquals(1, BOOLEAN.size);
		assertEquals(0, VOID.size);
	}
}
