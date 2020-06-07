/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
import static com.bandlem.jvm.jvmulator.Opcodes.ALOAD;
import static com.bandlem.jvm.jvmulator.Opcodes.ANEWARRAY;
import static com.bandlem.jvm.jvmulator.Opcodes.ASTORE;
import static com.bandlem.jvm.jvmulator.Opcodes.BIPUSH;
import static com.bandlem.jvm.jvmulator.Opcodes.CHECKCAST;
import static com.bandlem.jvm.jvmulator.Opcodes.DLOAD;
import static com.bandlem.jvm.jvmulator.Opcodes.DSTORE;
import static com.bandlem.jvm.jvmulator.Opcodes.FLOAD;
import static com.bandlem.jvm.jvmulator.Opcodes.FSTORE;
import static com.bandlem.jvm.jvmulator.Opcodes.GETFIELD;
import static com.bandlem.jvm.jvmulator.Opcodes.GETSTATIC;
import static com.bandlem.jvm.jvmulator.Opcodes.GOTO;
import static com.bandlem.jvm.jvmulator.Opcodes.GOTO_W;
import static com.bandlem.jvm.jvmulator.Opcodes.IFEQ;
import static com.bandlem.jvm.jvmulator.Opcodes.IFGE;
import static com.bandlem.jvm.jvmulator.Opcodes.IFGT;
import static com.bandlem.jvm.jvmulator.Opcodes.IFLE;
import static com.bandlem.jvm.jvmulator.Opcodes.IFLT;
import static com.bandlem.jvm.jvmulator.Opcodes.IFNONNULL;
import static com.bandlem.jvm.jvmulator.Opcodes.IFNULL;
import static com.bandlem.jvm.jvmulator.Opcodes.IF_ACMPEQ;
import static com.bandlem.jvm.jvmulator.Opcodes.IF_ACMPNE;
import static com.bandlem.jvm.jvmulator.Opcodes.IF_ICMPEQ;
import static com.bandlem.jvm.jvmulator.Opcodes.IF_ICMPGE;
import static com.bandlem.jvm.jvmulator.Opcodes.IF_ICMPGT;
import static com.bandlem.jvm.jvmulator.Opcodes.IF_ICMPLE;
import static com.bandlem.jvm.jvmulator.Opcodes.IF_ICMPLT;
import static com.bandlem.jvm.jvmulator.Opcodes.IF_ICMPNE;
import static com.bandlem.jvm.jvmulator.Opcodes.IINC;
import static com.bandlem.jvm.jvmulator.Opcodes.ILOAD;
import static com.bandlem.jvm.jvmulator.Opcodes.INSTANCEOF;
import static com.bandlem.jvm.jvmulator.Opcodes.INVOKEDYNAMIC;
import static com.bandlem.jvm.jvmulator.Opcodes.INVOKEINTERFACE;
import static com.bandlem.jvm.jvmulator.Opcodes.INVOKESPECIAL;
import static com.bandlem.jvm.jvmulator.Opcodes.INVOKESTATIC;
import static com.bandlem.jvm.jvmulator.Opcodes.INVOKEVIRTUAL;
import static com.bandlem.jvm.jvmulator.Opcodes.ISTORE;
import static com.bandlem.jvm.jvmulator.Opcodes.JSR;
import static com.bandlem.jvm.jvmulator.Opcodes.JSR_W;
import static com.bandlem.jvm.jvmulator.Opcodes.LDC;
import static com.bandlem.jvm.jvmulator.Opcodes.LDC2_W;
import static com.bandlem.jvm.jvmulator.Opcodes.LDC_W;
import static com.bandlem.jvm.jvmulator.Opcodes.LLOAD;
import static com.bandlem.jvm.jvmulator.Opcodes.LOOKUPSWITCH;
import static com.bandlem.jvm.jvmulator.Opcodes.LSTORE;
import static com.bandlem.jvm.jvmulator.Opcodes.MULTIANEWARRAY;
import static com.bandlem.jvm.jvmulator.Opcodes.NEW;
import static com.bandlem.jvm.jvmulator.Opcodes.NEWARRAY;
import static com.bandlem.jvm.jvmulator.Opcodes.PUTFIELD;
import static com.bandlem.jvm.jvmulator.Opcodes.PUTSTATIC;
import static com.bandlem.jvm.jvmulator.Opcodes.RET;
import static com.bandlem.jvm.jvmulator.Opcodes.SIPUSH;
import static com.bandlem.jvm.jvmulator.Opcodes.TABLESWITCH;
import static com.bandlem.jvm.jvmulator.Opcodes.WIDE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
class OpcodesTest {
	@Test
	void testOpcodeName() {
		assertEquals("nop", Opcodes.name((byte) 0x0));
		assertEquals("aconst_null", Opcodes.name((byte) 0x1));
		assertEquals("iconst_m1", Opcodes.name((byte) 0x2));
		assertEquals("iconst_0", Opcodes.name((byte) 0x3));
		assertEquals("iconst_1", Opcodes.name((byte) 0x4));
		assertEquals("iconst_2", Opcodes.name((byte) 0x5));
		assertEquals("iconst_3", Opcodes.name((byte) 0x6));
		assertEquals("iconst_4", Opcodes.name((byte) 0x7));
		assertEquals("iconst_5", Opcodes.name((byte) 0x8));
		assertEquals("lconst_0", Opcodes.name((byte) 0x9));
		assertEquals("lconst_1", Opcodes.name((byte) 0x0a));
		assertEquals("fconst_0", Opcodes.name((byte) 0x0b));
		assertEquals("fconst_1", Opcodes.name((byte) 0x0c));
		assertEquals("fconst_2", Opcodes.name((byte) 0x0d));
		assertEquals("dconst_0", Opcodes.name((byte) 0x0e));
		assertEquals("dconst_1", Opcodes.name((byte) 0x0f));
		assertEquals("bipush", Opcodes.name((byte) 0x10));
		assertEquals("sipush", Opcodes.name((byte) 0x11));
		assertEquals("ldc", Opcodes.name((byte) 0x12));
		assertEquals("ldc_w", Opcodes.name((byte) 0x13));
		assertEquals("ldc2_w", Opcodes.name((byte) 0x14));
		assertEquals("iload", Opcodes.name((byte) 0x15));
		assertEquals("lload", Opcodes.name((byte) 0x16));
		assertEquals("fload", Opcodes.name((byte) 0x17));
		assertEquals("dload", Opcodes.name((byte) 0x18));
		assertEquals("aload", Opcodes.name((byte) 0x19));
		assertEquals("iload_0", Opcodes.name((byte) 0x1a));
		assertEquals("iload_1", Opcodes.name((byte) 0x1b));
		assertEquals("iload_2", Opcodes.name((byte) 0x1c));
		assertEquals("iload_3", Opcodes.name((byte) 0x1d));
		assertEquals("lload_0", Opcodes.name((byte) 0x1e));
		assertEquals("lload_1", Opcodes.name((byte) 0x1f));
		assertEquals("lload_2", Opcodes.name((byte) 0x20));
		assertEquals("lload_3", Opcodes.name((byte) 0x21));
		assertEquals("fload_0", Opcodes.name((byte) 0x22));
		assertEquals("fload_1", Opcodes.name((byte) 0x23));
		assertEquals("fload_2", Opcodes.name((byte) 0x24));
		assertEquals("fload_3", Opcodes.name((byte) 0x25));
		assertEquals("dload_0", Opcodes.name((byte) 0x26));
		assertEquals("dload_1", Opcodes.name((byte) 0x27));
		assertEquals("dload_2", Opcodes.name((byte) 0x28));
		assertEquals("dload_3", Opcodes.name((byte) 0x29));
		assertEquals("aload_0", Opcodes.name((byte) 0x2a));
		assertEquals("aload_1", Opcodes.name((byte) 0x2b));
		assertEquals("aload_2", Opcodes.name((byte) 0x2c));
		assertEquals("aload_3", Opcodes.name((byte) 0x2d));
		assertEquals("iaload", Opcodes.name((byte) 0x2e));
		assertEquals("laload", Opcodes.name((byte) 0x2f));
		assertEquals("faload", Opcodes.name((byte) 0x30));
		assertEquals("daload", Opcodes.name((byte) 0x31));
		assertEquals("aaload", Opcodes.name((byte) 0x32));
		assertEquals("baload", Opcodes.name((byte) 0x33));
		assertEquals("caload", Opcodes.name((byte) 0x34));
		assertEquals("saload", Opcodes.name((byte) 0x35));
		assertEquals("istore", Opcodes.name((byte) 0x36));
		assertEquals("lstore", Opcodes.name((byte) 0x37));
		assertEquals("fstore", Opcodes.name((byte) 0x38));
		assertEquals("dstore", Opcodes.name((byte) 0x39));
		assertEquals("astore", Opcodes.name((byte) 0x3a));
		assertEquals("istore_0", Opcodes.name((byte) 0x3b));
		assertEquals("istore_1", Opcodes.name((byte) 0x3c));
		assertEquals("istore_2", Opcodes.name((byte) 0x3d));
		assertEquals("istore_3", Opcodes.name((byte) 0x3e));
		assertEquals("lstore_0", Opcodes.name((byte) 0x3f));
		assertEquals("lstore_1", Opcodes.name((byte) 0x40));
		assertEquals("lstore_2", Opcodes.name((byte) 0x41));
		assertEquals("lstore_3", Opcodes.name((byte) 0x42));
		assertEquals("fstore_0", Opcodes.name((byte) 0x43));
		assertEquals("fstore_1", Opcodes.name((byte) 0x44));
		assertEquals("fstore_2", Opcodes.name((byte) 0x45));
		assertEquals("fstore_3", Opcodes.name((byte) 0x46));
		assertEquals("dstore_0", Opcodes.name((byte) 0x47));
		assertEquals("dstore_1", Opcodes.name((byte) 0x48));
		assertEquals("dstore_2", Opcodes.name((byte) 0x49));
		assertEquals("dstore_3", Opcodes.name((byte) 0x4a));
		assertEquals("astore_0", Opcodes.name((byte) 0x4b));
		assertEquals("astore_1", Opcodes.name((byte) 0x4c));
		assertEquals("astore_2", Opcodes.name((byte) 0x4d));
		assertEquals("astore_3", Opcodes.name((byte) 0x4e));
		assertEquals("iastore", Opcodes.name((byte) 0x4f));
		assertEquals("lastore", Opcodes.name((byte) 0x50));
		assertEquals("fastore", Opcodes.name((byte) 0x51));
		assertEquals("dastore", Opcodes.name((byte) 0x52));
		assertEquals("aastore", Opcodes.name((byte) 0x53));
		assertEquals("bastore", Opcodes.name((byte) 0x54));
		assertEquals("castore", Opcodes.name((byte) 0x55));
		assertEquals("sastore", Opcodes.name((byte) 0x56));
		assertEquals("pop", Opcodes.name((byte) 0x57));
		assertEquals("pop2", Opcodes.name((byte) 0x58));
		assertEquals("dup", Opcodes.name((byte) 0x59));
		assertEquals("dup_x1", Opcodes.name((byte) 0x5a));
		assertEquals("dup_x2", Opcodes.name((byte) 0x5b));
		assertEquals("dup2", Opcodes.name((byte) 0x5c));
		assertEquals("dup2_x1", Opcodes.name((byte) 0x5d));
		assertEquals("dup2_x2", Opcodes.name((byte) 0x5e));
		assertEquals("swap", Opcodes.name((byte) 0x5f));
		assertEquals("iadd", Opcodes.name((byte) 0x60));
		assertEquals("ladd", Opcodes.name((byte) 0x61));
		assertEquals("fadd", Opcodes.name((byte) 0x62));
		assertEquals("dadd", Opcodes.name((byte) 0x63));
		assertEquals("isub", Opcodes.name((byte) 0x64));
		assertEquals("lsub", Opcodes.name((byte) 0x65));
		assertEquals("fsub", Opcodes.name((byte) 0x66));
		assertEquals("dsub", Opcodes.name((byte) 0x67));
		assertEquals("imul", Opcodes.name((byte) 0x68));
		assertEquals("lmul", Opcodes.name((byte) 0x69));
		assertEquals("fmul", Opcodes.name((byte) 0x6a));
		assertEquals("dmul", Opcodes.name((byte) 0x6b));
		assertEquals("idiv", Opcodes.name((byte) 0x6c));
		assertEquals("ldiv", Opcodes.name((byte) 0x6d));
		assertEquals("fdiv", Opcodes.name((byte) 0x6e));
		assertEquals("ddiv", Opcodes.name((byte) 0x6f));
		assertEquals("irem", Opcodes.name((byte) 0x70));
		assertEquals("lrem", Opcodes.name((byte) 0x71));
		assertEquals("frem", Opcodes.name((byte) 0x72));
		assertEquals("drem", Opcodes.name((byte) 0x73));
		assertEquals("ineg", Opcodes.name((byte) 0x74));
		assertEquals("lneg", Opcodes.name((byte) 0x75));
		assertEquals("fneg", Opcodes.name((byte) 0x76));
		assertEquals("dneg", Opcodes.name((byte) 0x77));
		assertEquals("ishl", Opcodes.name((byte) 0x78));
		assertEquals("lshl", Opcodes.name((byte) 0x79));
		assertEquals("ishr", Opcodes.name((byte) 0x7a));
		assertEquals("lshr", Opcodes.name((byte) 0x7b));
		assertEquals("iushr", Opcodes.name((byte) 0x7c));
		assertEquals("lushr", Opcodes.name((byte) 0x7d));
		assertEquals("iand", Opcodes.name((byte) 0x7e));
		assertEquals("land", Opcodes.name((byte) 0x7f));
		assertEquals("ior", Opcodes.name((byte) 0x80));
		assertEquals("lor", Opcodes.name((byte) 0x81));
		assertEquals("ixor", Opcodes.name((byte) 0x82));
		assertEquals("lxor", Opcodes.name((byte) 0x83));
		assertEquals("iinc", Opcodes.name((byte) 0x84));
		assertEquals("i2l", Opcodes.name((byte) 0x85));
		assertEquals("i2f", Opcodes.name((byte) 0x86));
		assertEquals("i2d", Opcodes.name((byte) 0x87));
		assertEquals("l2i", Opcodes.name((byte) 0x88));
		assertEquals("l2f", Opcodes.name((byte) 0x89));
		assertEquals("l2d", Opcodes.name((byte) 0x8a));
		assertEquals("f2i", Opcodes.name((byte) 0x8b));
		assertEquals("f2l", Opcodes.name((byte) 0x8c));
		assertEquals("f2d", Opcodes.name((byte) 0x8d));
		assertEquals("d2i", Opcodes.name((byte) 0x8e));
		assertEquals("d2l", Opcodes.name((byte) 0x8f));
		assertEquals("d2f", Opcodes.name((byte) 0x90));
		assertEquals("i2b", Opcodes.name((byte) 0x91));
		assertEquals("i2c", Opcodes.name((byte) 0x92));
		assertEquals("i2s", Opcodes.name((byte) 0x93));
		assertEquals("lcmp", Opcodes.name((byte) 0x94));
		assertEquals("fcmpl", Opcodes.name((byte) 0x95));
		assertEquals("fcmpg", Opcodes.name((byte) 0x96));
		assertEquals("dcmpl", Opcodes.name((byte) 0x97));
		assertEquals("dcmpg", Opcodes.name((byte) 0x98));
		assertEquals("ifeq", Opcodes.name((byte) 0x99));
		assertEquals("ifne", Opcodes.name((byte) 0x9a));
		assertEquals("iflt", Opcodes.name((byte) 0x9b));
		assertEquals("ifge", Opcodes.name((byte) 0x9c));
		assertEquals("ifgt", Opcodes.name((byte) 0x9d));
		assertEquals("ifle", Opcodes.name((byte) 0x9e));
		assertEquals("if_icmpeq", Opcodes.name((byte) 0x9f));
		assertEquals("if_icmpne", Opcodes.name((byte) 0xa0));
		assertEquals("if_icmplt", Opcodes.name((byte) 0xa1));
		assertEquals("if_icmpge", Opcodes.name((byte) 0xa2));
		assertEquals("if_icmpgt", Opcodes.name((byte) 0xa3));
		assertEquals("if_icmple", Opcodes.name((byte) 0xa4));
		assertEquals("if_acmpeq", Opcodes.name((byte) 0xa5));
		assertEquals("if_acmpne", Opcodes.name((byte) 0xa6));
		assertEquals("goto", Opcodes.name((byte) 0xa7));
		assertEquals("jsr", Opcodes.name((byte) 0xa8));
		assertEquals("ret", Opcodes.name((byte) 0xa9));
		assertEquals("tableswitch", Opcodes.name((byte) 0xaa));
		assertEquals("lookupswitch", Opcodes.name((byte) 0xab));
		assertEquals("ireturn", Opcodes.name((byte) 0xac));
		assertEquals("lreturn", Opcodes.name((byte) 0xad));
		assertEquals("freturn", Opcodes.name((byte) 0xae));
		assertEquals("dreturn", Opcodes.name((byte) 0xaf));
		assertEquals("areturn", Opcodes.name((byte) 0xb0));
		assertEquals("return", Opcodes.name((byte) 0xb1));
		assertEquals("getstatic", Opcodes.name((byte) 0xb2));
		assertEquals("putstatic", Opcodes.name((byte) 0xb3));
		assertEquals("getfield", Opcodes.name((byte) 0xb4));
		assertEquals("putfield", Opcodes.name((byte) 0xb5));
		assertEquals("invokevirtual", Opcodes.name((byte) 0xb6));
		assertEquals("invokespecial", Opcodes.name((byte) 0xb7));
		assertEquals("invokestatic", Opcodes.name((byte) 0xb8));
		assertEquals("invokeinterface", Opcodes.name((byte) 0xb9));
		assertEquals("invokedynamic", Opcodes.name((byte) 0xba));
		assertEquals("new", Opcodes.name((byte) 0xbb));
		assertEquals("newarray", Opcodes.name((byte) 0xbc));
		assertEquals("anewarray", Opcodes.name((byte) 0xbd));
		assertEquals("arraylength", Opcodes.name((byte) 0xbe));
		assertEquals("athrow", Opcodes.name((byte) 0xbf));
		assertEquals("checkcast", Opcodes.name((byte) 0xc0));
		assertEquals("instanceof", Opcodes.name((byte) 0xc1));
		assertEquals("monitorenter", Opcodes.name((byte) 0xc2));
		assertEquals("monitorexit", Opcodes.name((byte) 0xc3));
		assertEquals("wide", Opcodes.name((byte) 0xc4));
		assertEquals("multianewarray", Opcodes.name((byte) 0xc5));
		assertEquals("ifnull", Opcodes.name((byte) 0xc6));
		assertEquals("ifnonnull", Opcodes.name((byte) 0xc7));
		assertEquals("goto_w", Opcodes.name((byte) 0xc8));
		assertEquals("jsr_w", Opcodes.name((byte) 0xc9));
		assertEquals("breakpoint", Opcodes.name((byte) 0xca));
		assertEquals("impdep1", Opcodes.name((byte) 0xfe));
		assertEquals("impdep2", Opcodes.name((byte) 0xff));
		for (byte b = (byte) 0xcb; b < (byte) 0xfe; b++) {
			assertNull(Opcodes.name(b));
		}
	}
	@Test
	void testOpcodeOperands() {
		for (int i = 0; i < 256; i++) {
			final byte opcode = (byte) (i & 0xff);
			int operands;
			switch (opcode) {
			case ALOAD:
			case ASTORE:
			case BIPUSH:
			case DLOAD:
			case DSTORE:
			case FLOAD:
			case FSTORE:
			case ILOAD:
			case ISTORE:
			case LDC:
			case LLOAD:
			case LSTORE:
			case NEWARRAY:
			case RET:
				operands = 1;
				break;
			case ANEWARRAY:
			case CHECKCAST:
			case GETFIELD:
			case GETSTATIC:
			case GOTO:
			case IF_ACMPEQ:
			case IF_ACMPNE:
			case IF_ICMPEQ:
			case IF_ICMPGE:
			case IF_ICMPGT:
			case IF_ICMPLE:
			case IF_ICMPLT:
			case IF_ICMPNE:
			case IFEQ:
			case IFGE:
			case IFGT:
			case IFLE:
			case IFLT:
			case IFNONNULL:
			case IFNULL:
			case IINC:
			case INSTANCEOF:
			case INVOKESPECIAL:
			case INVOKESTATIC:
			case INVOKEVIRTUAL:
			case JSR:
			case LDC_W:
			case LDC2_W:
			case NEW:
			case PUTFIELD:
			case PUTSTATIC:
			case SIPUSH:
				operands = 2;
				break;
			case MULTIANEWARRAY:
				operands = 3;
				break;
			case GOTO_W:
			case INVOKEDYNAMIC:
			case INVOKEINTERFACE:
			case JSR_W:
				operands = 4;
				break;
			case WIDE:
			case TABLESWITCH:
			case LOOKUPSWITCH:
				operands = -1;
			default:
				operands = 0;
			}
			assertEquals(operands, Opcodes.operands(opcode), "Opcode " + opcode);
		}
	}
}
