/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
import static com.bandlem.jvm.jvmulator.Opcodes.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
class JVMTest {
	private void expect(final Class<? extends Throwable> expected, final byte[] code) {
		final JVMFrame frame = new JVMFrame(code);
		assertThrows(expected, frame::run);
	}
	private void expect(final double result, final byte[] code) {
		final JVMFrame frame = new JVMFrame(code);
		assertEquals(result, frame.run().doubleValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final float result, final byte[] code) {
		final JVMFrame frame = new JVMFrame(code);
		assertEquals(result, frame.run().floatValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final int result, final byte[] code) {
		final JVMFrame frame = new JVMFrame(code);
		assertEquals(result, frame.run().intValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final long result, final byte[] code) {
		final JVMFrame frame = new JVMFrame(code);
		assertEquals(result, frame.run().longValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final Object result, final byte[] code) {
		final JVMFrame frame = new JVMFrame(code);
		final Slot slot = frame.run();
		if (slot != null) {
			assertEquals(result, slot.referenceValue());
		}
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	@Test
	void testArray() {
		for (final byte b : new byte[] {
				'Z', 'B', 'S', 'C', 'I', 'L', 'F', 'D'
		}) {
			expect(2, new byte[] {
					ICONST_2, NEWARRAY, b, ARRAYLENGTH, IRETURN
			});
		}
		expect(1, new byte[] {
				ICONST_1, NEWARRAY, 'Z', DUP, ICONST_0, ICONST_1, BASTORE, ICONST_0, BALOAD, IRETURN
		});
		expect(0, new byte[] {
				ICONST_1, NEWARRAY, 'Z', DUP, ICONST_0, ICONST_0, BASTORE, ICONST_0, BALOAD, IRETURN
		});
		expect(1, new byte[] {
				ICONST_1, NEWARRAY, 'B', DUP, ICONST_0, ICONST_1, BASTORE, ICONST_0, BALOAD, IRETURN
		});
		expect(1, new byte[] {
				ICONST_1, NEWARRAY, 'S', DUP, ICONST_0, ICONST_1, SASTORE, ICONST_0, SALOAD, IRETURN
		});
		expect(1, new byte[] {
				ICONST_1, NEWARRAY, 'C', DUP, ICONST_0, ICONST_1, CASTORE, ICONST_0, CALOAD, IRETURN
		});
		expect(1, new byte[] {
				ICONST_1, NEWARRAY, 'I', DUP, ICONST_0, ICONST_1, IASTORE, ICONST_0, IALOAD, IRETURN
		});
		expect(1L, new byte[] {
				ICONST_1, NEWARRAY, 'L', DUP, ICONST_0, LCONST_1, LASTORE, ICONST_0, LALOAD, LRETURN
		});
		expect(1F, new byte[] {
				ICONST_1, NEWARRAY, 'F', DUP, ICONST_0, FCONST_1, FASTORE, ICONST_0, FALOAD, FRETURN
		});
		expect(1D, new byte[] {
				ICONST_1, NEWARRAY, 'D', DUP, ICONST_0, DCONST_1, DASTORE, ICONST_0, DALOAD, DRETURN
		});
		expect(IllegalStateException.class, new byte[] {
				ICONST_0, NEWARRAY, '?'
		});
		expect(IllegalStateException.class, new byte[] {
				ACONST_NULL, ARRAYLENGTH
		});
		expect(IllegalStateException.class, new byte[] {
				ACONST_NULL, ICONST_0, AALOAD
		});
		expect(IllegalStateException.class, new byte[] {
				ACONST_NULL, ICONST_0, ICONST_1, AASTORE
		});
		expect(2, new byte[] {
				ICONST_1, NEWARRAY, 'I', DUP, ICONST_0, ICONST_2, IASTORE, ICONST_0, IALOAD, IRETURN
		});
	}
	@Test
	void testBadArrayCombinations() {
		final byte[] types = new byte[] {
				'Z', 'B', 'S', 'C', 'I', 'L', 'F', 'D'
		};
		final byte[] load = new byte[] {
				BALOAD, BALOAD, SALOAD, CALOAD, IALOAD, LALOAD, FALOAD, DALOAD
		};
		final byte[] one = new byte[] {
				ICONST_1, ICONST_1, ICONST_1, ICONST_1, ICONST_1, LCONST_1, FCONST_1, DCONST_1
		};
		final byte[] store = new byte[] {
				BASTORE, BASTORE, SASTORE, CASTORE, IASTORE, LASTORE, FASTORE, DASTORE
		};
		assertEquals(types.length, load.length);
		assertEquals(types.length, store.length);
		assertEquals(types.length, one.length);
		for (int type = 0; type < types.length; type++) {
			for (int target = 0; target < types.length; target++) {
				if (store[target] == store[type]) {
					continue;
				}
				expect(IllegalStateException.class, new byte[] {
						ICONST_1, NEWARRAY, types[type], ICONST_0, one[target], store[target], RETURN
				});
			}
		}
		for (int type = 0; type < types.length; type++) {
			for (int target = 0; target < types.length; target++) {
				if (load[target] == load[type]) {
					continue;
				}
				expect(IllegalStateException.class, new byte[] {
						ICONST_1, NEWARRAY, types[type], ICONST_0, load[target], RETURN
				});
			}
		}
	}
	@Test
	void testBadReturnStack() {
		expect(IllegalStateException.class, new byte[] {
				ICONST_1, RETURN
		});
		expect(IllegalStateException.class, new byte[] {
				ICONST_1, ICONST_1, IRETURN
		});
		expect(IllegalStateException.class, new byte[] {
				ICONST_1, LCONST_1, LRETURN
		});
		expect(IllegalStateException.class, new byte[] {
				ICONST_1, FCONST_1, FRETURN
		});
		expect(IllegalStateException.class, new byte[] {
				ICONST_1, DCONST_1, DRETURN
		});
		expect(IllegalStateException.class, new byte[] {
				ICONST_1, ACONST_NULL, ARETURN
		});
		expect((Object) null, new byte[] {
				ACONST_NULL, ARETURN, NOP
		});
	}
	@Test
	void testBadStackSwap() {
		expect(IllegalStateException.class, new byte[] {
				LCONST_0, DCONST_0, SWAP, RETURN
		});
	}
	@Test
	void testComparisons() {
		expect(0, new byte[] {
				LCONST_1, LCONST_1, LCMP, IRETURN
		});
		expect(1, new byte[] {
				LCONST_0, LCONST_1, LCMP, IRETURN
		});
		expect(-1, new byte[] {
				LCONST_1, LCONST_0, LCMP, IRETURN
		});
		expect(0, new byte[] {
				FCONST_1, FCONST_1, FCMPL, IRETURN
		});
		expect(1, new byte[] {
				FCONST_0, FCONST_1, FCMPL, IRETURN
		});
		expect(-1, new byte[] {
				FCONST_1, FCONST_0, FCMPL, IRETURN
		});
		expect(-1, new byte[] {
				FCONST_0, FCONST_0, FDIV, FCONST_1, FCMPL, IRETURN
		});
		expect(0, new byte[] {
				DCONST_1, DCONST_1, DCMPL, IRETURN
		});
		expect(1, new byte[] {
				DCONST_0, DCONST_1, DCMPL, IRETURN
		});
		expect(-1, new byte[] {
				DCONST_1, DCONST_0, DCMPL, IRETURN
		});
		expect(-1, new byte[] {
				DCONST_0, DCONST_0, DDIV, DCONST_1, DCMPL, IRETURN
		});
		expect(0, new byte[] {
				FCONST_1, FCONST_1, FCMPG, IRETURN
		});
		expect(1, new byte[] {
				FCONST_0, FCONST_1, FCMPG, IRETURN
		});
		expect(-1, new byte[] {
				FCONST_1, FCONST_0, FCMPG, IRETURN
		});
		expect(1, new byte[] {
				FCONST_0, FCONST_0, FDIV, FCONST_1, FCMPG, IRETURN
		});
		expect(0, new byte[] {
				DCONST_1, DCONST_1, DCMPG, IRETURN
		});
		expect(1, new byte[] {
				DCONST_0, DCONST_1, DCMPG, IRETURN
		});
		expect(-1, new byte[] {
				DCONST_1, DCONST_0, DCMPG, IRETURN
		});
		expect(1, new byte[] {
				DCONST_0, DCONST_0, DDIV, DCONST_1, DCMPG, IRETURN
		});
	}
	@Test
	void testConstantPush() {
		expect(10, new byte[] {
				BIPUSH, 0x0a, IRETURN
		});
		expect(314, new byte[] {
				SIPUSH, 0x01, 0x3a, IRETURN
		});
	}
	@Test
	void testConversions() {
		expect(1L, new byte[] {
				ICONST_1, I2L, LRETURN
		});
		expect(1F, new byte[] {
				ICONST_1, I2F, FRETURN
		});
		expect(1D, new byte[] {
				ICONST_1, I2D, DRETURN
		});
		expect((short) -1, new byte[] {
				ICONST_M1, I2S, IRETURN
		});
		expect((char) -1, new byte[] {
				ICONST_M1, I2C, IRETURN
		});
		expect((byte) -1, new byte[] {
				ICONST_M1, I2B, IRETURN
		});
		expect(1, new byte[] {
				LCONST_1, L2I, IRETURN
		});
		expect(1F, new byte[] {
				LCONST_1, L2F, FRETURN
		});
		expect(1D, new byte[] {
				LCONST_1, L2D, DRETURN
		});
		expect(1, new byte[] {
				FCONST_1, F2I, IRETURN
		});
		expect(1L, new byte[] {
				FCONST_1, F2L, LRETURN
		});
		expect(1D, new byte[] {
				FCONST_1, F2D, DRETURN
		});
		expect(1, new byte[] {
				DCONST_1, D2I, IRETURN
		});
		expect(1L, new byte[] {
				DCONST_1, D2L, LRETURN
		});
		expect(1F, new byte[] {
				DCONST_1, D2F, FRETURN
		});
	}
	@Test
	void testDouble() {
		expect(1.0D, new byte[] {
				DCONST_0, DCONST_1, DADD, DRETURN
		});
		expect(-1.0D, new byte[] {
				DCONST_1, DCONST_0, DSUB, DRETURN
		});
		expect(4.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DMUL, DRETURN
		});
		expect(1.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DDIV, DRETURN
		});
		expect(0.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DREM, DRETURN
		});
		expect(1.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DSUB, DNEG, DRETURN
		});
	}
	@Test
	void testFloat() {
		expect(3.0F, new byte[] {
				FCONST_0, FCONST_1, FCONST_2, FADD, FADD, FRETURN
		});
		expect(4.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FMUL, FRETURN
		});
		expect(1.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FDIV, FRETURN
		});
		expect(0.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FREM, FRETURN
		});
		expect(1.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FSUB, FNEG, FRETURN
		});
	}
	@Test
	void testGoto() {
		expect(4, new byte[] {
				ICONST_1, GOTO, 0x00, 0x07, ICONST_2, GOTO, 0x00, 0x03, ICONST_3, IADD, IRETURN
		});
		expect(4, new byte[] {
				ICONST_1, GOTO_W, 0x00, 0x00, 0x00, 0x0b, ICONST_2, GOTO_W, 0x00, 0x00, 0x00, 0x05, ICONST_3, IADD,
				IRETURN
		});
		expect(5, new byte[] {
				GOTO, 0x00, 0x08, ICONST_1, ICONST_2, GOTO, 0x00, 0x06, GOTO, (byte) 0xff, (byte) -4, ICONST_3, IADD,
				IRETURN
		});
		expect(5, new byte[] {
				GOTO_W, 0x00, 0x00, 0x00, 0x0c, //
				ICONST_1, ICONST_2, //
				GOTO_W, 0x00, 0x00, 0x00, 0x0a, //
				GOTO_W, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) -6, //
				ICONST_3, IADD, IRETURN
		});
	}
	@Test
	void testIf() {
		expect(2, new byte[] {
				ICONST_0, IFEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_1, IFEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_0, IFNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_1, IFNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_0, IFLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_1, IFLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_0, IFLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_M1, IFLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_M1, IFLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_0, IFGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_M1, IFGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_0, IFGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_1, IFGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_1, IFGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
	}
	@Test
	void testIfCmp() {
		expect(2, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_0, ICONST_1, IF_ICMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_1, ICONST_0, IF_ICMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_M1, ICONST_0, IF_ICMPLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
		expect(2, new byte[] {
				ICONST_1, ICONST_0, IF_ICMPLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_1, ICONST_0, IF_ICMPGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_M1, ICONST_0, IF_ICMPGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
		expect(2, new byte[] {
				ACONST_NULL, ACONST_NULL, IF_ACMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
		expect(3, new byte[] {
				ICONST_0, NEWARRAY, 'Z', ACONST_NULL, IF_ACMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2,
				ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ACONST_NULL, ACONST_NULL, IF_ACMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
		expect(2, new byte[] {
				ICONST_0, NEWARRAY, 'Z', ACONST_NULL, IF_ACMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2,
				ICONST_0, IADD, IRETURN
		});
		expect(2, new byte[] {
				ACONST_NULL, IFNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ACONST_NULL, IFNONNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, new byte[] {
				ICONST_0, NEWARRAY, 'Z', IFNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
		expect(2, new byte[] {
				ICONST_0, NEWARRAY, 'Z', IFNONNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
	}
	@Test
	void testInteger() {
		expect(24, new byte[] {
				ICONST_4, ICONST_3, ICONST_1, ICONST_0, ICONST_M1, IADD, ISUB, IMUL, IMUL, INEG, IRETURN
		});
		expect(2, new byte[] {
				ICONST_5, NOP, ICONST_2, IREM, IRETURN
		});
		expect(1, new byte[] {
				ICONST_2, ICONST_5, IREM, IRETURN
		});
		expect(0, new byte[] {
				ICONST_5, ICONST_2, IDIV, IRETURN
		});
		expect(2, new byte[] {
				ICONST_2, ICONST_5, IDIV, IRETURN
		});
		expect(2, new byte[] {
				ICONST_1, ICONST_1, ISHL, IRETURN
		});
		expect(-1, new byte[] {
				ICONST_M1, ICONST_1, ISHR, IRETURN
		});
		expect(-1 >>> 1, new byte[] {
				ICONST_M1, ICONST_1, IUSHR, IRETURN
		});
		expect(0, new byte[] {
				ICONST_1, ICONST_1, ICONST_1, IADD, IAND, IRETURN
		});
		expect(3, new byte[] {
				ICONST_1, ICONST_1, ICONST_1, IADD, IOR, IRETURN
		});
		expect(3, new byte[] {
				ICONST_1, ICONST_1, ICONST_1, IADD, IXOR, IRETURN
		});
	}
	@Test
	void testLong() {
		expect(1L, new byte[] {
				LCONST_0, LCONST_1, LADD, LRETURN
		});
		expect(4L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LMUL, LRETURN
		});
		expect(1L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LDIV, LRETURN
		});
		expect(0L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LREM, LRETURN
		});
		expect(1L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LSUB, LNEG, LRETURN
		});
		expect(2L, new byte[] {
				LCONST_1, ICONST_1, LSHL, LRETURN
		});
		expect(-1L, new byte[] {
				LCONST_1, LNEG, ICONST_1, LSHR, LRETURN
		});
		expect(-1L >>> 1, new byte[] {
				LCONST_1, LNEG, ICONST_1, LUSHR, LRETURN
		});
		expect(0L, new byte[] {
				LCONST_1, LCONST_1, LCONST_1, LADD, LAND, LRETURN
		});
		expect(3L, new byte[] {
				LCONST_1, LCONST_1, LCONST_1, LADD, LOR, LRETURN
		});
		expect(3L, new byte[] {
				LCONST_1, LCONST_1, LCONST_1, LADD, LXOR, LRETURN
		});
	}
	@Test
	void testMisc() {
		expect(IllegalArgumentException.class, new byte[] {
				BREAKPOINT
		});
		expect(IllegalArgumentException.class, new byte[] {
				IMPDEP1
		});
		expect(IllegalArgumentException.class, new byte[] {
				IMPDEP2
		});
		expect(IllegalStateException.class, new byte[] {
				(byte) 0xf0
		});
	}
	@Test
	void testReturn() {
		expect(1, new byte[] {
				ICONST_1, IRETURN
		});
		expect(1L, new byte[] {
				LCONST_1, LRETURN
		});
		expect(1F, new byte[] {
				FCONST_1, FRETURN
		});
		expect(1D, new byte[] {
				DCONST_1, DRETURN
		});
		expect((Object) null, new byte[] {
				ACONST_NULL, ARETURN
		});
		expect((Object) null, new byte[] {
				RETURN
		});
	}
	@Test
	void testStackManipulation() {
		expect(1, new byte[] {
				ICONST_1, ICONST_0, POP, IRETURN
		});
		expect(1.0D, new byte[] {
				DCONST_1, DCONST_0, POP2, DRETURN
		});
		expect(-1, new byte[] {
				ICONST_0, ICONST_1, SWAP, ISUB, IRETURN
		});
		expect(2, new byte[] {
				ICONST_1, DUP, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_1, ICONST_0, DUP_X1, IADD, IADD, IRETURN
		});
		expect(2, new byte[] {
				ICONST_1, ICONST_0, ICONST_0, DUP_X2, IADD, IADD, IADD, IRETURN
		});
		expect(2.0D, new byte[] {
				DCONST_1, DUP2, DADD, DRETURN
		});
		expect(4, new byte[] {
				ICONST_1, ICONST_1, DUP2, IADD, IADD, IADD, IRETURN
		});
		expect(1.0D, new byte[] {
				DCONST_1, ICONST_5, DUP2_X1, POP2, POP, DRETURN
		});
		expect(2.0D, new byte[] {
				DCONST_1, DCONST_0, DUP2_X2, DADD, DADD, DRETURN
		});
	}
	@Test
	void testSupportedBytecodes() {
		// Contains the high water mark of implemented features
		final int max = 153;
		for (int b = 0; b < max; b++) {
			final String name = Opcodes.name((byte) b);
			// Not defined bytecodes
			if (name == null)
				continue;
			// Not supported yet
			if (name.contains("load") || name.contains("store") || name.contains("ldc") || name.contains("iinc"))
				continue;
			int steps = 2;
			byte[] code;
			if (name.startsWith("l")) {
				code = new byte[] {
						LCONST_1, LCONST_0, (byte) b, 0x01, 0x02
				};
			} else if (name.startsWith("d")) {
				code = new byte[] {
						DCONST_1, DCONST_0, (byte) b, 0x01, 0x02
				};
			} else if (name.startsWith("f")) {
				code = new byte[] {
						FCONST_1, FCONST_0, (byte) b, 0x01, 0x02
				};
			} else {
				steps = 4;
				code = new byte[] {
						ICONST_4, ICONST_3, ICONST_2, ICONST_1, (byte) b, 0x01, 0x02
				};
			}
			if (name.endsWith("shr") || name.endsWith("shl")) {
				code[1] = ICONST_0;
			}
			final JVMFrame frame = new JVMFrame(code);
			while (steps-- > 0) {
				frame.step();
			}
			assertDoesNotThrow(frame::step, "Opcode " + name + " not supported (" + b + ")");
		}
	}
}
