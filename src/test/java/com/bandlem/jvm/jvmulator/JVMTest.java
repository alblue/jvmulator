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
		final JVM jvm3 = new JVM();
		jvm3.setBytecode(code);
		while (--steps > 0) {
			jvm3.step();
		}
		assertThrows(expected, jvm3::step);
	}
	private void expect(final double result, final byte[] code) {
		final JVM jvm = run(code);
		assertEquals(result, jvm.stack.pop().doubleValue());
		assertThrows(IndexOutOfBoundsException.class, jvm.stack::peek);
	}
	private void expect(final float result, final byte[] code) {
		final JVM jvm = run(code);
		assertEquals(result, jvm.stack.pop().floatValue());
		assertThrows(IndexOutOfBoundsException.class, jvm.stack::peek);
	}
	private void expect(final int result, final byte[] code) {
		final JVM jvm = run(code);
		assertEquals(result, jvm.stack.pop().intValue());
		assertThrows(IndexOutOfBoundsException.class, jvm.stack::peek);
	}
	private void expect(final long result, final byte[] code) {
		final JVM jvm = run(code);
		assertEquals(result, jvm.stack.pop().longValue());
		assertThrows(IndexOutOfBoundsException.class, jvm.stack::peek);
	}
	private JVM run(final byte[] code) {
		final JVM jvm = new JVM();
		jvm.setBytecode(code);
		jvm.run();
		return jvm;
	}
	@Test
	void testArray() {
		for (final byte b : new byte[] {
				'Z', 'B', 'S', 'C', 'I', 'L', 'F', 'D'
		}) {
			expect(2, new byte[] {
					ICONST_2, NEWARRAY, b, ARRAYLENGTH
			});
			if (b == 'L') {
				expect(1L, new byte[] {
						ICONST_1, NEWARRAY, b, DUP, ICONST_0, LCONST_1, AASTORE, ICONST_0, AALOAD
				});
			} else if (b == 'F') {
				expect(1F, new byte[] {
						ICONST_1, NEWARRAY, b, DUP, ICONST_0, FCONST_1, AASTORE, ICONST_0, AALOAD
				});
			} else if (b == 'D') {
				expect(1D, new byte[] {
						ICONST_1, NEWARRAY, b, DUP, ICONST_0, DCONST_1, AASTORE, ICONST_0, AALOAD
				});
			} else if (b == 'Z') {
				expect(1, new byte[] {
						ICONST_1, NEWARRAY, b, DUP, ICONST_0, ICONST_1, AASTORE, ICONST_0, AALOAD
				});
				expect(0, new byte[] {
						ICONST_1, NEWARRAY, b, DUP, ICONST_0, ICONST_0, AASTORE, ICONST_0, AALOAD
				});
			} else {
				expect(1, new byte[] {
						ICONST_1, NEWARRAY, b, DUP, ICONST_0, ICONST_1, AASTORE, ICONST_0, AALOAD
				});
			}
		}
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
				ICONST_1, NEWARRAY, 'I', DUP, ICONST_0, ICONST_2, AASTORE, ICONST_0, AALOAD
		});
	}
	@Test
	void testBadStackSwap() {
		final JVM jvm = new JVM();
		jvm.setBytecode(new byte[] {
				LCONST_0, DCONST_0, SWAP
		});
		jvm.step();
		jvm.step();
		assertThrows(IllegalStateException.class, jvm::step);
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
		expect(5, new byte[] {
				GOTO, 0x00, 0x08, ICONST_1, ICONST_2, GOTO, 0x00, 0x06, GOTO, (byte) 0xff, (byte) -4, ICONST_3, IADD
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
			final JVM jvm = new JVM();
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
			jvm.setBytecode(code);
			while (steps-- > 0) {
				jvm.step();
			}
			assertDoesNotThrow(jvm::step, "Opcode " + name + " not supported (" + b + ")");
		}
	}
}
