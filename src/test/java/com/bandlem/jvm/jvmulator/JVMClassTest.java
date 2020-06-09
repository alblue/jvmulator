/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
import static com.bandlem.jvm.jvmulator.Opcodes.ACONST_NULL;
import static com.bandlem.jvm.jvmulator.Opcodes.ARETURN;
import static com.bandlem.jvm.jvmulator.Opcodes.DRETURN;
import static com.bandlem.jvm.jvmulator.Opcodes.FRETURN;
import static com.bandlem.jvm.jvmulator.Opcodes.INSTANCEOF;
import static com.bandlem.jvm.jvmulator.Opcodes.INVOKESTATIC;
import static com.bandlem.jvm.jvmulator.Opcodes.INVOKEVIRTUAL;
import static com.bandlem.jvm.jvmulator.Opcodes.IRETURN;
import static com.bandlem.jvm.jvmulator.Opcodes.LDC;
import static com.bandlem.jvm.jvmulator.Opcodes.LDC2_W;
import static com.bandlem.jvm.jvmulator.Opcodes.LDC_W;
import static com.bandlem.jvm.jvmulator.Opcodes.LRETURN;
import static com.bandlem.jvm.jvmulator.Opcodes.RETURN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.ClassConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.DoubleConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.FloatConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.IntConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.Item;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.LongConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.MethodRef;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.StringConstant;
import com.bandlem.jvm.jvmulator.classfile.JavaClass;
class JVMClassTest {
	public interface Example {
		double d = 3.141;
		float f = 2.718f;
		int i = 42;
		long l = 37L;
		String s = "alex.blewitt@gmail.com";
		default double foo() {
			System.gc();
			return Math.random();
		}
	}
	static class InvokeDirect {
		public float floaty() {
			return 3.141f;
		}
		public long longy() {
			return 42L;
		}
	}
	private static final String constantData = "yv66vgAAADcAJgoAGgAbCgAcAB0HAB4HAB8BAAFpAQABSQEADUNvbnN0YW50VmFsdWUDAAAAKgEAAWwBAAFKBQAAAAAAAAAlAQABcwEAEkxqYXZhL2xhbmcvU3RyaW5nOwgAIAEAAWQBAAFEBkAJIMSbpeNUAQABZgEAAUYEQC3ztgEAA2ZvbwEAAygpRAEABENvZGUHACEMACIAIwcAJAwAJQAYAQAEVGVzdAEAEGphdmEvbGFuZy9PYmplY3QBABZhbGV4LmJsZXdpdHRAZ21haWwuY29tAQAQamF2YS9sYW5nL1N5c3RlbQEAAmdjAQADKClWAQAOamF2YS9sYW5nL01hdGgBAAZyYW5kb20GAQADAAQAAAAFABkABQAGAAEABwAAAAIACAAZAAkACgABAAcAAAACAAsAGQANAA4AAQAHAAAAAgAPABkAEAARAAEABwAAAAIAEgAZABQAFQABAAcAAAACABYAAQABABcAGAABABkAAAATAAIAAQAAAAe4AAG4AAKvAAAAAAAA";
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
	void testConstantData() {
		final byte[] bytes = Base64.getDecoder().decode(constantData);
		assertEquals(399, bytes.length);
		final JavaClass javaClass = new JavaClass(new DataInputStream(new ByteArrayInputStream(bytes)));
		final ConstantPool pool = javaClass.pool;
		assertEquals("alex.blewitt@gmail.com", pool.getString(32));
		assertEquals(32, ((StringConstant) pool.getItem(15)).index);
		assertEquals(42, ((IntConstant) pool.getItem(8)).value);
		assertEquals(37, ((LongConstant) pool.getItem(11)).value);
		assertEquals(2.718F, ((FloatConstant) pool.getItem(22)).value);
		assertEquals(3.141D, ((DoubleConstant) pool.getItem(18)).value);
		expect("alex.blewitt@gmail.com", javaClass, 0, new byte[] {
				LDC, 0x0f, ARETURN
		});
		expect(42, javaClass, 0, new byte[] {
				LDC, 0x08, IRETURN
		});
		expect(37L, javaClass, 0, new byte[] {
				LDC2_W, 0x00, 0x0b, LRETURN
		});
		expect(2.718F, javaClass, 0, new byte[] {
				LDC_W, 0x00, 0x16, FRETURN
		});
		expect(3.141D, javaClass, 0, new byte[] {
				LDC2_W, 0x00, 0x12, DRETURN
		});
		expect(UnsupportedOperationException.class, javaClass, 0, new byte[] {
				LDC, 0x01, IRETURN
		});
	}
	@Test
	void testInstanceOf() {
		final byte[] bytes = Base64.getDecoder().decode(constantData);
		assertEquals(399, bytes.length);
		final JavaClass javaClass = new JavaClass(new DataInputStream(new ByteArrayInputStream(bytes)));
		final ConstantPool pool = javaClass.pool;
		final Item objectClass = pool.getItem(4);
		assertTrue(objectClass instanceof ClassConstant);
		assertEquals("java/lang/Object", pool.getClassName(4));
		final Item systemClass = pool.getItem(26);
		assertTrue(systemClass instanceof ClassConstant);
		assertEquals("java/lang/System", pool.getClassName(26));
		final Item string = pool.getItem(15);
		assertTrue(string instanceof StringConstant);
		assertEquals(0, new JVMFrame(javaClass, 0, new byte[] {
				ACONST_NULL, INSTANCEOF, 0x00, 0x04, IRETURN
		}).run().intValue());
		assertEquals(1, new JVMFrame(javaClass, 0, new byte[] {
				LDC, 0x0f, INSTANCEOF, 0x00, 0x04, IRETURN
		}).run().intValue());
		assertEquals(0, new JVMFrame(javaClass, 0, new byte[] {
				LDC, 0x0f, INSTANCEOF, 0x00, 0x1a, IRETURN
		}).run().intValue());
		assertThrows(UnsupportedOperationException.class, () -> JVMFrame.instanceOf("foobar", "foobar"));
	}
	@Test
	void testInvoke() {
		final byte[] bytes = Base64.getDecoder().decode(constantData);
		assertEquals(399, bytes.length);
		final JavaClass javaClass = new JavaClass(new DataInputStream(new ByteArrayInputStream(bytes)));
		final ConstantPool pool = javaClass.pool;
		final Item item = pool.getItem(1);
		assertTrue(item instanceof MethodRef);
		assertNotNull(new JVMFrame(javaClass, 0, new byte[] {
				INVOKESTATIC, 0x00, 0x02, DRETURN
		}).run());
		assertNull(new JVMFrame(javaClass, 0, new byte[] {
				ACONST_NULL, INVOKEVIRTUAL, 0x00, 0x01, RETURN
		}).run());
	}
	@Test
	void testInvokeDirect() {
		final byte[] bytes = Base64.getDecoder().decode(constantData);
		final JavaClass javaClass = new JavaClass(new DataInputStream(new ByteArrayInputStream(bytes)));
		final ConstantPool pool = javaClass.pool;
		final Item item = pool.getItem(1);
		assertTrue(item instanceof MethodRef);
		final JVMFrame frame = new JVMFrame(javaClass, 0, new byte[] {
				INVOKESTATIC, 0x00, 0x01, RETURN
		});
		final ClassLoader classLoader = getClass().getClassLoader();
		final Slot resultInt = frame.invoke("alex", "length", "()I", String.class.getName(), classLoader);
		assertNotNull(resultInt);
		assertDoesNotThrow(resultInt::intValue);
		final Slot resultLong = frame.invoke(new InvokeDirect(), "longy", "()J", InvokeDirect.class.getName(),
				classLoader);
		assertNotNull(resultLong);
		assertDoesNotThrow(resultLong::longValue);
		final Slot resultFloat = frame.invoke(new InvokeDirect(), "floaty", "()F", InvokeDirect.class.getName(),
				classLoader);
		assertNotNull(resultFloat);
		assertDoesNotThrow(resultFloat::floatValue);
		final Slot resultDouble = frame.invoke(null, "random", "()D", Math.class.getName(), classLoader);
		assertNotNull(resultDouble);
		assertDoesNotThrow(resultDouble::doubleValue);
		final Slot stringSlot = frame.invoke("alex", "toUpperCase", "()Ljava/lang/String;", String.class.getName(),
				classLoader);
		assertNotNull(stringSlot);
		assertEquals("ALEX", stringSlot.toObject());
		assertThrows(UnsupportedOperationException.class,
				() -> frame.invoke("alex", "toSnakeCase", "()Ljava/lang/String;", String.class.getName(), classLoader));
		frame.stack.push(123);
		final Slot negatedSlot = frame.invoke(null, "negateExact", "(I)I", Math.class.getName(), classLoader);
		assertEquals(-123, negatedSlot.intValue());
	}
}
