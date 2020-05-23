/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
public class SlotTest {
	private static final Slot doubleSlot = Slot.of(4.0d);
	private static final Slot empty = Slot.empty();
	private static final Slot floatSlot = Slot.of(2.0f);
	private static final Slot intSlot = Slot.of(1);
	private static final Slot longSlot = Slot.of(3L);
	@Test
	void testDoubleSlot() {
		final Slot slot = doubleSlot;
		assertEquals(4, slot.doubleValue());
		assertTrue(slot.isWide());
		assertThrows(ClassCastException.class, slot::intValue);
		assertThrows(ClassCastException.class, slot::longValue);
		assertThrows(ClassCastException.class, slot::floatValue);
	}
	@Test
	void testEmptySlot() {
		final Slot slot = empty;
		assertFalse(slot.isWide());
		assertThrows(ClassCastException.class, slot::intValue);
		assertThrows(ClassCastException.class, slot::longValue);
		assertThrows(ClassCastException.class, slot::floatValue);
		assertThrows(ClassCastException.class, slot::doubleValue);
	}
	@Test
	void testFloatSlot() {
		final Slot slot = floatSlot;
		assertEquals(2, slot.floatValue());
		assertFalse(slot.isWide());
		assertThrows(ClassCastException.class, slot::intValue);
		assertThrows(ClassCastException.class, slot::longValue);
		assertThrows(ClassCastException.class, slot::doubleValue);
	}
	@Test
	void testIntSlot() {
		final Slot slot = intSlot;
		assertEquals(1, slot.intValue());
		assertFalse(slot.isWide());
		assertThrows(ClassCastException.class, slot::longValue);
		assertThrows(ClassCastException.class, slot::floatValue);
		assertThrows(ClassCastException.class, slot::doubleValue);
	}
	@Test
	void testLongSlot() {
		final Slot slot = longSlot;
		assertEquals(3, slot.longValue());
		assertTrue(slot.isWide());
		assertThrows(ClassCastException.class, slot::intValue);
		assertThrows(ClassCastException.class, slot::floatValue);
		assertThrows(ClassCastException.class, slot::doubleValue);
	}
}
