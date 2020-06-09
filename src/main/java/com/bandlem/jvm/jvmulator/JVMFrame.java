/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.DoubleConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.FieldRef;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.FloatConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.IntConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.Item;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.LongConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.MethodRef;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.NameAndType;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.StringConstant;
import com.bandlem.jvm.jvmulator.classfile.JavaClass;
import com.bandlem.jvm.jvmulator.classfile.Member;
public class JVMFrame {
	static Slot getfield(final Object target, final String fieldName, final String descriptor, final String className,
			final ClassLoader classLoader) {
		try {
			final Class<?> clazz = Class.forName(className.replace('/', '.'));
			final Field field = clazz.getField(fieldName);
			final Object result = field.get(target);
			switch (descriptor.charAt(descriptor.length() - 1)) {
			case 'Z':
			case 'B':
			case 'S':
			case 'C':
			case 'I':
				return Slot.of((int) result);
			case 'J':
				return Slot.of((long) result);
			case 'F':
				return Slot.of((float) result);
			case 'D':
				return Slot.of((double) result);
			default:
				return Slot.of(result);
			}
		} catch (final Exception e) {
			throw new UnsupportedOperationException("Cannot access field " + className + ":" + fieldName, e);
		}
	}
	static boolean instanceOf(final Object target, final String className) {
		try {
			final Class<?> clazz = Class.forName(className.replace('/', '.'));
			return clazz.isAssignableFrom(target.getClass());
		} catch (final Exception e) {
			throw new UnsupportedOperationException("Cannot instanceof " + className + " on " + target, e);
		}
	}
	static void putfield(final Slot value, final Object target, final String fieldName, final String descriptor,
			final String className, final ClassLoader classLoader) {
		try {
			final Class<?> clazz = Class.forName(className.replace('/', '.'));
			final Field field = clazz.getField(fieldName);
			switch (descriptor.charAt(descriptor.length() - 1)) {
			case 'Z':
				field.setBoolean(target, value.booleanValue());
				return;
			case 'B':
				field.setByte(target, (byte) value.intValue());
				return;
			case 'S':
				field.setShort(target, (short) value.intValue());
				return;
			case 'C':
				field.setChar(target, (char) value.intValue());
				return;
			case 'I':
				field.setInt(target, value.intValue());
				return;
			case 'J':
				field.setLong(target, value.longValue());
				return;
			case 'F':
				field.setFloat(target, value.floatValue());
				return;
			case 'D':
				field.setDouble(target, value.doubleValue());
				return;
			default:
				field.set(target, value.referenceValue());
				return;
			}
		} catch (final Exception e) {
			throw new UnsupportedOperationException("Cannot access field " + className + ":" + fieldName, e);
		}
	}
	private final byte[] bytecode;
	// private final JavaClass javaClass;
	private final Slot[] locals;
	private int pc;
	private final ConstantPool pool;
	private Slot returnValue;
	final Stack stack = new Stack();
	public JVMFrame(final JavaClass javaClass, final int locals, final byte[] code) {
		this.bytecode = code;
		this.locals = new Slot[locals];
		// this.javaClass = javaClass;
		this.pool = javaClass == null ? null : javaClass.pool;
	}
	private void getfield(final Object target, final int index) {
		final FieldRef fieldRef = (FieldRef) pool.getItem(index);
		final NameAndType nat = (NameAndType) pool.getItem(fieldRef.nameAndTypeIndex);
		final String fieldName = pool.getString(nat.nameIndex);
		final String descriptor = pool.getString(nat.descriptorIndex);
		final String className = pool.getClassName(fieldRef.classIndex);
		final Slot result = getfield(target, fieldName, descriptor, className, JVMFrame.class.getClassLoader());
		stack.pushSlot(result);
	}
	public Slot[] getLocals() {
		return locals;
	}
	public int getPC() {
		return pc;
	}
	public Slot getReturnValue() {
		return returnValue;
	}
	public Stack getStack() {
		return stack;
	}
	private void invoke(final Object target, final int index) {
		final MethodRef methodRef = (MethodRef) pool.getItem(index);
		final NameAndType nat = (NameAndType) pool.getItem(methodRef.nameAndTypeIndex);
		final String methodName = pool.getString(nat.nameIndex);
		final String descriptor = pool.getString(nat.descriptorIndex);
		final String className = pool.getClassName(methodRef.classIndex);
		final Slot result = invoke(target, methodName, descriptor, className, JVMFrame.class.getClassLoader());
		if (result != null) {
			stack.pushSlot(result);
		}
	}
	Slot invoke(final Object target, final String methodName, final String descriptor, final String className,
			final ClassLoader classLoader) {
		try {
			final Class<?> clazz = Class.forName(className.replace('/', '.'));
			final Class<?> types[] = Member.Method.argumentTypes(descriptor, classLoader);
			final Method method = clazz.getMethod(methodName, types);
			final Object args[] = new Object[types.length];
			for (int i = args.length - 1; i >= 0; i--) {
				args[i] = stack.pop().toObject();
			}
			final Object result = method.invoke(target, args);
			final char last = descriptor.charAt(descriptor.length() - 1);
			switch (last) {
			case 'V':
				// no push
				return null;
			case 'I':
			case 'S':
			case 'C':
			case 'Z':
				return Slot.of((int) result);
			case 'J':
				return Slot.of((long) result);
			case 'F':
				return Slot.of((float) result);
			case 'D':
				return Slot.of((double) result);
			default:
				return Slot.of(result);
			}
		} catch (final Exception e) {
			throw new UnsupportedOperationException("Cannot execute method " + className + ":" + methodName, e);
		}
	}
	private Slot notWide(final Slot slot, final byte opcode) {
		if (slot.isWide()) {
			throw new IllegalStateException("Cannot use wide slot for opcode " + opcode);
		}
		return slot;
	}
	private void pushConstant(final int constant) {
		final Item item = pool.getItem(constant);
		if (item instanceof StringConstant) {
			final short index = ((StringConstant) item).index;
			stack.push(pool.getString(index));
		} else if (item instanceof IntConstant) {
			stack.push(((IntConstant) item).value);
		} else if (item instanceof LongConstant) {
			stack.push(((LongConstant) item).value);
		} else if (item instanceof FloatConstant) {
			stack.push(((FloatConstant) item).value);
		} else if (item instanceof DoubleConstant) {
			stack.push(((DoubleConstant) item).value);
		} else {
			throw new UnsupportedOperationException("Unknown item type " + item.type);
		}
	}
	private void putfield(final Slot value, final Object target, final int index) {
		final FieldRef fieldRef = (FieldRef) pool.getItem(index);
		final NameAndType nat = (NameAndType) pool.getItem(fieldRef.nameAndTypeIndex);
		final String fieldName = pool.getString(nat.nameIndex);
		final String descriptor = pool.getString(nat.descriptorIndex);
		final String className = pool.getClassName(fieldRef.classIndex);
		putfield(value, target, fieldName, descriptor, className, JVMFrame.class.getClassLoader());
	}
	public Slot run() {
		returnValue = null;
		while (step())
			;
		if (stack.size() != 0) {
			throw new IllegalStateException("Stack should be empty at return");
		}
		return returnValue;
	}
	public boolean step() {
		final byte opcode = bytecode[pc++];
		switch (opcode) {
		case Opcodes.NOP:
			return true;
		// Constants
		case Opcodes.ACONST_NULL:
			stack.push(null);
			return true;
		case Opcodes.ICONST_0:
			stack.push(0);
			return true;
		case Opcodes.ICONST_1:
			stack.push(1);
			return true;
		case Opcodes.ICONST_2:
			stack.push(2);
			return true;
		case Opcodes.ICONST_3:
			stack.push(3);
			return true;
		case Opcodes.ICONST_4:
			stack.push(4);
			return true;
		case Opcodes.ICONST_5:
			stack.push(5);
			return true;
		case Opcodes.ICONST_M1:
			stack.push(-1);
			return true;
		case Opcodes.FCONST_0:
			stack.push(0F);
			return true;
		case Opcodes.FCONST_1:
			stack.push(1F);
			return true;
		case Opcodes.FCONST_2:
			stack.push(2F);
			return true;
		case Opcodes.LCONST_0:
			stack.push(0L);
			return true;
		case Opcodes.LCONST_1:
			stack.push(1L);
			return true;
		case Opcodes.DCONST_0:
			stack.push(0D);
			return true;
		case Opcodes.DCONST_1:
			stack.push(1D);
			return true;
		case Opcodes.SIPUSH:
			stack.push(bytecode[pc++] << 8 | bytecode[pc++]);
			return true;
		case Opcodes.BIPUSH:
			stack.push(bytecode[pc++]);
			return true;
		// Addition
		case Opcodes.IADD:
			stack.push(stack.popInt() + stack.popInt());
			return true;
		case Opcodes.FADD:
			stack.push(stack.popFloat() + stack.popFloat());
			return true;
		case Opcodes.LADD:
			stack.push(stack.popLong() + stack.popLong());
			return true;
		case Opcodes.DADD:
			stack.push(stack.popDouble() + stack.popDouble());
			return true;
		// Multiplication
		case Opcodes.IMUL:
			stack.push(stack.popInt() * stack.popInt());
			return true;
		case Opcodes.FMUL:
			stack.push(stack.popFloat() * stack.popFloat());
			return true;
		case Opcodes.LMUL:
			stack.push(stack.popLong() * stack.popLong());
			return true;
		case Opcodes.DMUL:
			stack.push(stack.popDouble() * stack.popDouble());
			return true;
		// Division
		case Opcodes.IDIV:
			stack.push(stack.popInt() / stack.popInt());
			return true;
		case Opcodes.FDIV:
			stack.push(stack.popFloat() / stack.popFloat());
			return true;
		case Opcodes.LDIV:
			stack.push(stack.popLong() / stack.popLong());
			return true;
		case Opcodes.DDIV:
			stack.push(stack.popDouble() / stack.popDouble());
			return true;
		// Remainder
		case Opcodes.IREM:
			stack.push(stack.popInt() % stack.popInt());
			return true;
		case Opcodes.FREM:
			stack.push(stack.popFloat() % stack.popFloat());
			return true;
		case Opcodes.LREM:
			stack.push(stack.popLong() % stack.popLong());
			return true;
		case Opcodes.DREM:
			stack.push(stack.popDouble() % stack.popDouble());
			return true;
		// Subtraction
		case Opcodes.ISUB:
			stack.push(stack.popInt() - stack.popInt());
			return true;
		case Opcodes.FSUB:
			stack.push(stack.popFloat() - stack.popFloat());
			return true;
		case Opcodes.LSUB:
			stack.push(stack.popLong() - stack.popLong());
			return true;
		case Opcodes.DSUB:
			stack.push(stack.popDouble() - stack.popDouble());
			return true;
		// Negation
		case Opcodes.INEG:
			stack.push(0 - stack.popInt());
			return true;
		case Opcodes.FNEG:
			stack.push(0.0F - stack.popFloat());
			return true;
		case Opcodes.LNEG:
			stack.push(0L - stack.popLong());
			return true;
		case Opcodes.DNEG:
			stack.push(0.0D - stack.popDouble());
			return true;
		// Stack manipulation
		case Opcodes.SWAP: {
			final Slot first = notWide(stack.pop(), opcode);
			final Slot second = notWide(stack.pop(), opcode);
			stack.pushSlot(first);
			stack.pushSlot(second);
			return true;
		}
		case Opcodes.DUP:
			stack.dup();
			return true;
		case Opcodes.DUP_X1:
			stack.dup_x1();
			return true;
		case Opcodes.DUP_X2:
			stack.dup_x2();
			return true;
		case Opcodes.DUP2:
			stack.dup2();
			return true;
		case Opcodes.DUP2_X1:
			stack.dup2_x1();
			return true;
		case Opcodes.DUP2_X2:
			stack.dup2_x2();
			return true;
		case Opcodes.POP:
			notWide(stack.pop(), opcode);
			return true;
		case Opcodes.POP2:
			if (!stack.pop().isWide()) {
				notWide(stack.pop(), opcode);
			}
			return true;
		// Bitwise and shift operations
		case Opcodes.ISHL: {
			final int shift = stack.popInt();
			stack.push(stack.popInt() << shift);
			return true;
		}
		case Opcodes.LSHL: {
			final int shift = stack.popInt();
			stack.push(stack.popLong() << shift);
			return true;
		}
		case Opcodes.ISHR: {
			final int shift = stack.popInt();
			stack.push(stack.popInt() >> shift);
			return true;
		}
		case Opcodes.LSHR: {
			final int shift = stack.popInt();
			stack.push(stack.popLong() >> shift);
			return true;
		}
		case Opcodes.IUSHR: {
			final int shift = stack.popInt();
			stack.push(stack.popInt() >>> shift);
			return true;
		}
		case Opcodes.LUSHR: {
			final int shift = stack.popInt();
			stack.push(stack.popLong() >>> shift);
			return true;
		}
		case Opcodes.IAND:
			stack.push(stack.popInt() & stack.popInt());
			return true;
		case Opcodes.LAND:
			stack.push(stack.popLong() & stack.popLong());
			return true;
		case Opcodes.IOR:
			stack.push(stack.popInt() | stack.popInt());
			return true;
		case Opcodes.LOR:
			stack.push(stack.popLong() | stack.popLong());
			return true;
		case Opcodes.IXOR:
			stack.push(stack.popInt() ^ stack.popInt());
			return true;
		case Opcodes.LXOR:
			stack.push(stack.popLong() ^ stack.popLong());
			return true;
		// Conversions
		case Opcodes.I2B:
			stack.push((byte) stack.popInt());
			return true;
		case Opcodes.I2C:
			stack.push((char) stack.popInt());
			return true;
		case Opcodes.I2S:
			stack.push((short) stack.popInt());
			return true;
		case Opcodes.I2L:
			stack.push((long) stack.popInt());
			return true;
		case Opcodes.I2F:
			stack.push((float) stack.popInt());
			return true;
		case Opcodes.I2D:
			stack.push((double) stack.popInt());
			return true;
		case Opcodes.F2I:
			stack.push((int) stack.popFloat());
			return true;
		case Opcodes.F2L:
			stack.push((long) stack.popFloat());
			return true;
		case Opcodes.F2D:
			stack.push((double) stack.popFloat());
			return true;
		case Opcodes.D2I:
			stack.push((int) stack.popDouble());
			return true;
		case Opcodes.D2L:
			stack.push((long) stack.popDouble());
			return true;
		case Opcodes.D2F:
			stack.push((float) stack.popDouble());
			return true;
		case Opcodes.L2I:
			stack.push((int) stack.popLong());
			return true;
		case Opcodes.L2F:
			stack.push((float) stack.popLong());
			return true;
		case Opcodes.L2D:
			stack.push((double) stack.popLong());
			return true;
		// Comparisons
		case Opcodes.LCMP: {
			final long l1 = stack.popLong();
			final long l2 = stack.popLong();
			stack.push((int) l1 == l2 ? 0 : l1 > l2 ? 1 : -1);
			return true;
		}
		case Opcodes.FCMPL: {
			final float f1 = stack.popFloat();
			final float f2 = stack.popFloat();
			stack.push((int) f1 == f2 ? 0 : f1 > f2 ? 1 : -1);
			return true;
		}
		case Opcodes.FCMPG: {
			final float f1 = stack.popFloat();
			final float f2 = stack.popFloat();
			stack.push((int) f1 == f2 ? 0 : f2 > f1 ? -1 : 1);
			return true;
		}
		case Opcodes.DCMPL: {
			final double f1 = stack.popDouble();
			final double f2 = stack.popDouble();
			stack.push((int) f1 == f2 ? 0 : f1 > f2 ? 1 : -1);
			return true;
		}
		case Opcodes.DCMPG: {
			final double f1 = stack.popDouble();
			final double f2 = stack.popDouble();
			stack.push((int) f1 == f2 ? 0 : f2 > f1 ? -1 : 1);
			return true;
		}
		// Branching
		case Opcodes.RET: {
			final int local = bytecode[pc++] & 0xff;
			pc = (int) (locals[local].referenceValue());
			return true;
		}
		case Opcodes.JSR_W:
			stack.push((Object) (pc + 4));
			pc += bytecode[pc++] << 24 | bytecode[pc++] << 16 | bytecode[pc++] << 8 | bytecode[pc++] - 1;
			return true;
		case Opcodes.JSR:
			stack.push((Object) (pc + 2));
			pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			return true;
		case Opcodes.GOTO_W: {
			pc += bytecode[pc++] << 24 | bytecode[pc++] << 16 | bytecode[pc++] << 8 | bytecode[pc++] - 1;
			return true;
		}
		case Opcodes.GOTO: {
			pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			return true;
		}
		case Opcodes.IFEQ: {
			if (stack.popInt() == 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IFNE: {
			if (stack.popInt() != 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IFLT: {
			if (stack.popInt() < 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IFGE: {
			if (stack.popInt() >= 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IFGT: {
			if (stack.popInt() > 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IFLE: {
			if (stack.popInt() <= 0) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IF_ICMPEQ: {
			if (stack.popInt() == stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IF_ICMPNE: {
			if (stack.popInt() != stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IF_ICMPLT: {
			if (stack.popInt() < stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IF_ICMPGE: {
			if (stack.popInt() >= stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IF_ICMPGT: {
			if (stack.popInt() > stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IF_ICMPLE: {
			if (stack.popInt() <= stack.popInt()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IF_ACMPEQ: {
			if (stack.popReference() == stack.popReference()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IF_ACMPNE: {
			if (stack.popReference() != stack.popReference()) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IFNULL: {
			if (stack.popReference() == null) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		case Opcodes.IFNONNULL: {
			if (stack.popReference() != null) {
				pc += bytecode[pc++] << 8 | bytecode[pc++] - 1;
			} else {
				pc += 2;
			}
			return true;
		}
		// Returns
		case Opcodes.DRETURN:
			returnValue = stack.pop();
			returnValue.doubleValue(); // check return type
			return false;
		case Opcodes.LRETURN:
			returnValue = stack.pop();
			returnValue.longValue(); // check return type
			return false;
		case Opcodes.ARETURN:
			returnValue = stack.pop();
			returnValue.referenceValue(); // check return type
			return false;
		case Opcodes.FRETURN:
			returnValue = stack.pop();
			returnValue.floatValue(); // check return type
			return false;
		case Opcodes.IRETURN:
			returnValue = stack.pop();
			returnValue.intValue(); // check return type
			return false;
		case Opcodes.RETURN:
			returnValue = null;
			return false;
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
			return true;
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
			return true;
		}
		case Opcodes.BASTORE:
			// Fallthrough
		case Opcodes.SASTORE:
			// Fallthrough
		case Opcodes.CASTORE:
			// Fallthrough
		case Opcodes.IASTORE:
			// Fallthrough
		case Opcodes.LASTORE:
			// Fallthrough
		case Opcodes.FASTORE:
			// Fallthrough
		case Opcodes.DASTORE:
			// Fallthrough
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
			return true;
		}
		case Opcodes.BALOAD:
			// Fallthrough
		case Opcodes.SALOAD:
			// Fallthrough
		case Opcodes.CALOAD:
			// Fallthrough
		case Opcodes.IALOAD:
			// Fallthrough
		case Opcodes.LALOAD:
			// Fallthrough
		case Opcodes.FALOAD:
			// Fallthrough
		case Opcodes.DALOAD:
			// Fallthrough
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
			return true;
		}
		// Locals
		case Opcodes.IINC: {
			final int local = bytecode[pc++] & 0xff;
			locals[local] = Slot.of(locals[local].intValue() + bytecode[pc++]);
			return true;
		}
		case Opcodes.ILOAD:
			stack.push(locals[bytecode[pc++] & 0xff].intValue());
			return true;
		case Opcodes.ILOAD_0:
			stack.push(locals[0].intValue());
			return true;
		case Opcodes.ILOAD_1:
			stack.push(locals[1].intValue());
			return true;
		case Opcodes.ILOAD_2:
			stack.push(locals[2].intValue());
			return true;
		case Opcodes.ILOAD_3:
			stack.push(locals[3].intValue());
			return true;
		case Opcodes.ISTORE:
			locals[bytecode[pc++] & 0xff] = Slot.of(stack.popInt());
			return true;
		case Opcodes.ISTORE_0:
			locals[0] = Slot.of(stack.popInt());
			return true;
		case Opcodes.ISTORE_1:
			locals[1] = Slot.of(stack.popInt());
			return true;
		case Opcodes.ISTORE_2:
			locals[2] = Slot.of(stack.popInt());
			return true;
		case Opcodes.ISTORE_3:
			locals[3] = Slot.of(stack.popInt());
			return true;
		case Opcodes.LLOAD:
			stack.push(locals[bytecode[pc++] & 0xff].longValue());
			return true;
		case Opcodes.LLOAD_0:
			stack.push(locals[0].longValue());
			return true;
		case Opcodes.LLOAD_1:
			stack.push(locals[1].longValue());
			return true;
		case Opcodes.LLOAD_2:
			stack.push(locals[2].longValue());
			return true;
		case Opcodes.LLOAD_3:
			stack.push(locals[3].longValue());
			return true;
		case Opcodes.LSTORE:
			locals[bytecode[pc++] & 0xff] = Slot.of(stack.popLong());
			return true;
		case Opcodes.LSTORE_0:
			locals[0] = Slot.of(stack.popLong());
			return true;
		case Opcodes.LSTORE_1:
			locals[1] = Slot.of(stack.popLong());
			return true;
		case Opcodes.LSTORE_2:
			locals[2] = Slot.of(stack.popLong());
			return true;
		case Opcodes.LSTORE_3:
			locals[3] = Slot.of(stack.popLong());
			return true;
		case Opcodes.FLOAD:
			stack.push(locals[bytecode[pc++] & 0xff].floatValue());
			return true;
		case Opcodes.FLOAD_0:
			stack.push(locals[0].floatValue());
			return true;
		case Opcodes.FLOAD_1:
			stack.push(locals[1].floatValue());
			return true;
		case Opcodes.FLOAD_2:
			stack.push(locals[2].floatValue());
			return true;
		case Opcodes.FLOAD_3:
			stack.push(locals[3].floatValue());
			return true;
		case Opcodes.FSTORE:
			locals[bytecode[pc++] & 0xff] = Slot.of(stack.popFloat());
			return true;
		case Opcodes.FSTORE_0:
			locals[0] = Slot.of(stack.popFloat());
			return true;
		case Opcodes.FSTORE_1:
			locals[1] = Slot.of(stack.popFloat());
			return true;
		case Opcodes.FSTORE_2:
			locals[2] = Slot.of(stack.popFloat());
			return true;
		case Opcodes.FSTORE_3:
			locals[3] = Slot.of(stack.popFloat());
			return true;
		case Opcodes.DLOAD:
			stack.push(locals[bytecode[pc++] & 0xff].doubleValue());
			return true;
		case Opcodes.DLOAD_0:
			stack.push(locals[0].doubleValue());
			return true;
		case Opcodes.DLOAD_1:
			stack.push(locals[1].doubleValue());
			return true;
		case Opcodes.DLOAD_2:
			stack.push(locals[2].doubleValue());
			return true;
		case Opcodes.DLOAD_3:
			stack.push(locals[3].doubleValue());
			return true;
		case Opcodes.DSTORE:
			locals[bytecode[pc++] & 0xff] = Slot.of(stack.popDouble());
			return true;
		case Opcodes.DSTORE_0:
			locals[0] = Slot.of(stack.popDouble());
			return true;
		case Opcodes.DSTORE_1:
			locals[1] = Slot.of(stack.popDouble());
			return true;
		case Opcodes.DSTORE_2:
			locals[2] = Slot.of(stack.popDouble());
			return true;
		case Opcodes.DSTORE_3:
			locals[3] = Slot.of(stack.popDouble());
			return true;
		case Opcodes.ALOAD:
			stack.push(locals[bytecode[pc++] & 0xff].referenceValue());
			return true;
		case Opcodes.ALOAD_0:
			stack.push(locals[0].referenceValue());
			return true;
		case Opcodes.ALOAD_1:
			stack.push(locals[1].referenceValue());
			return true;
		case Opcodes.ALOAD_2:
			stack.push(locals[2].referenceValue());
			return true;
		case Opcodes.ALOAD_3:
			stack.push(locals[3].referenceValue());
			return true;
		case Opcodes.ASTORE:
			locals[bytecode[pc++] & 0xff] = Slot.of(stack.popReference());
			return true;
		case Opcodes.ASTORE_0:
			locals[0] = Slot.of(stack.popReference());
			return true;
		case Opcodes.ASTORE_1:
			locals[1] = Slot.of(stack.popReference());
			return true;
		case Opcodes.ASTORE_2:
			locals[2] = Slot.of(stack.popReference());
			return true;
		case Opcodes.ASTORE_3:
			locals[3] = Slot.of(stack.popReference());
			return true;
		case Opcodes.LDC:
			pushConstant(bytecode[pc++] & 0xff);
			return true;
		case Opcodes.LDC_W:
			pushConstant((bytecode[pc++] & 0xff) << 8 | (bytecode[pc++] & 0xff));
			return true;
		case Opcodes.LDC2_W:
			pushConstant((bytecode[pc++] & 0xff) << 8 | (bytecode[pc++] & 0xff));
			return true;
		// Instances
		case Opcodes.INSTANCEOF: {
			final int index = (bytecode[pc++] & 0xff) << 8 | (bytecode[pc++] & 0xff);
			final Object ref = stack.popReference();
			if (ref == null) {
				stack.push(false);
			} else {
				stack.push(instanceOf(ref, pool.getClassName(index)));
			}
			return true;
		}
		// Invoke
		case Opcodes.INVOKESTATIC: {
			invoke(null, (bytecode[pc++] & 0xff) << 8 | (bytecode[pc++] & 0xff));
			return true;
		}
		case Opcodes.INVOKEVIRTUAL: {
			invoke(stack.popReference(), (bytecode[pc++] & 0xff) << 8 | (bytecode[pc++] & 0xff));
			return true;
		}
		// Field accessors
		case Opcodes.GETSTATIC: {
			getfield(null, (bytecode[pc++] & 0xff) << 8 | (bytecode[pc++] & 0xff));
			return true;
		}
		case Opcodes.PUTSTATIC: {
			putfield(stack.pop(), null, (bytecode[pc++] & 0xff) << 8 | (bytecode[pc++] & 0xff));
			return true;
		}
		case Opcodes.GETFIELD: {
			getfield(stack.popReference(), (bytecode[pc++] & 0xff) << 8 | (bytecode[pc++] & 0xff));
			return true;
		}
		case Opcodes.PUTFIELD: {
			putfield(stack.pop(), stack.popReference(), (bytecode[pc++] & 0xff) << 8 | (bytecode[pc++] & 0xff));
			return true;
		}
		// Miscellaneous
		case Opcodes.IMPDEP1:
			// Fallthrough
		case Opcodes.IMPDEP2:
			// Fallthrough
		case Opcodes.BREAKPOINT:
			throw new IllegalArgumentException(Opcodes.name(opcode) + " should not be found here");
		default:
			throw new IllegalStateException("Unknown opcode: " + Opcodes.name(opcode) + " [" + (opcode & 0xff) + "]");
		}
	}
}
