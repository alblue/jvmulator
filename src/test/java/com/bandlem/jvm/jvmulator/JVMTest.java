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
		expect(expected, code.length, code);
	}
	private void expect(final Class<? extends Throwable> expected, int steps, final byte[] code) {
		final JVMFrame frame = new JVMFrame(code);
		while (--steps > 0) {
			frame.step();
		}
		assertThrows(expected, frame::step);
	}
	private void expect(final double result, final byte[] code) {
		final JVMFrame frame = run(code);
		assertEquals(result, frame.stack.pop().doubleValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final float result, final byte[] code) {
		final JVMFrame frame = run(code);
		assertEquals(result, frame.stack.pop().floatValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final int result, final byte[] code) {
		final JVMFrame frame = run(code);
		assertEquals(result, frame.stack.pop().intValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final long result, final byte[] code) {
		final JVMFrame frame = run(code);
		assertEquals(result, frame.stack.pop().longValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private JVMFrame run(final byte[] code) {
		final JVMFrame frame = new JVMFrame(code);
		frame.run();
		return frame;
	}
	@Test
	void testArray() {
		for (final byte b : new byte[] {
				'Z', 'B', 'S', 'C', 'I', 'L', 'F', 'D'
		}) {
			expect(2, new byte[] {
					ICONST_2, NEWARRAY, b, ARRAYLENGTH
			});
		}
		expect(1, new byte[] {
				ICONST_1, NEWARRAY, 'Z', DUP, ICONST_0, ICONST_1, BASTORE, ICONST_0, BALOAD
		});
		expect(0, new byte[] {
				ICONST_1, NEWARRAY, 'Z', DUP, ICONST_0, ICONST_0, BASTORE, ICONST_0, BALOAD
		});
		expect(1, new byte[] {
				ICONST_1, NEWARRAY, 'B', DUP, ICONST_0, ICONST_1, BASTORE, ICONST_0, BALOAD
		});
		expect(1, new byte[] {
				ICONST_1, NEWARRAY, 'S', DUP, ICONST_0, ICONST_1, SASTORE, ICONST_0, SALOAD
		});
		expect(1, new byte[] {
				ICONST_1, NEWARRAY, 'C', DUP, ICONST_0, ICONST_1, CASTORE, ICONST_0, CALOAD
		});
		expect(1, new byte[] {
				ICONST_1, NEWARRAY, 'I', DUP, ICONST_0, ICONST_1, IASTORE, ICONST_0, IALOAD
		});
		expect(1L, new byte[] {
				ICONST_1, NEWARRAY, 'L', DUP, ICONST_0, LCONST_1, LASTORE, ICONST_0, LALOAD
		});
		expect(1F, new byte[] {
				ICONST_1, NEWARRAY, 'F', DUP, ICONST_0, FCONST_1, FASTORE, ICONST_0, FALOAD
		});
		expect(1D, new byte[] {
				ICONST_1, NEWARRAY, 'D', DUP, ICONST_0, DCONST_1, DASTORE, ICONST_0, DALOAD
		});
		expect(IllegalStateException.class, 2, new byte[] {
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
				ICONST_1, NEWARRAY, 'I', DUP, ICONST_0, ICONST_2, IASTORE, ICONST_0, IALOAD
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
				expect(IllegalStateException.class, 5, new byte[] {
						ICONST_1, NEWARRAY, types[type], ICONST_0, one[target], store[target]
				});
			}
		}
		for (int type = 0; type < types.length; type++) {
			for (int target = 0; target < types.length; target++) {
				if (load[target] == load[type]) {
					continue;
				}
				expect(IllegalStateException.class, 4, new byte[] {
						ICONST_1, NEWARRAY, types[type], ICONST_0, load[target]
				});
			}
		}
	}
	@Test
	void testBadStackSwap() {
		final JVMFrame frame = new JVMFrame(new byte[] {
				LCONST_0, DCONST_0, SWAP
		});
		frame.step();
		frame.step();
		assertThrows(IllegalStateException.class, frame::step);
	}
	@Test
	void testComparisons() {
		expect(0, new byte[] {
				LCONST_1, LCONST_1, LCMP
		});
		expect(1, new byte[] {
				LCONST_0, LCONST_1, LCMP
		});
		expect(-1, new byte[] {
				LCONST_1, LCONST_0, LCMP
		});
		expect(0, new byte[] {
				FCONST_1, FCONST_1, FCMPL
		});
		expect(1, new byte[] {
				FCONST_0, FCONST_1, FCMPL
		});
		expect(-1, new byte[] {
				FCONST_1, FCONST_0, FCMPL
		});
		expect(-1, new byte[] {
				FCONST_0, FCONST_0, FDIV, FCONST_1, FCMPL
		});
		expect(0, new byte[] {
				DCONST_1, DCONST_1, DCMPL
		});
		expect(1, new byte[] {
				DCONST_0, DCONST_1, DCMPL
		});
		expect(-1, new byte[] {
				DCONST_1, DCONST_0, DCMPL
		});
		expect(-1, new byte[] {
				DCONST_0, DCONST_0, DDIV, DCONST_1, DCMPL
		});
		expect(0, new byte[] {
				FCONST_1, FCONST_1, FCMPG
		});
		expect(1, new byte[] {
				FCONST_0, FCONST_1, FCMPG
		});
		expect(-1, new byte[] {
				FCONST_1, FCONST_0, FCMPG
		});
		expect(1, new byte[] {
				FCONST_0, FCONST_0, FDIV, FCONST_1, FCMPG
		});
		expect(0, new byte[] {
				DCONST_1, DCONST_1, DCMPG
		});
		expect(1, new byte[] {
				DCONST_0, DCONST_1, DCMPG
		});
		expect(-1, new byte[] {
				DCONST_1, DCONST_0, DCMPG
		});
		expect(1, new byte[] {
				DCONST_0, DCONST_0, DDIV, DCONST_1, DCMPG
		});
	}
	@Test
	void testConstantPush() {
		expect(10, new byte[] {
				BIPUSH, 0x0a
		});
		expect(314, new byte[] {
				SIPUSH, 0x01, 0x3a
		});
	}
	@Test
	void testConversions() {
		expect(1L, new byte[] {
				ICONST_1, I2L
		});
		expect(1F, new byte[] {
				ICONST_1, I2F
		});
		expect(1D, new byte[] {
				ICONST_1, I2D
		});
		expect((short) -1, new byte[] {
				ICONST_M1, I2S
		});
		expect((char) -1, new byte[] {
				ICONST_M1, I2C
		});
		expect((byte) -1, new byte[] {
				ICONST_M1, I2B
		});
		expect(1, new byte[] {
				LCONST_1, L2I
		});
		expect(1F, new byte[] {
				LCONST_1, L2F
		});
		expect(1D, new byte[] {
				LCONST_1, L2D
		});
		expect(1, new byte[] {
				FCONST_1, F2I
		});
		expect(1L, new byte[] {
				FCONST_1, F2L
		});
		expect(1D, new byte[] {
				FCONST_1, F2D
		});
		expect(1, new byte[] {
				DCONST_1, D2I
		});
		expect(1L, new byte[] {
				DCONST_1, D2L
		});
		expect(1F, new byte[] {
				DCONST_1, D2F
		});
	}
	@Test
	void testDouble() {
		expect(1.0D, new byte[] {
				DCONST_0, DCONST_1, DADD
		});
		expect(-1.0D, new byte[] {
				DCONST_1, DCONST_0, DSUB
		});
		expect(4.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DMUL
		});
		expect(1.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DDIV
		});
		expect(0.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DREM
		});
		expect(1.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DSUB, DNEG
		});
	}
	@Test
	void testFloat() {
		expect(3.0F, new byte[] {
				FCONST_0, FCONST_1, FCONST_2, FADD, FADD
		});
		expect(4.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FMUL
		});
		expect(1.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FDIV
		});
		expect(0.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FREM
		});
		expect(1.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FSUB, FNEG
		});
	}
	@Test
	void testGoto() {
		expect(4, new byte[] {
				ICONST_1, GOTO, 0x00, 0x07, ICONST_2, GOTO, 0x00, 0x03, ICONST_3, IADD
		});
		expect(4, new byte[] {
				ICONST_1, GOTO_W, 0x00, 0x00, 0x00, 0x0b, ICONST_2, GOTO_W, 0x00, 0x00, 0x00, 0x05, ICONST_3, IADD
		});
		expect(5, new byte[] {
				GOTO, 0x00, 0x08, ICONST_1, ICONST_2, GOTO, 0x00, 0x06, GOTO, (byte) 0xff, (byte) -4, ICONST_3, IADD
		});
		expect(5, new byte[] {
				GOTO_W, 0x00, 0x00, 0x00, 0x0c, //
				ICONST_1, ICONST_2, //
				GOTO_W, 0x00, 0x00, 0x00, 0x0a, //
				GOTO_W, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) -6, //
				ICONST_3, IADD
		});
	}
	@Test
	void testIf() {
		expect(2, new byte[] {
				ICONST_0, IFEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_1, IFEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_0, IFNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_1, IFNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_0, IFLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_1, IFLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_0, IFLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_M1, IFLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_M1, IFLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_0, IFGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_M1, IFGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_0, IFGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_1, IFGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_1, IFGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
	}
	@Test
	void testIfCmp() {
		expect(2, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_0, ICONST_1, IF_ICMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_1, ICONST_0, IF_ICMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_M1, ICONST_0, IF_ICMPLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_1, ICONST_0, IF_ICMPLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_1, ICONST_0, IF_ICMPGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_M1, ICONST_0, IF_ICMPGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ACONST_NULL, ACONST_NULL, IF_ACMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_0, NEWARRAY, 'Z', ACONST_NULL, IF_ACMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2,
				ICONST_0, IADD
		});
		expect(3, new byte[] {
				ACONST_NULL, ACONST_NULL, IF_ACMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_0, NEWARRAY, 'Z', ACONST_NULL, IF_ACMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2,
				ICONST_0, IADD
		});
		expect(2, new byte[] {
				ACONST_NULL, IFNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ACONST_NULL, IFNONNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(3, new byte[] {
				ICONST_0, NEWARRAY, 'Z', IFNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
		expect(2, new byte[] {
				ICONST_0, NEWARRAY, 'Z', IFNONNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD
		});
	}
	@Test
	void testInteger() {
		expect(24, new byte[] {
				ICONST_4, ICONST_3, ICONST_1, ICONST_0, ICONST_M1, IADD, ISUB, IMUL, IMUL, INEG
		});
		expect(2, new byte[] {
				ICONST_5, NOP, ICONST_2, IREM
		});
		expect(1, new byte[] {
				ICONST_2, ICONST_5, IREM
		});
		expect(0, new byte[] {
				ICONST_5, ICONST_2, IDIV
		});
		expect(2, new byte[] {
				ICONST_2, ICONST_5, IDIV
		});
		expect(2, new byte[] {
				ICONST_1, ICONST_1, ISHL
		});
		expect(-1, new byte[] {
				ICONST_M1, ICONST_1, ISHR
		});
		expect(-1 >>> 1, new byte[] {
				ICONST_M1, ICONST_1, IUSHR
		});
		expect(0, new byte[] {
				ICONST_1, ICONST_1, ICONST_1, IADD, IAND
		});
		expect(3, new byte[] {
				ICONST_1, ICONST_1, ICONST_1, IADD, IOR
		});
		expect(3, new byte[] {
				ICONST_1, ICONST_1, ICONST_1, IADD, IXOR
		});
	}
	@Test
	void testLong() {
		expect(1L, new byte[] {
				LCONST_0, LCONST_1, LADD
		});
		expect(4L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LMUL
		});
		expect(1L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LDIV
		});
		expect(0L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LREM
		});
		expect(1L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LSUB, LNEG
		});
		expect(2L, new byte[] {
				LCONST_1, ICONST_1, LSHL
		});
		expect(-1L, new byte[] {
				LCONST_1, LNEG, ICONST_1, LSHR
		});
		expect(-1L >>> 1, new byte[] {
				LCONST_1, LNEG, ICONST_1, LUSHR
		});
		expect(0L, new byte[] {
				LCONST_1, LCONST_1, LCONST_1, LADD, LAND
		});
		expect(3L, new byte[] {
				LCONST_1, LCONST_1, LCONST_1, LADD, LOR
		});
		expect(3L, new byte[] {
				LCONST_1, LCONST_1, LCONST_1, LADD, LXOR
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
	void testStackManipulation() {
		expect(1, new byte[] {
				ICONST_1, ICONST_0, POP
		});
		expect(1.0D, new byte[] {
				DCONST_1, DCONST_0, POP2
		});
		expect(-1, new byte[] {
				ICONST_0, ICONST_1, SWAP, ISUB
		});
		expect(2, new byte[] {
				ICONST_1, DUP, IADD
		});
		expect(2, new byte[] {
				ICONST_1, ICONST_0, DUP_X1, IADD, IADD
		});
		expect(2, new byte[] {
				ICONST_1, ICONST_0, ICONST_0, DUP_X2, IADD, IADD, IADD
		});
		expect(2.0D, new byte[] {
				DCONST_1, DUP2, DADD
		});
		expect(4, new byte[] {
				ICONST_1, ICONST_1, DUP2, IADD, IADD, IADD
		});
		expect(1.0D, new byte[] {
				DCONST_1, ICONST_5, DUP2_X1, POP2, POP
		});
		expect(2.0D, new byte[] {
				DCONST_1, DCONST_0, DUP2_X2, DADD, DADD
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
