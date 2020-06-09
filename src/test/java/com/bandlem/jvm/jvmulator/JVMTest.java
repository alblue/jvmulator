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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import com.bandlem.jvm.jvmulator.classfile.JavaClass;
class JVMTest {
	private void expect(final Class<? extends Throwable> expected, final JavaClass javaClass, final int locals,
			final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		assertThrows(expected, frame::run);
	}
	private void expect(final double result, final JavaClass javaClass, final int locals, final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		assertEquals(result, frame.run().doubleValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final float result, final JavaClass javaClass, final int locals, final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		assertEquals(result, frame.run().floatValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final int result, final JavaClass javaClass, final int locals, final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		assertEquals(result, frame.run().intValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final long result, final JavaClass javaClass, final int locals, final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		assertEquals(result, frame.run().longValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final Object result, final JavaClass javaClass, final int locals, final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		final Slot slot = frame.run();
		if (slot != null) {
			assertEquals(result, slot.referenceValue());
		}
		assertEquals(slot, frame.getReturnValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	@Test
	void testArray() {
		for (final byte b : new byte[] {
				'Z', 'B', 'S', 'C', 'I', 'L', 'F', 'D'
		}) {
			expect(2, null, 0, new byte[] {
					ICONST_2, NEWARRAY, b, ARRAYLENGTH, IRETURN
			});
		}
		expect(1, null, 0, new byte[] {
				ICONST_1, NEWARRAY, 'Z', DUP, ICONST_0, ICONST_1, BASTORE, ICONST_0, BALOAD, IRETURN
		});
		expect(0, null, 0, new byte[] {
				ICONST_1, NEWARRAY, 'Z', DUP, ICONST_0, ICONST_0, BASTORE, ICONST_0, BALOAD, IRETURN
		});
		expect(1, null, 0, new byte[] {
				ICONST_1, NEWARRAY, 'B', DUP, ICONST_0, ICONST_1, BASTORE, ICONST_0, BALOAD, IRETURN
		});
		expect(1, null, 0, new byte[] {
				ICONST_1, NEWARRAY, 'S', DUP, ICONST_0, ICONST_1, SASTORE, ICONST_0, SALOAD, IRETURN
		});
		expect(1, null, 0, new byte[] {
				ICONST_1, NEWARRAY, 'C', DUP, ICONST_0, ICONST_1, CASTORE, ICONST_0, CALOAD, IRETURN
		});
		expect(1, null, 0, new byte[] {
				ICONST_1, NEWARRAY, 'I', DUP, ICONST_0, ICONST_1, IASTORE, ICONST_0, IALOAD, IRETURN
		});
		expect(1L, null, 0, new byte[] {
				ICONST_1, NEWARRAY, 'L', DUP, ICONST_0, LCONST_1, LASTORE, ICONST_0, LALOAD, LRETURN
		});
		expect(1F, null, 0, new byte[] {
				ICONST_1, NEWARRAY, 'F', DUP, ICONST_0, FCONST_1, FASTORE, ICONST_0, FALOAD, FRETURN
		});
		expect(1D, null, 0, new byte[] {
				ICONST_1, NEWARRAY, 'D', DUP, ICONST_0, DCONST_1, DASTORE, ICONST_0, DALOAD, DRETURN
		});
		expect(IllegalStateException.class, null, 0, new byte[] {
				ICONST_0, NEWARRAY, '?'
		});
		expect(IllegalStateException.class, null, 0, new byte[] {
				ACONST_NULL, ARRAYLENGTH
		});
		expect(IllegalStateException.class, null, 0, new byte[] {
				ACONST_NULL, ICONST_0, AALOAD
		});
		expect(IllegalStateException.class, null, 0, new byte[] {
				ACONST_NULL, ICONST_0, ICONST_1, AASTORE
		});
		expect(2, null, 0, new byte[] {
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
				expect(IllegalStateException.class, null, 0, new byte[] {
						ICONST_1, NEWARRAY, types[type], ICONST_0, one[target], store[target], RETURN
				});
			}
		}
		for (int type = 0; type < types.length; type++) {
			for (int target = 0; target < types.length; target++) {
				if (load[target] == load[type]) {
					continue;
				}
				expect(IllegalStateException.class, null, 0, new byte[] {
						ICONST_1, NEWARRAY, types[type], ICONST_0, load[target], RETURN
				});
			}
		}
	}
	@Test
	void testBadReturnStack() {
		expect(IllegalStateException.class, null, 0, new byte[] {
				ICONST_1, RETURN
		});
		expect(IllegalStateException.class, null, 0, new byte[] {
				ICONST_1, ICONST_1, IRETURN
		});
		expect(IllegalStateException.class, null, 0, new byte[] {
				ICONST_1, LCONST_1, LRETURN
		});
		expect(IllegalStateException.class, null, 0, new byte[] {
				ICONST_1, FCONST_1, FRETURN
		});
		expect(IllegalStateException.class, null, 0, new byte[] {
				ICONST_1, DCONST_1, DRETURN
		});
		expect(IllegalStateException.class, null, 0, new byte[] {
				ICONST_1, ACONST_NULL, ARETURN
		});
		expect((Object) null, null, 0, new byte[] {
				ACONST_NULL, ARETURN, NOP
		});
	}
	@Test
	void testBadStackSwap() {
		expect(IllegalStateException.class, null, 0, new byte[] {
				LCONST_0, DCONST_0, SWAP, RETURN
		});
	}
	@Test
	void testComparisons() {
		expect(0, null, 0, new byte[] {
				LCONST_1, LCONST_1, LCMP, IRETURN
		});
		expect(1, null, 0, new byte[] {
				LCONST_0, LCONST_1, LCMP, IRETURN
		});
		expect(-1, null, 0, new byte[] {
				LCONST_1, LCONST_0, LCMP, IRETURN
		});
		expect(0, null, 0, new byte[] {
				FCONST_1, FCONST_1, FCMPL, IRETURN
		});
		expect(1, null, 0, new byte[] {
				FCONST_0, FCONST_1, FCMPL, IRETURN
		});
		expect(-1, null, 0, new byte[] {
				FCONST_1, FCONST_0, FCMPL, IRETURN
		});
		expect(-1, null, 0, new byte[] {
				FCONST_0, FCONST_0, FDIV, FCONST_1, FCMPL, IRETURN
		});
		expect(0, null, 0, new byte[] {
				DCONST_1, DCONST_1, DCMPL, IRETURN
		});
		expect(1, null, 0, new byte[] {
				DCONST_0, DCONST_1, DCMPL, IRETURN
		});
		expect(-1, null, 0, new byte[] {
				DCONST_1, DCONST_0, DCMPL, IRETURN
		});
		expect(-1, null, 0, new byte[] {
				DCONST_0, DCONST_0, DDIV, DCONST_1, DCMPL, IRETURN
		});
		expect(0, null, 0, new byte[] {
				FCONST_1, FCONST_1, FCMPG, IRETURN
		});
		expect(1, null, 0, new byte[] {
				FCONST_0, FCONST_1, FCMPG, IRETURN
		});
		expect(-1, null, 0, new byte[] {
				FCONST_1, FCONST_0, FCMPG, IRETURN
		});
		expect(1, null, 0, new byte[] {
				FCONST_0, FCONST_0, FDIV, FCONST_1, FCMPG, IRETURN
		});
		expect(0, null, 0, new byte[] {
				DCONST_1, DCONST_1, DCMPG, IRETURN
		});
		expect(1, null, 0, new byte[] {
				DCONST_0, DCONST_1, DCMPG, IRETURN
		});
		expect(-1, null, 0, new byte[] {
				DCONST_1, DCONST_0, DCMPG, IRETURN
		});
		expect(1, null, 0, new byte[] {
				DCONST_0, DCONST_0, DDIV, DCONST_1, DCMPG, IRETURN
		});
	}
	@Test
	void testConstantPush() {
		expect(10, null, 0, new byte[] {
				BIPUSH, 0x0a, IRETURN
		});
		expect(314, null, 0, new byte[] {
				SIPUSH, 0x01, 0x3a, IRETURN
		});
	}
	@Test
	void testConversions() {
		expect(1L, null, 0, new byte[] {
				ICONST_1, I2L, LRETURN
		});
		expect(1F, null, 0, new byte[] {
				ICONST_1, I2F, FRETURN
		});
		expect(1D, null, 0, new byte[] {
				ICONST_1, I2D, DRETURN
		});
		expect((short) -1, null, 0, new byte[] {
				ICONST_M1, I2S, IRETURN
		});
		expect((char) -1, null, 0, new byte[] {
				ICONST_M1, I2C, IRETURN
		});
		expect((byte) -1, null, 0, new byte[] {
				ICONST_M1, I2B, IRETURN
		});
		expect(1, null, 0, new byte[] {
				LCONST_1, L2I, IRETURN
		});
		expect(1F, null, 0, new byte[] {
				LCONST_1, L2F, FRETURN
		});
		expect(1D, null, 0, new byte[] {
				LCONST_1, L2D, DRETURN
		});
		expect(1, null, 0, new byte[] {
				FCONST_1, F2I, IRETURN
		});
		expect(1L, null, 0, new byte[] {
				FCONST_1, F2L, LRETURN
		});
		expect(1D, null, 0, new byte[] {
				FCONST_1, F2D, DRETURN
		});
		expect(1, null, 0, new byte[] {
				DCONST_1, D2I, IRETURN
		});
		expect(1L, null, 0, new byte[] {
				DCONST_1, D2L, LRETURN
		});
		expect(1F, null, 0, new byte[] {
				DCONST_1, D2F, FRETURN
		});
	}
	@Test
	void testDouble() {
		expect(1.0D, null, 0, new byte[] {
				DCONST_0, DCONST_1, DADD, DRETURN
		});
		expect(-1.0D, null, 0, new byte[] {
				DCONST_1, DCONST_0, DSUB, DRETURN
		});
		expect(4.0D, null, 0, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DMUL, DRETURN
		});
		expect(1.0D, null, 0, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DDIV, DRETURN
		});
		expect(0.0D, null, 0, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DREM, DRETURN
		});
		expect(1.0D, null, 0, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DSUB, DNEG, DRETURN
		});
	}
	@Test
	void testFloat() {
		expect(3.0F, null, 0, new byte[] {
				FCONST_0, FCONST_1, FCONST_2, FADD, FADD, FRETURN
		});
		expect(4.0F, null, 0, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FMUL, FRETURN
		});
		expect(1.0F, null, 0, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FDIV, FRETURN
		});
		expect(0.0F, null, 0, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FREM, FRETURN
		});
		expect(1.0F, null, 0, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FSUB, FNEG, FRETURN
		});
	}
	@Test
	void testGoto() {
		expect(4, null, 0, new byte[] {
				ICONST_1, GOTO, 0x00, 0x07, ICONST_2, GOTO, 0x00, 0x03, ICONST_3, IADD, IRETURN
		});
		expect(4, null, 0, new byte[] {
				ICONST_1, GOTO_W, 0x00, 0x00, 0x00, 0x0b, ICONST_2, GOTO_W, 0x00, 0x00, 0x00, 0x05, ICONST_3, IADD,
				IRETURN
		});
		expect(5, null, 0, new byte[] {
				GOTO, 0x00, 0x08, ICONST_1, ICONST_2, GOTO, 0x00, 0x06, GOTO, (byte) 0xff, (byte) -4, ICONST_3, IADD,
				IRETURN
		});
		expect(5, null, 0, new byte[] {
				GOTO_W, 0x00, 0x00, 0x00, 0x0c, //
				ICONST_1, ICONST_2, //
				GOTO_W, 0x00, 0x00, 0x00, 0x0a, //
				GOTO_W, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) -6, //
				ICONST_3, IADD, IRETURN
		});
	}
	@Test
	void testIf() {
		expect(2, null, 0, new byte[] {
				ICONST_0, IFEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_1, IFEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_0, IFNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_1, IFNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_0, IFLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_1, IFLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_0, IFLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_M1, IFLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_M1, IFLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_0, IFGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_M1, IFGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_0, IFGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_1, IFGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_1, IFGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
	}
	@Test
	void testIfCmp() {
		expect(2, null, 0, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_0, ICONST_1, IF_ICMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_1, ICONST_0, IF_ICMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_M1, ICONST_0, IF_ICMPLE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_1, ICONST_0, IF_ICMPLT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_0, ICONST_0, IF_ICMPGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_1, ICONST_0, IF_ICMPGE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_M1, ICONST_0, IF_ICMPGT, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
		expect(2, null, 0, new byte[] {
				ACONST_NULL, ACONST_NULL, IF_ACMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_0, NEWARRAY, 'Z', ACONST_NULL, IF_ACMPEQ, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2,
				ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ACONST_NULL, ACONST_NULL, IF_ACMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_0, NEWARRAY, 'Z', ACONST_NULL, IF_ACMPNE, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2,
				ICONST_0, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ACONST_NULL, IFNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ACONST_NULL, IFNONNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_0, NEWARRAY, 'Z', IFNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_0, NEWARRAY, 'Z', IFNONNULL, 0x00, 0x07, ICONST_3, GOTO, 0x00, 0x04, ICONST_2, ICONST_0, IADD,
				IRETURN
		});
	}
	@Test
	void testInteger() {
		expect(24, null, 0, new byte[] {
				ICONST_4, ICONST_3, ICONST_1, ICONST_0, ICONST_M1, IADD, ISUB, IMUL, IMUL, INEG, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_5, NOP, ICONST_2, IREM, IRETURN
		});
		expect(1, null, 0, new byte[] {
				ICONST_2, ICONST_5, IREM, IRETURN
		});
		expect(0, null, 0, new byte[] {
				ICONST_5, ICONST_2, IDIV, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_2, ICONST_5, IDIV, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_1, ICONST_1, ISHL, IRETURN
		});
		expect(-1, null, 0, new byte[] {
				ICONST_M1, ICONST_1, ISHR, IRETURN
		});
		expect(-1 >>> 1, null, 0, new byte[] {
				ICONST_M1, ICONST_1, IUSHR, IRETURN
		});
		expect(0, null, 0, new byte[] {
				ICONST_1, ICONST_1, ICONST_1, IADD, IAND, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_1, ICONST_1, ICONST_1, IADD, IOR, IRETURN
		});
		expect(3, null, 0, new byte[] {
				ICONST_1, ICONST_1, ICONST_1, IADD, IXOR, IRETURN
		});
	}
	@Test
	void testJSR() {
		expect(3, null, 2, new byte[] {
				ICONST_3, JSR, 0x00, 0x04, IRETURN, ASTORE_1, RET, 0x01
		});
		expect(2, null, 2, new byte[] {
				ICONST_2, JSR_W, 0x00, 0x00, 0x00, 0x06, IRETURN, ASTORE_0, RET, 0x00
		});
	}
	@Test
	void testLocals() {
		expect(1, null, 5, new byte[] {
				ICONST_1, ISTORE_0, ILOAD_0, ISTORE_1, ILOAD_1, ISTORE_2, ILOAD_2, ISTORE_3, ILOAD_3, ISTORE, 4, ILOAD,
				4, IRETURN
		});
		expect(1, null, 0xff, new byte[] {
				ICONST_1, ISTORE, (byte) 0xfe, ILOAD, (byte) 0xfe, IRETURN
		});
		expect(2, null, 1, new byte[] {
				ICONST_1, ISTORE_0, IINC, 0, 1, ILOAD_0, IRETURN
		});
		expect(0, null, 1, new byte[] {
				ICONST_1, ISTORE_0, IINC, 0, (byte) 0xff, ILOAD_0, IRETURN
		});
		expect(1L, null, 5, new byte[] {
				LCONST_1, LSTORE_0, LLOAD_0, LSTORE_1, LLOAD_1, LSTORE_2, LLOAD_2, LSTORE_3, LLOAD_3, LSTORE, 4, LLOAD,
				4, LRETURN
		});
		expect(1L, null, 0xff, new byte[] {
				LCONST_1, LSTORE, (byte) 0xfe, LLOAD, (byte) 0xfe, LRETURN
		});
		expect(1.0F, null, 5, new byte[] {
				FCONST_1, FSTORE_0, FLOAD_0, FSTORE_1, FLOAD_1, FSTORE_2, FLOAD_2, FSTORE_3, FLOAD_3, FSTORE, 4, FLOAD,
				4, FRETURN
		});
		expect(1.0F, null, 0xff, new byte[] {
				FCONST_1, FSTORE, (byte) 0xfe, FLOAD, (byte) 0xfe, FRETURN
		});
		expect(1.0D, null, 5, new byte[] {
				DCONST_1, DSTORE_0, DLOAD_0, DSTORE_1, DLOAD_1, DSTORE_2, DLOAD_2, DSTORE_3, DLOAD_3, DSTORE, 4, DLOAD,
				4, DRETURN
		});
		expect(1.0D, null, 0xff, new byte[] {
				DCONST_1, DSTORE, (byte) 0xfe, DLOAD, (byte) 0xfe, DRETURN
		});
		expect((Object) null, null, 5, new byte[] {
				ACONST_NULL, ASTORE_0, ALOAD_0, ASTORE_1, ALOAD_1, ASTORE_2, ALOAD_2, ASTORE_3, ALOAD_3, ASTORE, 4,
				ALOAD, 4, ARETURN
		});
		expect((Object) null, null, 0xff, new byte[] {
				ACONST_NULL, ASTORE, (byte) 0xfe, ALOAD, (byte) 0xfe, ARETURN
		});
	}
	@Test
	void testLong() {
		expect(1L, null, 0, new byte[] {
				LCONST_0, LCONST_1, LADD, LRETURN
		});
		expect(4L, null, 0, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LMUL, LRETURN
		});
		expect(1L, null, 0, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LDIV, LRETURN
		});
		expect(0L, null, 0, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LREM, LRETURN
		});
		expect(1L, null, 0, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LSUB, LNEG, LRETURN
		});
		expect(2L, null, 0, new byte[] {
				LCONST_1, ICONST_1, LSHL, LRETURN
		});
		expect(-1L, null, 0, new byte[] {
				LCONST_1, LNEG, ICONST_1, LSHR, LRETURN
		});
		expect(-1L >>> 1, null, 0, new byte[] {
				LCONST_1, LNEG, ICONST_1, LUSHR, LRETURN
		});
		expect(0L, null, 0, new byte[] {
				LCONST_1, LCONST_1, LCONST_1, LADD, LAND, LRETURN
		});
		expect(3L, null, 0, new byte[] {
				LCONST_1, LCONST_1, LCONST_1, LADD, LOR, LRETURN
		});
		expect(3L, null, 0, new byte[] {
				LCONST_1, LCONST_1, LCONST_1, LADD, LXOR, LRETURN
		});
	}
	@Test
	void testMisc() {
		expect(IllegalArgumentException.class, null, 0, new byte[] {
				BREAKPOINT
		});
		expect(IllegalArgumentException.class, null, 0, new byte[] {
				IMPDEP1
		});
		expect(IllegalArgumentException.class, null, 0, new byte[] {
				IMPDEP2
		});
		expect(IllegalStateException.class, null, 0, new byte[] {
				(byte) 0xf0
		});
	}
	@Test
	void testReturn() {
		expect(1, null, 0, new byte[] {
				ICONST_1, IRETURN
		});
		expect(1L, null, 0, new byte[] {
				LCONST_1, LRETURN
		});
		expect(1F, null, 0, new byte[] {
				FCONST_1, FRETURN
		});
		expect(1D, null, 0, new byte[] {
				DCONST_1, DRETURN
		});
		expect((Object) null, null, 0, new byte[] {
				ACONST_NULL, ARETURN
		});
		expect((Object) null, null, 0, new byte[] {
				RETURN
		});
	}
	@Test
	void testStackManipulation() {
		expect(1, null, 0, new byte[] {
				ICONST_1, ICONST_0, POP, IRETURN
		});
		expect(1, null, 0, new byte[] {
				ICONST_1, ICONST_2, ICONST_3, POP2, IRETURN
		});
		expect(1.0D, null, 0, new byte[] {
				DCONST_1, DCONST_0, POP2, DRETURN
		});
		expect(-1, null, 0, new byte[] {
				ICONST_0, ICONST_1, SWAP, ISUB, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_1, DUP, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_1, ICONST_0, DUP_X1, IADD, IADD, IRETURN
		});
		expect(2, null, 0, new byte[] {
				ICONST_1, ICONST_0, ICONST_0, DUP_X2, IADD, IADD, IADD, IRETURN
		});
		expect(2.0D, null, 0, new byte[] {
				DCONST_1, DUP2, DADD, DRETURN
		});
		expect(4, null, 0, new byte[] {
				ICONST_1, ICONST_1, DUP2, IADD, IADD, IADD, IRETURN
		});
		expect(1.0D, null, 0, new byte[] {
				DCONST_1, ICONST_5, DUP2_X1, POP2, POP, DRETURN
		});
		expect(2.0D, null, 0, new byte[] {
				DCONST_1, DCONST_0, DUP2_X2, DADD, DADD, DRETURN
		});
	}
	@Test
	void testStep() {
		final JVMFrame frame = new JVMFrame(null, 0, new byte[] {
				ICONST_1, IRETURN
		});
		assertEquals(0, frame.getLocals().length);
		assertEquals(0, frame.getStack().size());
		assertEquals(0, frame.getPC());
		assertNull(frame.getReturnValue());
		assertTrue(frame.step());
		assertEquals(0, frame.getLocals().length);
		assertEquals(1, frame.getStack().size());
		assertEquals(1, frame.getPC());
		assertNull(frame.getReturnValue());
		assertFalse(frame.step());
		assertEquals(2, frame.getPC());
		assertNotNull(frame.getReturnValue());
	}
	@Test
	void testSupportedBytecodes() {
		// Contains the high water mark of implemented features
		final int max = 256;
		for (int b = 0; b < max; b++) {
			final String name = Opcodes.name((byte) b);
			// Not defined bytecodes
			if (name == null)
				continue;
			// Not supported yet - object
			if (name.startsWith("get") || name.startsWith("put") || name.startsWith("new") || name.contains("anew"))
				continue;
			// Not supported yet - switch
			if (name.endsWith("switch"))
				continue;
			// Not supported yet - invoke, throw and monitor
			if (name.startsWith("invoke") || name.startsWith("monitor") || name.equals("athrow"))
				continue;
			// Not supported yet - type casting
			if (name.equals("instanceof") || name.equals("checkcast"))
				continue;
			// Not supported yet - wide
			if (name.equals("wide"))
				continue;
			try {
				final JVMFrame frame = new JVMFrame(null, 0, new byte[] {
						(byte) b
				});
				frame.step();
				assertTrue(frame.getPC() > 0);
			} catch (final Exception e) {
				final String message = e.getMessage();
				final boolean unsupported = message.startsWith("Unknown opcode:");
				if (unsupported) {
					fail(message);
				}
			}
		}
	}
}
