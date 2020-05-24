/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
public class JVM {
	private byte[] bytecode;
	private int pc;
	Stack stack = new Stack();
	private Slot notWide(final Slot slot, final byte opcode) {
		if (slot.isWide()) {
			throw new IllegalStateException("Cannot use wide slot for opcode " + opcode);
		}
		return slot;
	}
	public void run() {
		while (pc != bytecode.length) {
			step();
		}
	}
	public void setBytecode(final byte[] code) {
		bytecode = code;
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
		default:
			throw new IllegalStateException("Unknown opcode: " + Opcodes.name(opcode) + " [" + opcode + "]");
		}
	}
}
