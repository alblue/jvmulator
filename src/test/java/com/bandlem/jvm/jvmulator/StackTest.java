/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
public class StackTest {
	private Stack stack;
	@BeforeEach
	void newStack() {
		stack = new Stack();
	}
	@Test
	void testBadStack() {
		stack.push(0);
		assertEquals(0, stack.at(0).intValue());
		assertEquals(1, stack.size());
		stack.pushSlot(Slot.empty());
		assertThrows(IllegalStateException.class, stack::peek);
		assertThrows(IllegalStateException.class, stack::pop);
	}
	@Test
	void testDoublePushPeekPop() {
		stack.push(8.0D);
		final Slot s = stack.peek();
		assertFalse(s == Slot.empty());
		assertEquals(8.0D, s.doubleValue());
	}
	@Test
	void testIncompatiblePop() {
		final Slot[] slots = new Slot[] {
				Slot.of(1), Slot.of(2f), Slot.of(3L), Slot.of(4d)
		};
		final Executable[] ops = new Executable[] {
				stack::popInt, stack::popFloat, stack::popLong, stack::popDouble
		};
		for (final Slot s : slots) {
			stack.pushSlot(s);
			final Slot same = stack.pop();
			assertEquals(s, same);
		}
		for (int s = 0; s < 4; s++) {
			for (int o = 0; o < 4; o++) {
				stack.pushSlot(slots[s]);
				final Executable op = ops[o];
				if (s == o) {
					assertDoesNotThrow(op);
				} else {
					assertThrows(ClassCastException.class, op);
				}
			}
		}
	}
	@Test
	void testPeek() {
		stack.push(2.0D);
		assertEquals(2.0D, stack.peek().doubleValue());
		assertEquals(2.0D, stack.pop().doubleValue());
		stack.push(2L);
		assertEquals(2L, stack.peek().longValue());
		assertEquals(2L, stack.pop().longValue());
		stack.push(2F);
		assertEquals(2F, stack.peek().floatValue());
		assertEquals(2F, stack.pop().floatValue());
		stack.push(2);
		assertEquals(2, stack.peek().intValue());
		assertEquals(2, stack.pop().intValue());
	}
	@Test
	void testPushNull() {
		stack.push(null);
		assertNull(stack.popReference());
		assertThrows(IllegalArgumentException.class, () -> stack.pushSlot(null));
	}
	@Test
	void testReference() {
		stack.push("Hello World");
		stack.push(null);
		assertNull(stack.popReference());
		assertEquals("Hello World", stack.popReference());
	}
	@Test
	void testStackPopEmpty() {
		assertThrows(IndexOutOfBoundsException.class, stack::pop);
	}
	@Test
	void testStackPushBoolean() {
		stack.push(true);
		assertEquals(true, stack.pop().booleanValue());
		stack.push(false);
		assertEquals(false, stack.pop().booleanValue());
	}
	@Test
	void testStackPushDouble() {
		stack.push(2.0d);
		assertEquals(2.0d, stack.pop().doubleValue());
	}
	@Test
	void testStackPushFloat() {
		stack.push(2.0f);
		assertEquals(2.0f, stack.pop().floatValue());
	}
	@Test
	void testStackPushInt() {
		stack.push(1);
		assertEquals(1, stack.pop().intValue());
	}
	@Test
	void testStackPushLong() {
		stack.push(3L);
		assertEquals(3L, stack.pop().longValue());
	}
}
