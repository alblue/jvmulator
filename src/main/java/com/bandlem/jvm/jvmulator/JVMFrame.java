/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
public class JVMFrame {
	private final byte[] bytecode;
	private final Slot[] locals;
	private int pc;
	final Stack stack = new Stack();
	public JVMFrame(final byte[] code) {
		this(code, 0);
	}
	public JVMFrame(final byte[] code, final int locals) {
		this.bytecode = code;
		this.locals = new Slot[locals];
	}
	private Slot notWide(final Slot slot, final byte opcode) {
		if (slot.isWide()) {
			throw new IllegalStateException("Cannot use wide slot for opcode " + opcode);
		}
		return slot;
	}
	public Slot run() {
		while (pc != bytecode.length) {
			step();
		}
		return null;
	}
	public void step() {
		final byte opcode = bytecode[pc++];
		switch (opcode) {
		case Opcodes.NOP:
			return;
		// Constants
		case Opcodes.ACONST_NULL:
			stack.push(null);
			return;
		case Opcodes.ICONST_0:
			stack.push(0);
			return;
		case Opcodes.ICONST_1:
			stack.push(1);
			return;
		case Opcodes.ICONST_2:
			stack.push(2);
			return;
		case Opcodes.ICONST_3:
			stack.push(3);
			return;
		case Opcodes.ICONST_4:
			stack.push(4);
			return;
		case Opcodes.ICONST_5:
			stack.push(5);
			return;
		case Opcodes.ICONST_M1:
			stack.push(-1);
			return;
		case Opcodes.FCONST_0:
			stack.push(0F);
			return;
		case Opcodes.FCONST_1:
			stack.push(1F);
			return;
		case Opcodes.FCONST_2:
			stack.push(2F);
			return;
		case Opcodes.LCONST_0:
			stack.push(0L);
			return;
		case Opcodes.LCONST_1:
			stack.push(1L);
			return;
		case Opcodes.DCONST_0:
			stack.push(0D);
			return;
		case Opcodes.DCONST_1:
			stack.push(1D);
			return;
		case Opcodes.SIPUSH:
			stack.push(bytecode[pc++] << 8 | bytecode[pc++]);
			return;
		case Opcodes.BIPUSH:
			stack.push(bytecode[pc++]);
			return;
		// Addition
		case Opcodes.IADD:
			stack.push(stack.popInt() + stack.popInt());
			return;
		case Opcodes.FADD:
			stack.push(stack.popFloat() + stack.popFloat());
			return;
		case Opcodes.LADD:
			stack.push(stack.popLong() + stack.popLong());
			return;
		case Opcodes.DADD:
			stack.push(stack.popDouble() + stack.popDouble());
			return;
		// Multiplication
		case Opcodes.IMUL:
			stack.push(stack.popInt() * stack.popInt());
			return;
		case Opcodes.FMUL:
			stack.push(stack.popFloat() * stack.popFloat());
			return;
		case Opcodes.LMUL:
			stack.push(stack.popLong() * stack.popLong());
			return;
		case Opcodes.DMUL:
			stack.push(stack.popDouble() * stack.popDouble());
			return;
		// Division
		case Opcodes.IDIV:
			stack.push(stack.popInt() / stack.popInt());
			return;
		case Opcodes.FDIV:
			stack.push(stack.popFloat() / stack.popFloat());
			return;
		case Opcodes.LDIV:
			stack.push(stack.popLong() / stack.popLong());
			return;
		case Opcodes.DDIV:
			stack.push(stack.popDouble() / stack.popDouble());
			return;
		// Remainder
		case Opcodes.IREM:
			stack.push(stack.popInt() % stack.popInt());
			return;
		case Opcodes.FREM:
			stack.push(stack.popFloat() % stack.popFloat());
			return;
		case Opcodes.LREM:
			stack.push(stack.popLong() % stack.popLong());
			return;
		case Opcodes.DREM:
			stack.push(stack.popDouble() % stack.popDouble());
			return;
		// Subtraction
		case Opcodes.ISUB:
			stack.push(stack.popInt() - stack.popInt());
			return;
		case Opcodes.FSUB:
			stack.push(stack.popFloat() - stack.popFloat());
			return;
		case Opcodes.LSUB:
			stack.push(stack.popLong() - stack.popLong());
			return;
		case Opcodes.DSUB:
			stack.push(stack.popDouble() - stack.popDouble());
			return;
		// Negation
		case Opcodes.INEG:
			stack.push(0 - stack.popInt());
			return;
		case Opcodes.FNEG:
			stack.push(0.0F - stack.popFloat());
			return;
		case Opcodes.LNEG:
			stack.push(0L - stack.popLong());
			return;
		case Opcodes.DNEG:
			stack.push(0.0D - stack.popDouble());
			return;
		// Stack manipulation
		case Opcodes.SWAP: {
			final Slot first = notWide(stack.pop(), opcode);
			final Slot second = notWide(stack.pop(), opcode);
			stack.pushSlot(first);
			stack.pushSlot(second);
			return;
		}
		case Opcodes.DUP:
			stack.dup();
			return;
		case Opcodes.DUP_X1:
			stack.dup_x1();
			return;
		case Opcodes.DUP_X2:
			stack.dup_x2();
			return;
		case Opcodes.DUP2:
			stack.dup2();
			return;
		case Opcodes.DUP2_X1:
			stack.dup2_x1();
			return;
		case Opcodes.DUP2_X2:
			stack.dup2_x2();
			return;
		case Opcodes.POP:
			notWide(stack.pop(), opcode);
			return;
		case Opcodes.POP2:
			if (!stack.pop().isWide()) {
				notWide(stack.pop(), opcode);
			}
			return;
		// Bitwise and shift operations
		case Opcodes.ISHL: {
			final int shift = stack.popInt();
			stack.push(stack.popInt() << shift);
			return;
		}
		case Opcodes.LSHL: {
			final int shift = stack.popInt();
			stack.push(stack.popLong() << shift);
			return;
		}
		case Opcodes.ISHR: {
			final int shift = stack.popInt();
			stack.push(stack.popInt() >> shift);
			return;
		}
		case Opcodes.LSHR: {
			final int shift = stack.popInt();
			stack.push(stack.popLong() >> shift);
			return;
		}
		case Opcodes.IUSHR: {
			final int shift = stack.popInt();
			stack.push(stack.popInt() >>> shift);
			return;
		}
		case Opcodes.LUSHR: {
			final int shift = stack.popInt();
			stack.push(stack.popLong() >>> shift);
			return;
		}
		case Opcodes.IAND:
			stack.push(stack.popInt() & stack.popInt());
			return;
		case Opcodes.LAND:
			stack.push(stack.popLong() & stack.popLong());
			return;
		case Opcodes.IOR:
			stack.push(stack.popInt() | stack.popInt());
			return;
		case Opcodes.LOR:
			stack.push(stack.popLong() | stack.popLong());
			return;
		case Opcodes.IXOR:
			stack.push(stack.popInt() ^ stack.popInt());
			return;
		case Opcodes.LXOR:
			stack.push(stack.popLong() ^ stack.popLong());
			return;
		// Conversions
		case Opcodes.I2B:
			stack.push((byte) stack.popInt());
			return;
		case Opcodes.I2C:
			stack.push((char) stack.popInt());
			return;
		case Opcodes.I2S:
			stack.push((short) stack.popInt());
			return;
		case Opcodes.I2L:
			stack.push((long) stack.popInt());
			return;
		case Opcodes.I2F:
			stack.push((float) stack.popInt());
			return;
		case Opcodes.I2D:
			stack.push((double) stack.popInt());
			return;
		case Opcodes.F2I:
			stack.push((int) stack.popFloat());
			return;
		case Opcodes.F2L:
			stack.push((long) stack.popFloat());
			return;
		case Opcodes.F2D:
			stack.push((double) stack.popFloat());
			return;
		case Opcodes.D2I:
			stack.push((int) stack.popDouble());
			return;
		case Opcodes.D2L:
			stack.push((long) stack.popDouble());
			return;
		case Opcodes.D2F:
			stack.push((float) stack.popDouble());
			return;
		case Opcodes.L2I:
			stack.push((int) stack.popLong());
			return;
		case Opcodes.L2F:
			stack.push((float) stack.popLong());
			return;
		case Opcodes.L2D:
			stack.push((double) stack.popLong());
			return;
		// Comparisons
		case Opcodes.LCMP: {
			final long l1 = stack.popLong();
			final long l2 = stack.popLong();
			stack.push((int) l1 == l2 ? 0 : l1 > l2 ? 1 : -1);
			return;
		}
		case Opcodes.FCMPL: {
			final float f1 = stack.popFloat();
			final float f2 = stack.popFloat();
			stack.push((int) f1 == f2 ? 0 : f1 > f2 ? 1 : -1);
			return;
		}
		case Opcodes.FCMPG: {
			final float f1 = stack.popFloat();
			final float f2 = stack.popFloat();
			stack.push((int) f1 == f2 ? 0 : f2 > f1 ? -1 : 1);
			return;
		}
		case Opcodes.DCMPL: {
			final double f1 = stack.popDouble();
			final double f2 = stack.popDouble();
			stack.push((int) f1 == f2 ? 0 : f1 > f2 ? 1 : -1);
			return;
		}
		case Opcodes.DCMPG: {
			final double f1 = stack.popDouble();
			final double f2 = stack.popDouble();
			stack.push((int) f1 == f2 ? 0 : f2 > f1 ? -1 : 1);
			return;
		}
		// Branching
		case Opcodes.GOTO_W: {
			pc += bytecode[pc++] << 24 | bytecode[pc++] << 16 | bytecode[pc++] << 8 | bytecode[pc++] - 1;
			return;
		}
		case Opcodes.GOTO: {
			pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			return;
		}
		case Opcodes.IFEQ: {
			if (stack.popInt() == 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IFNE: {
			if (stack.popInt() != 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IFLT: {
			if (stack.popInt() < 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IFGE: {
			if (stack.popInt() >= 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IFGT: {
			if (stack.popInt() > 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IFLE: {
			if (stack.popInt() <= 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IF_ICMPEQ: {
			if (stack.popInt() == stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IF_ICMPNE: {
			if (stack.popInt() != stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IF_ICMPLT: {
			if (stack.popInt() < stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IF_ICMPGE: {
			if (stack.popInt() >= stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IF_ICMPGT: {
			if (stack.popInt() > stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IF_ICMPLE: {
			if (stack.popInt() <= stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IF_ACMPEQ: {
			if (stack.popReference() == stack.popReference()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IF_ACMPNE: {
			if (stack.popReference() != stack.popReference()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IFNULL: {
			if (stack.popReference() == null) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		case Opcodes.IFNONNULL: {
			if (stack.popReference() != null) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return;
		}
		// Arrays
		case Opcodes.NEWARRAY: {
			final int size = stack.popInt();
			final char type = (char) bytecode[pc++];
			Object array;
			switch (type) {
			case 'Z':
				array = new boolean[size];
				break;
			case 'B':
				array = new byte[size];
				break;
			case 'C':
				array = new char[size];
				break;
			case 'S':
				array = new short[size];
				break;
			case 'I':
				array = new int[size];
				break;
			case 'L':
				array = new long[size];
				break;
			case 'F':
				array = new float[size];
				break;
			case 'D':
				array = new double[size];
				break;
			default:
				throw new IllegalStateException("Unknown type: " + type + " for newarray");
			}
			stack.push(array);
			return;
		}
		case Opcodes.ARRAYLENGTH: {
			final Object array = stack.popReference();
			if (array instanceof boolean[]) {
				stack.push(((boolean[]) array).length);
			} else if (array instanceof byte[]) {
				stack.push(((byte[]) array).length);
			} else if (array instanceof char[]) {
				stack.push(((char[]) array).length);
			} else if (array instanceof short[]) {
				stack.push(((short[]) array).length);
			} else if (array instanceof int[]) {
				stack.push(((int[]) array).length);
			} else if (array instanceof long[]) {
				stack.push(((long[]) array).length);
			} else if (array instanceof float[]) {
				stack.push(((float[]) array).length);
			} else if (array instanceof double[]) {
				stack.push(((double[]) array).length);
//			} else if (array instanceof Object[]) {
//				stack.push(((Object[]) array).length);
			} else {
				throw new IllegalStateException("Unknown array type: " + array + " for arraylength");
			}
			return;
		}
		case Opcodes.BASTORE:
		case Opcodes.SASTORE:
		case Opcodes.CASTORE:
		case Opcodes.IASTORE:
		case Opcodes.LASTORE:
		case Opcodes.FASTORE:
		case Opcodes.DASTORE:
		case Opcodes.AASTORE: {
			final Slot value = stack.pop();
			final int index = stack.popInt();
			final Object array = stack.popReference();
			if (array instanceof boolean[] && opcode == Opcodes.BASTORE) {
				((boolean[]) array)[index] = value.intValue() != 0;
			} else if (array instanceof byte[] && opcode == Opcodes.BASTORE) {
				((byte[]) array)[index] = (byte) value.intValue();
			} else if (array instanceof char[] && opcode == Opcodes.CASTORE) {
				((char[]) array)[index] = (char) value.intValue();
			} else if (array instanceof short[] && opcode == Opcodes.SASTORE) {
				((short[]) array)[index] = (short) value.intValue();
			} else if (array instanceof int[] && opcode == Opcodes.IASTORE) {
				((int[]) array)[index] = value.intValue();
			} else if (array instanceof long[] && opcode == Opcodes.LASTORE) {
				((long[]) array)[index] = value.longValue();
			} else if (array instanceof float[] && opcode == Opcodes.FASTORE) {
				((float[]) array)[index] = value.floatValue();
			} else if (array instanceof double[] && opcode == Opcodes.DASTORE) {
				((double[]) array)[index] = value.doubleValue();
//			} else if (array instanceof Object[]) {
//				((Object[]) array)[index] = value.referenceValue();
			} else {
				throw new IllegalStateException("Unknown array type: " + array + " for aastore");
			}
			return;
		}
		case Opcodes.BALOAD:
		case Opcodes.SALOAD:
		case Opcodes.CALOAD:
		case Opcodes.IALOAD:
		case Opcodes.LALOAD:
		case Opcodes.FALOAD:
		case Opcodes.DALOAD:
		case Opcodes.AALOAD: {
			final int index = stack.popInt();
			final Object array = stack.popReference();
			if (array instanceof boolean[] && opcode == Opcodes.BALOAD) {
				stack.push(((boolean[]) array)[index] ? 1 : 0);
			} else if (array instanceof byte[] && opcode == Opcodes.BALOAD) {
				stack.push(((byte[]) array)[index]);
			} else if (array instanceof char[] && opcode == Opcodes.CALOAD) {
				stack.push(((char[]) array)[index]);
			} else if (array instanceof short[] && opcode == Opcodes.SALOAD) {
				stack.push(((short[]) array)[index]);
			} else if (array instanceof int[] && opcode == Opcodes.IALOAD) {
				stack.push(((int[]) array)[index]);
			} else if (array instanceof long[] && opcode == Opcodes.LALOAD) {
				stack.push(((long[]) array)[index]);
			} else if (array instanceof float[] && opcode == Opcodes.FALOAD) {
				stack.push(((float[]) array)[index]);
			} else if (array instanceof double[] && opcode == Opcodes.DALOAD) {
				stack.push(((double[]) array)[index]);
//			} else if (array instanceof Object[] && opcode == Opcodes.AALOAD) {
//				stack.push(((Object[]) array).length);
			} else {
				throw new IllegalStateException("Unknown array type: " + array + " for " + Opcodes.name(opcode));
			}
			return;
		}
		// Miscellaneous
		case Opcodes.IMPDEP1:
			// Fallthrough
		case Opcodes.IMPDEP2:
			// Fallthrough
		case Opcodes.BREAKPOINT:
			throw new IllegalArgumentException(Opcodes.name(opcode) + " should not be found here");
		default:
			throw new IllegalStateException("Unknown opcode: " + Opcodes.name(opcode) + " [" + opcode + "]");
		}
	}
}
