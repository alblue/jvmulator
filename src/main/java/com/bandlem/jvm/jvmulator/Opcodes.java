/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
public interface Opcodes {
	public static final byte AALOAD = (byte) 50;
	public static final byte AASTORE = (byte) 83;
	public static final byte ACONST_NULL = (byte) 1;
	public static final byte ALOAD = (byte) 25;
	public static final byte ALOAD_0 = (byte) 42;
	public static final byte ALOAD_1 = (byte) 43;
	public static final byte ALOAD_2 = (byte) 44;
	public static final byte ALOAD_3 = (byte) 45;
	public static final byte ANEWARRAY = (byte) 189;
	public static final byte ARETURN = (byte) 176;
	public static final byte ARRAYLENGTH = (byte) 190;
	public static final byte ASTORE = (byte) 58;
	public static final byte ASTORE_0 = (byte) 75;
	public static final byte ASTORE_1 = (byte) 76;
	public static final byte ASTORE_2 = (byte) 77;
	public static final byte ASTORE_3 = (byte) 78;
	public static final byte ATHROW = (byte) 191;
	public static final byte BALOAD = (byte) 51;
	public static final byte BASTORE = (byte) 84;
	public static final byte BIPUSH = (byte) 16;
	public static final byte BREAKPOINT = (byte) 202;
	public static final byte CALOAD = (byte) 52;
	public static final byte CASTORE = (byte) 85;
	public static final byte CHECKCAST = (byte) 192;
	public static final byte D2F = (byte) 144;
	public static final byte D2I = (byte) 142;
	public static final byte D2L = (byte) 143;
	public static final byte DADD = (byte) 99;
	public static final byte DALOAD = (byte) 49;
	public static final byte DASTORE = (byte) 82;
	public static final byte DCMPG = (byte) 152;
	public static final byte DCMPL = (byte) 151;
	public static final byte DCONST_0 = (byte) 14;
	public static final byte DCONST_1 = (byte) 15;
	public static final byte DDIV = (byte) 111;
	public static final byte DLOAD = (byte) 24;
	public static final byte DLOAD_0 = (byte) 38;
	public static final byte DLOAD_1 = (byte) 39;
	public static final byte DLOAD_2 = (byte) 40;
	public static final byte DLOAD_3 = (byte) 41;
	public static final byte DMUL = (byte) 107;
	public static final byte DNEG = (byte) 119;
	public static final byte DREM = (byte) 115;
	public static final byte DRETURN = (byte) 175;
	public static final byte DSTORE = (byte) 57;
	public static final byte DSTORE_0 = (byte) 71;
	public static final byte DSTORE_1 = (byte) 72;
	public static final byte DSTORE_2 = (byte) 73;
	public static final byte DSTORE_3 = (byte) 74;
	public static final byte DSUB = (byte) 103;
	public static final byte DUP = (byte) 89;
	public static final byte DUP_X1 = (byte) 90;
	public static final byte DUP_X2 = (byte) 91;
	public static final byte DUP2 = (byte) 92;
	public static final byte DUP2_X1 = (byte) 93;
	public static final byte DUP2_X2 = (byte) 94;
	public static final byte F2D = (byte) 141;
	public static final byte F2I = (byte) 139;
	public static final byte F2L = (byte) 140;
	public static final byte FADD = (byte) 98;
	public static final byte FALOAD = (byte) 48;
	public static final byte FASTORE = (byte) 81;
	public static final byte FCMPG = (byte) 150;
	public static final byte FCMPL = (byte) 149;
	public static final byte FCONST_0 = (byte) 11;
	public static final byte FCONST_1 = (byte) 12;
	public static final byte FCONST_2 = (byte) 13;
	public static final byte FDIV = (byte) 110;
	public static final byte FLOAD = (byte) 23;
	public static final byte FLOAD_0 = (byte) 34;
	public static final byte FLOAD_1 = (byte) 35;
	public static final byte FLOAD_2 = (byte) 36;
	public static final byte FLOAD_3 = (byte) 37;
	public static final byte FMUL = (byte) 106;
	public static final byte FNEG = (byte) 118;
	public static final byte FREM = (byte) 114;
	public static final byte FRETURN = (byte) 174;
	public static final byte FSTORE = (byte) 56;
	public static final byte FSTORE_0 = (byte) 67;
	public static final byte FSTORE_1 = (byte) 68;
	public static final byte FSTORE_2 = (byte) 69;
	public static final byte FSTORE_3 = (byte) 70;
	public static final byte FSUB = (byte) 102;
	public static final byte GETFIELD = (byte) 180;
	public static final byte GETSTATIC = (byte) 178;
	public static final byte GOTO = (byte) 167;
	public static final byte GOTO_W = (byte) 200;
	public static final byte I2B = (byte) 145;
	public static final byte I2C = (byte) 146;
	public static final byte I2D = (byte) 135;
	public static final byte I2F = (byte) 134;
	public static final byte I2L = (byte) 133;
	public static final byte I2S = (byte) 147;
	public static final byte IADD = (byte) 96;
	public static final byte IALOAD = (byte) 46;
	public static final byte IAND = (byte) 126;
	public static final byte IASTORE = (byte) 79;
	public static final byte ICONST_0 = (byte) 3;
	public static final byte ICONST_1 = (byte) 4;
	public static final byte ICONST_2 = (byte) 5;
	public static final byte ICONST_3 = (byte) 6;
	public static final byte ICONST_4 = (byte) 7;
	public static final byte ICONST_5 = (byte) 8;
	public static final byte ICONST_M1 = (byte) 2;
	public static final byte IDIV = (byte) 108;
	public static final byte IF_ACMPEQ = (byte) 165;
	public static final byte IF_ACMPNE = (byte) 166;
	public static final byte IF_ICMPEQ = (byte) 159;
	public static final byte IF_ICMPGE = (byte) 162;
	public static final byte IF_ICMPGT = (byte) 163;
	public static final byte IF_ICMPLE = (byte) 164;
	public static final byte IF_ICMPLT = (byte) 161;
	public static final byte IF_ICMPNE = (byte) 160;
	public static final byte IFEQ = (byte) 153;
	public static final byte IFGE = (byte) 156;
	public static final byte IFGT = (byte) 157;
	public static final byte IFLE = (byte) 158;
	public static final byte IFLT = (byte) 155;
	public static final byte IFNE = (byte) 154;
	public static final byte IFNONNULL = (byte) 199;
	public static final byte IFNULL = (byte) 198;
	public static final byte IINC = (byte) 132;
	public static final byte ILOAD = (byte) 21;
	public static final byte ILOAD_0 = (byte) 26;
	public static final byte ILOAD_1 = (byte) 27;
	public static final byte ILOAD_2 = (byte) 28;
	public static final byte ILOAD_3 = (byte) 29;
	public static final byte IMPDEP1 = (byte) 254;
	public static final byte IMPDEP2 = (byte) 255;
	public static final byte IMUL = (byte) 104;
	public static final byte INEG = (byte) 116;
	public static final byte INSTANCEOF = (byte) 193;
	public static final byte INVOKEDYNAMIC = (byte) 186;
	public static final byte INVOKEINTERFACE = (byte) 185;
	public static final byte INVOKESPECIAL = (byte) 183;
	public static final byte INVOKESTATIC = (byte) 184;
	public static final byte INVOKEVIRTUAL = (byte) 182;
	public static final byte IOR = (byte) 128;
	public static final byte IREM = (byte) 112;
	public static final byte IRETURN = (byte) 172;
	public static final byte ISHL = (byte) 120;
	public static final byte ISHR = (byte) 122;
	public static final byte ISTORE = (byte) 54;
	public static final byte ISTORE_0 = (byte) 59;
	public static final byte ISTORE_1 = (byte) 60;
	public static final byte ISTORE_2 = (byte) 61;
	public static final byte ISTORE_3 = (byte) 62;
	public static final byte ISUB = (byte) 100;
	public static final byte IUSHR = (byte) 124;
	public static final byte IXOR = (byte) 130;
	public static final byte JSR = (byte) 168;
	public static final byte JSR_W = (byte) 201;
	public static final byte L2D = (byte) 138;
	public static final byte L2F = (byte) 137;
	public static final byte L2I = (byte) 136;
	public static final byte LADD = (byte) 97;
	public static final byte LALOAD = (byte) 47;
	public static final byte LAND = (byte) 127;
	public static final byte LASTORE = (byte) 80;
	public static final byte LCMP = (byte) 148;
	public static final byte LCONST_0 = (byte) 9;
	public static final byte LCONST_1 = (byte) 10;
	public static final byte LDC = (byte) 18;
	public static final byte LDC_W = (byte) 19;
	public static final byte LDC2_W = (byte) 20;
	public static final byte LDIV = (byte) 109;
	public static final byte LLOAD = (byte) 22;
	public static final byte LLOAD_0 = (byte) 30;
	public static final byte LLOAD_1 = (byte) 31;
	public static final byte LLOAD_2 = (byte) 32;
	public static final byte LLOAD_3 = (byte) 33;
	public static final byte LMUL = (byte) 105;
	public static final byte LNEG = (byte) 117;
	public static final byte LOOKUPSWITCH = (byte) 171;
	public static final byte LOR = (byte) 129;
	public static final byte LREM = (byte) 113;
	public static final byte LRETURN = (byte) 173;
	public static final byte LSHL = (byte) 121;
	public static final byte LSHR = (byte) 123;
	public static final byte LSTORE = (byte) 55;
	public static final byte LSTORE_0 = (byte) 63;
	public static final byte LSTORE_1 = (byte) 64;
	public static final byte LSTORE_2 = (byte) 65;
	public static final byte LSTORE_3 = (byte) 66;
	public static final byte LSUB = (byte) 101;
	public static final byte LUSHR = (byte) 125;
	public static final byte LXOR = (byte) 131;
	public static final byte MONITORENTER = (byte) 194;
	public static final byte MONITOREXIT = (byte) 195;
	public static final byte MULTIANEWARRAY = (byte) 197;
	public static final String[] name = new String[] {
			"nop", // 0
			"aconst_null", // 1
			"iconst_m1", // 2
			"iconst_0", // 3
			"iconst_1", // 4
			"iconst_2", // 5
			"iconst_3", // 6
			"iconst_4", // 7
			"iconst_5", // 8
			"lconst_0", // 9
			"lconst_1", // 10
			"fconst_0", // 11
			"fconst_1", // 12
			"fconst_2", // 13
			"dconst_0", // 14
			"dconst_1", // 15
			"bipush", // 16
			"sipush", // 17
			"ldc", // 18
			"ldc_w", // 19
			"ldc2_w", // 20
			"iload", // 21
			"lload", // 22
			"fload", // 23
			"dload", // 24
			"aload", // 25
			"iload_0", // 26
			"iload_1", // 27
			"iload_2", // 28
			"iload_3", // 29
			"lload_0", // 30
			"lload_1", // 31
			"lload_2", // 32
			"lload_3", // 33
			"fload_0", // 34
			"fload_1", // 35
			"fload_2", // 36
			"fload_3", // 37
			"dload_0", // 38
			"dload_1", // 39
			"dload_2", // 40
			"dload_3", // 41
			"aload_0", // 42
			"aload_1", // 43
			"aload_2", // 44
			"aload_3", // 45
			"iaload", // 46
			"laload", // 47
			"faload", // 48
			"daload", // 49
			"aaload", // 50
			"baload", // 51
			"caload", // 52
			"saload", // 53
			"istore", // 54
			"lstore", // 55
			"fstore", // 56
			"dstore", // 57
			"astore", // 58
			"istore_0", // 59
			"istore_1", // 60
			"istore_2", // 61
			"istore_3", // 62
			"lstore_0", // 63
			"lstore_1", // 64
			"lstore_2", // 65
			"lstore_3", // 66
			"fstore_0", // 67
			"fstore_1", // 68
			"fstore_2", // 69
			"fstore_3", // 70
			"dstore_0", // 71
			"dstore_1", // 72
			"dstore_2", // 73
			"dstore_3", // 74
			"astore_0", // 75
			"astore_1", // 76
			"astore_2", // 77
			"astore_3", // 78
			"iastore", // 79
			"lastore", // 80
			"fastore", // 81
			"dastore", // 82
			"aastore", // 83
			"bastore", // 84
			"castore", // 85
			"sastore", // 86
			"pop", // 87
			"pop2", // 88
			"dup", // 89
			"dup_x1", // 90
			"dup_x2", // 91
			"dup2", // 92
			"dup2_x1", // 93
			"dup2_x2", // 94
			"swap", // 95
			"iadd", // 96
			"ladd", // 97
			"fadd", // 98
			"dadd", // 99
			"isub", // 100
			"lsub", // 101
			"fsub", // 102
			"dsub", // 103
			"imul", // 104
			"lmul", // 105
			"fmul", // 106
			"dmul", // 107
			"idiv", // 108
			"ldiv", // 109
			"fdiv", // 110
			"ddiv", // 111
			"irem", // 112
			"lrem", // 113
			"frem", // 114
			"drem", // 115
			"ineg", // 116
			"lneg", // 117
			"fneg", // 118
			"dneg", // 119
			"ishl", // 120
			"lshl", // 121
			"ishr", // 122
			"lshr", // 123
			"iushr", // 124
			"lushr", // 125
			"iand", // 126
			"land", // 127
			"ior", // 128
			"lor", // 129
			"ixor", // 130
			"lxor", // 131
			"iinc", // 132
			"i2l", // 133
			"i2f", // 134
			"i2d", // 135
			"l2i", // 136
			"l2f", // 137
			"l2d", // 138
			"f2i", // 139
			"f2l", // 140
			"f2d", // 141
			"d2i", // 142
			"d2l", // 143
			"d2f", // 144
			"i2b", // 145
			"i2c", // 146
			"i2s", // 147
			"lcmp", // 148
			"fcmpl", // 149
			"fcmpg", // 150
			"dcmpl", // 151
			"dcmpg", // 152
			"ifeq", // 153
			"ifne", // 154
			"iflt", // 155
			"ifge", // 156
			"ifgt", // 157
			"ifle", // 158
			"if_icmpeq", // 159
			"if_icmpne", // 160
			"if_icmplt", // 161
			"if_icmpge", // 162
			"if_icmpgt", // 163
			"if_icmple", // 164
			"if_acmpeq", // 165
			"if_acmpne", // 166
			"goto", // 167
			"jsr", // 168
			"ret", // 169
			"tableswitch", // 170
			"lookupswitch", // 171
			"ireturn", // 172
			"lreturn", // 173
			"freturn", // 174
			"dreturn", // 175
			"areturn", // 176
			"return", // 177
			"getstatic", // 178
			"putstatic", // 179
			"getfield", // 180
			"putfield", // 181
			"invokevirtual", // 182
			"invokespecial", // 183
			"invokestatic", // 184
			"invokeinterface", // 185
			"invokedynamic", // 186
			"new", // 187
			"newarray", // 188
			"anewarray", // 189
			"arraylength", // 190
			"athrow", // 191
			"checkcast", // 192
			"instanceof", // 193
			"monitorenter", // 194
			"monitorexit", // 195
			"wide", // 196
			"multianewarray", // 197
			"ifnull", // 198
			"ifnonnull", // 199
			"goto_w", // 200
			"jsr_w", // 201
			"breakpoint", // 202
			null, // 203
			null, // 204
			null, // 205
			null, // 206
			null, // 207
			null, // 208
			null, // 209
			null, // 210
			null, // 211
			null, // 212
			null, // 213
			null, // 214
			null, // 215
			null, // 216
			null, // 217
			null, // 218
			null, // 219
			null, // 220
			null, // 221
			null, // 222
			null, // 223
			null, // 224
			null, // 225
			null, // 226
			null, // 227
			null, // 228
			null, // 229
			null, // 230
			null, // 231
			null, // 232
			null, // 233
			null, // 234
			null, // 235
			null, // 236
			null, // 237
			null, // 238
			null, // 239
			null, // 240
			null, // 241
			null, // 242
			null, // 243
			null, // 244
			null, // 245
			null, // 246
			null, // 247
			null, // 248
			null, // 249
			null, // 250
			null, // 251
			null, // 252
			null, // 253
			"impdep1", // 254
			"impdep2", // 255
	};
	public static final byte NEW = (byte) 187;
	public static final byte NEWARRAY = (byte) 188;
	public static final byte NOP = (byte) 0;
	public static final byte POP = (byte) 87;
	public static final byte POP2 = (byte) 88;
	public static final byte PUTFIELD = (byte) 181;
	public static final byte PUTSTATIC = (byte) 179;
	public static final byte RET = (byte) 169;
	public static final byte RETURN = (byte) 177;
	public static final byte SALOAD = (byte) 53;
	public static final byte SASTORE = (byte) 86;
	public static final byte SIPUSH = (byte) 17;
	public static final byte SWAP = (byte) 95;
	public static final byte TABLESWITCH = (byte) 170;
	public static final byte WIDE = (byte) 196;
	public static String name(final byte i) {
		return name[i & 0xff];
	}
	static int operands(final byte opcode) {
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
		return operands;
	}
}
