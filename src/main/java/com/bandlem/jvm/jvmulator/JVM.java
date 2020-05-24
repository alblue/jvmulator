/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
import net.bytebuddy.jar.asm.Opcodes;
public class JVM {
	private byte[] bytecode;
	private int pc;
	Stack stack = new Stack();
	public void run() {
		while (pc != bytecode.length) {
			step();
		}
	}
	public void setBytecode(final byte[] code) {
		bytecode = code;
	}
	public void step() {
		final byte code = bytecode[pc++];
		switch (code) {
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
		// Stack manipulation
		case Opcodes.SWAP:
			final Slot first = stack.pop();
			final Slot second = stack.pop();
			if (first.isWide() || second.isWide()) {
				throw new IllegalStateException("Cannot swap wide slots");
			}
			stack.pushSlot(first);
			stack.pushSlot(second);
			return;
		default:
			throw new IllegalStateException("Unknown opcode: " + code);
		}
	}
}
