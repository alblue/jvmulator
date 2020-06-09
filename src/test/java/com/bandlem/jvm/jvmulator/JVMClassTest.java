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
import static com.bandlem.jvm.jvmulator.Opcodes.DCONST_1;
import static com.bandlem.jvm.jvmulator.Opcodes.DRETURN;
import static com.bandlem.jvm.jvmulator.Opcodes.FCONST_1;
import static com.bandlem.jvm.jvmulator.Opcodes.FRETURN;
import static com.bandlem.jvm.jvmulator.Opcodes.GETFIELD;
import static com.bandlem.jvm.jvmulator.Opcodes.GETSTATIC;
import static com.bandlem.jvm.jvmulator.Opcodes.ICONST_1;
import static com.bandlem.jvm.jvmulator.Opcodes.INSTANCEOF;
import static com.bandlem.jvm.jvmulator.Opcodes.INVOKESTATIC;
import static com.bandlem.jvm.jvmulator.Opcodes.INVOKEVIRTUAL;
import static com.bandlem.jvm.jvmulator.Opcodes.IRETURN;
import static com.bandlem.jvm.jvmulator.Opcodes.LCONST_1;
import static com.bandlem.jvm.jvmulator.Opcodes.LDC;
import static com.bandlem.jvm.jvmulator.Opcodes.LDC2_W;
import static com.bandlem.jvm.jvmulator.Opcodes.LDC_W;
import static com.bandlem.jvm.jvmulator.Opcodes.LRETURN;
import static com.bandlem.jvm.jvmulator.Opcodes.PUTFIELD;
import static com.bandlem.jvm.jvmulator.Opcodes.PUTSTATIC;
import static com.bandlem.jvm.jvmulator.Opcodes.RETURN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.DataInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.ClassConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.DoubleConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.FieldRef;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.FloatConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.IntConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.Item;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.LongConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.MethodRef;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.NameAndType;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.StringConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.UTFConstant;
import com.bandlem.jvm.jvmulator.classfile.JavaClass;
class JVMClassTest {
	static class Sample {
		public static boolean bb = false;
		public static double dd = 3.141;
		public static float ff = 2.718f;
		public static int ii = 0xff00;
		public static long ll = 37L;
		public static String ss = "alex.blewitt@gmail.com";
		public boolean b = false;
		public double d = 3.141;
		public float f = 2.718f;
		public int i = 0xff00;
		public long l = 37L;
		public String s = "alex.blewitt@gmail.com";
		public float floaty() {
			return 3.141f;
		}
		public long longy() {
			return 4200L;
		}
		public void reset() {
			dd = d;
			ff = f;
			ii = i;
			ll = l;
			bb = b;
			ss = s;
		}
		public double run() {
			System.gc();
			return Math.random();
		}
	}
	private static byte constant_double;
	private static byte constant_email;
	private static byte constant_email_utf;
	private static byte constant_field_d;
	private static byte constant_field_dd;
	private static byte constant_field_f;
	private static byte constant_field_ff;
	private static byte constant_field_i;
	private static byte constant_field_ii;
	private static byte constant_field_l;
	private static byte constant_field_ll;
	private static byte constant_field_s;
	private static byte constant_field_ss;
	private static byte constant_float;
	private static byte constant_gc;
	private static byte constant_int;
	private static byte constant_long;
	private static byte constant_object;
	private static byte constant_random;
	private static byte constant_system;
	private static JavaClass javaClass;
	private static ConstantPool pool;
	static short find(final Object value, final Class<?> type) {
		for (short i = 1; i < pool.size(); i++) {
			final Item item = pool.getItem(i);
			// Double and Long values have a missing slot
			if (item == null) {
				continue;
			}
			if (type == StringConstant.class && item instanceof StringConstant) {
				if (value.equals(pool.getString(((StringConstant) item).index))) {
					return i;
				}
			} else if (type == UTFConstant.class && item instanceof UTFConstant) {
				if (value.equals((((UTFConstant) item).value))) {
					return i;
				}
			} else if (type == IntConstant.class && item instanceof IntConstant) {
				if (value.equals((((IntConstant) item).value))) {
					return i;
				}
			} else if (type == LongConstant.class && item instanceof LongConstant) {
				if (value.equals((((LongConstant) item).value))) {
					return i;
				}
			} else if (type == FloatConstant.class && item instanceof FloatConstant) {
				if (value.equals((((FloatConstant) item).value))) {
					return i;
				}
			} else if (type == DoubleConstant.class && item instanceof DoubleConstant) {
				if (value.equals((((DoubleConstant) item).value))) {
					return i;
				}
			} else if (type == ClassConstant.class && item instanceof ClassConstant) {
				if (value.equals(pool.getString(((ClassConstant) item).index))) {
					return i;
				}
			} else if (type == MethodRef.class && item instanceof MethodRef) {
				// NB only considers name
				final short nat = ((MethodRef) item).nameAndTypeIndex;
				final short name = ((NameAndType) pool.getItem(nat)).nameIndex;
				if (value.equals(pool.getString(name))) {
					return i;
				}
			} else if (type == FieldRef.class && item instanceof FieldRef) {
				// NB only considers name
				final short nat = ((FieldRef) item).nameAndTypeIndex;
				final short name = ((NameAndType) pool.getItem(nat)).nameIndex;
				if (value.equals(pool.getString(name))) {
					return i;
				}
			}
		}
		throw new IllegalArgumentException("Cannot find " + value + " of type " + type);
	}
	@BeforeAll
	static void setup() {
		final InputStream in = Sample.class
				.getResourceAsStream("/" + Sample.class.getName().replace('.', '/') + ".class");
		javaClass = new JavaClass(new DataInputStream(in));
		pool = javaClass.pool;
		// If the pool is above 256 we have problems with constant resolution
		assertTrue(pool.size() < 256);
		constant_email_utf = (byte) find(Sample.ss, UTFConstant.class);
		constant_email = (byte) find(Sample.ss, StringConstant.class);
		constant_int = (byte) find(Sample.ii, IntConstant.class);
		constant_long = (byte) find(Sample.ll, LongConstant.class);
		constant_float = (byte) find(Sample.ff, FloatConstant.class);
		constant_double = (byte) find(Sample.dd, DoubleConstant.class);
		constant_object = (byte) find("java/lang/Object", ClassConstant.class);
		constant_system = (byte) find("java/lang/System", ClassConstant.class);
		constant_gc = (byte) find("gc", MethodRef.class);
		constant_random = (byte) find("random", MethodRef.class);
		constant_field_i = (byte) find("i", FieldRef.class);
		constant_field_l = (byte) find("l", FieldRef.class);
		constant_field_f = (byte) find("f", FieldRef.class);
		constant_field_d = (byte) find("d", FieldRef.class);
		constant_field_s = (byte) find("s", FieldRef.class);
		constant_field_ii = (byte) find("ii", FieldRef.class);
		constant_field_ll = (byte) find("ll", FieldRef.class);
		constant_field_ff = (byte) find("ff", FieldRef.class);
		constant_field_dd = (byte) find("dd", FieldRef.class);
		constant_field_ss = (byte) find("ss", FieldRef.class);
	}
	private void expect(final Class<? extends Throwable> expected, final JavaClass javaClass, final int locals,
			final Slot slot, final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		if (slot != null) {
			frame.stack.pushSlot(slot);
		}
		assertThrows(expected, frame::run);
	}
	private void expect(final double result, final JavaClass javaClass, final int locals, final Slot slot,
			final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		if (slot != null) {
			frame.stack.pushSlot(slot);
		}
		assertEquals(result, frame.run().doubleValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final float result, final JavaClass javaClass, final int locals, final Slot slot,
			final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		if (slot != null) {
			frame.stack.pushSlot(slot);
		}
		assertEquals(result, frame.run().floatValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final int result, final JavaClass javaClass, final int locals, final Slot slot,
			final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		if (slot != null) {
			frame.stack.pushSlot(slot);
		}
		assertEquals(result, frame.run().intValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final long result, final JavaClass javaClass, final int locals, final Slot slot,
			final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		if (slot != null) {
			frame.stack.pushSlot(slot);
		}
		assertEquals(result, frame.run().longValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	private void expect(final Object result, final JavaClass javaClass, final int locals, final Slot slot,
			final byte[] code) {
		final JVMFrame frame = new JVMFrame(javaClass, locals, code);
		if (slot != null) {
			frame.stack.pushSlot(slot);
		}
		final Slot answer = frame.run();
		if (answer != null) {
			assertEquals(result, answer.referenceValue());
		}
		assertEquals(answer, frame.getReturnValue());
		assertThrows(IndexOutOfBoundsException.class, frame.stack::peek);
	}
	@Test
	void testClassData() {
		new Sample().reset();
		assertEquals("alex.blewitt@gmail.com", pool.getString(constant_email_utf));
		assertEquals(constant_email_utf, ((StringConstant) pool.getItem(constant_email)).index);
		assertEquals("alex.blewitt@gmail.com", pool.getString(((StringConstant) pool.getItem(constant_email)).index));
		assertEquals(Sample.ii, ((IntConstant) pool.getItem(constant_int)).value);
		assertEquals(Sample.ll, ((LongConstant) pool.getItem(constant_long)).value);
		assertEquals(Sample.ff, ((FloatConstant) pool.getItem(constant_float)).value);
		assertEquals(Sample.dd, ((DoubleConstant) pool.getItem(constant_double)).value);
		expect("alex.blewitt@gmail.com", javaClass, 0, null, new byte[] {
				LDC, constant_email, ARETURN
		});
		expect(Sample.ii, javaClass, 0, null, new byte[] {
				LDC, constant_int, IRETURN
		});
		expect(Sample.ll, javaClass, 0, null, new byte[] {
				LDC2_W, 0x00, constant_long, LRETURN
		});
		expect(Sample.ff, javaClass, 0, null, new byte[] {
				LDC_W, 0x00, constant_float, FRETURN
		});
		expect(Sample.dd, javaClass, 0, null, new byte[] {
				LDC2_W, 0x00, constant_double, DRETURN
		});
		expect(UnsupportedOperationException.class, javaClass, 0, null, new byte[] {
				LDC, 0x01, IRETURN
		});
	}
	@Test
	void testFields() {
		final Sample original = new Sample();
		original.reset();
		assertNotNull(JVMFrame.getfield(null, "out", "Ljava/io/PrintStream;", System.class.getName(), null));
		assertEquals(original.d, JVMFrame.getfield(original, "d", "D", Sample.class.getName(), null).doubleValue());
		assertEquals(original.f, JVMFrame.getfield(original, "f", "F", Sample.class.getName(), null).floatValue());
		assertEquals(original.i, JVMFrame.getfield(original, "i", "I", Sample.class.getName(), null).intValue());
		assertEquals(original.l, JVMFrame.getfield(original, "l", "J", Sample.class.getName(), null).longValue());
		assertEquals(Sample.dd, JVMFrame.getfield(null, "dd", "D", Sample.class.getName(), null).doubleValue());
		assertEquals(Sample.ff, JVMFrame.getfield(null, "ff", "F", Sample.class.getName(), null).floatValue());
		assertEquals(Sample.ii, JVMFrame.getfield(null, "ii", "I", Sample.class.getName(), null).intValue());
		assertEquals(Sample.ll, JVMFrame.getfield(null, "ll", "J", Sample.class.getName(), null).longValue());
		assertEquals(Sample.ii, JVMFrame.getfield(null, "ii", "Z", Sample.class.getName(), null).intValue());
		assertEquals(Sample.ii, JVMFrame.getfield(null, "ii", "B", Sample.class.getName(), null).intValue());
		assertEquals(Sample.ii, JVMFrame.getfield(null, "ii", "S", Sample.class.getName(), null).intValue());
		assertEquals(Sample.ii, JVMFrame.getfield(null, "ii", "C", Sample.class.getName(), null).intValue());
		// instance fields
		JVMFrame.putfield(Slot.of(1.23D), original, "d", "D", Sample.class.getName(), null);
		assertEquals(1.23D, original.d);
		JVMFrame.putfield(Slot.of(2.34F), original, "f", "F", Sample.class.getName(), null);
		assertEquals(2.34f, original.f);
		JVMFrame.putfield(Slot.of(5), original, "i", "I", Sample.class.getName(), null);
		assertEquals(5, original.i);
		JVMFrame.putfield(Slot.of(6L), original, "l", "J", Sample.class.getName(), null);
		assertEquals(6L, original.l);
		JVMFrame.putfield(Slot.of("Hello World"), original, "s", "Lsomething;", Sample.class.getName(), null);
		assertEquals("Hello World", original.s);
		// static fields
		JVMFrame.putfield(Slot.of(2.23D), null, "dd", "D", Sample.class.getName(), null);
		assertEquals(2.23D, Sample.dd);
		JVMFrame.putfield(Slot.of(3.34F), null, "ff", "F", Sample.class.getName(), null);
		assertEquals(3.34f, Sample.ff);
		JVMFrame.putfield(Slot.of(7), null, "ii", "I", Sample.class.getName(), null);
		assertEquals(7, Sample.ii);
		JVMFrame.putfield(Slot.of(8L), null, "ll", "J", Sample.class.getName(), null);
		assertEquals(8L, Sample.ll);
		JVMFrame.putfield(Slot.of("Goodbye World"), null, "ss", "Lsomething;", Sample.class.getName(), null);
		assertEquals("Goodbye World", Sample.ss);
		// Short types
		JVMFrame.putfield(Slot.of(-2), original, "i", "C", Sample.class.getName(), null);
		assertEquals((char) -2, original.i);
		JVMFrame.putfield(Slot.of(-3), original, "i", "S", Sample.class.getName(), null);
		assertEquals((short) -3, original.i);
		JVMFrame.putfield(Slot.of(-4), original, "i", "B", Sample.class.getName(), null);
		assertEquals((byte) -4, original.i);
		JVMFrame.putfield(Slot.of(true), original, "b", "Z", Sample.class.getName(), null);
		assertEquals(true, original.b);
		JVMFrame.putfield(Slot.of(-2), null, "ii", "C", Sample.class.getName(), null);
		assertEquals((char) -2, Sample.ii);
		JVMFrame.putfield(Slot.of(-3), null, "ii", "S", Sample.class.getName(), null);
		assertEquals((short) -3, Sample.ii);
		JVMFrame.putfield(Slot.of(-4), null, "ii", "B", Sample.class.getName(), null);
		assertEquals((byte) -4, Sample.ii);
		JVMFrame.putfield(Slot.of(true), null, "bb", "Z", Sample.class.getName(), null);
		assertEquals(true, Sample.bb);
		assertThrows(UnsupportedOperationException.class,
				() -> JVMFrame.getfield(null, "foobar", "V", "missing class", null));
		assertThrows(UnsupportedOperationException.class,
				() -> JVMFrame.putfield(Slot.of(true), null, "foobar", "V", "missing class", null));
	}
	@Test
	void testGet() {
		final Sample sample = new Sample();
		final Slot sampleSlot = Slot.of(sample);
		expect(sample.i, javaClass, 0, sampleSlot, new byte[] {
				GETFIELD, 0x00, constant_field_i, IRETURN
		});
		expect(sample.l, javaClass, 0, sampleSlot, new byte[] {
				GETFIELD, 0x00, constant_field_l, LRETURN
		});
		expect(sample.f, javaClass, 0, sampleSlot, new byte[] {
				GETFIELD, 0x00, constant_field_f, FRETURN
		});
		expect(sample.d, javaClass, 0, sampleSlot, new byte[] {
				GETFIELD, 0x00, constant_field_d, DRETURN
		});
		expect(sample.s, javaClass, 0, sampleSlot, new byte[] {
				GETFIELD, 0x00, constant_field_s, ARETURN
		});
		expect(Sample.ii, javaClass, 0, null, new byte[] {
				GETSTATIC, 0x00, constant_field_ii, IRETURN
		});
		expect(Sample.ll, javaClass, 0, null, new byte[] {
				GETSTATIC, 0x00, constant_field_ll, LRETURN
		});
		expect(Sample.ff, javaClass, 0, null, new byte[] {
				GETSTATIC, 0x00, constant_field_ff, FRETURN
		});
		expect(Sample.dd, javaClass, 0, null, new byte[] {
				GETSTATIC, 0x00, constant_field_dd, DRETURN
		});
		expect(Sample.ss, javaClass, 0, null, new byte[] {
				GETSTATIC, 0x00, constant_field_ss, ARETURN
		});
	}
	@Test
	void testInstanceOf() {
		assertEquals(0, new JVMFrame(javaClass, 0, new byte[] {
				ACONST_NULL, INSTANCEOF, 0x00, constant_object, IRETURN
		}).run().intValue());
		assertEquals(1, new JVMFrame(javaClass, 0, new byte[] {
				LDC, constant_email, INSTANCEOF, 0x00, constant_object, IRETURN
		}).run().intValue());
		assertEquals(0, new JVMFrame(javaClass, 0, new byte[] {
				LDC, constant_email, INSTANCEOF, 0x00, constant_system, IRETURN
		}).run().intValue());
		assertThrows(UnsupportedOperationException.class, () -> JVMFrame.instanceOf("foobar", "foobar"));
	}
	@Test
	void testInvoke() {
		assertNotNull(new JVMFrame(javaClass, 0, new byte[] {
				INVOKESTATIC, 0x00, constant_random, DRETURN
		}).run());
		assertNull(new JVMFrame(javaClass, 0, new byte[] {
				INVOKEVIRTUAL, 0x00, constant_gc, RETURN
		}).run());
	}
	@Test
	void testInvokeDirect() {
		final JVMFrame frame = new JVMFrame(javaClass, 0, new byte[] {
				RETURN
		});
		final ClassLoader classLoader = getClass().getClassLoader();
		frame.stack.push("Hello World");
		final Slot resultInt = frame.invoke("length", "()I", String.class.getName(), classLoader);
		assertNotNull(resultInt);
		assertDoesNotThrow(resultInt::intValue);
		frame.stack.push(new Sample());
		final Slot resultLong = frame.invoke("longy", "()J", Sample.class.getName(), classLoader);
		assertNotNull(resultLong);
		assertDoesNotThrow(resultLong::longValue);
		frame.stack.push(new Sample());
		final Slot resultFloat = frame.invoke("floaty", "()F", Sample.class.getName(), classLoader);
		assertNotNull(resultFloat);
		assertDoesNotThrow(resultFloat::floatValue);
		final Slot resultDouble = frame.invoke("random", "()D", Math.class.getName(), classLoader);
		assertNotNull(resultDouble);
		assertDoesNotThrow(resultDouble::doubleValue);
		frame.stack.push("Alex");
		final Slot stringSlot = frame.invoke("toUpperCase", "()Ljava/lang/String;", String.class.getName(),
				classLoader);
		assertNotNull(stringSlot);
		assertEquals("ALEX", stringSlot.toObject());
		assertThrows(UnsupportedOperationException.class,
				() -> frame.invoke("toSnakeCase", "()Ljava/lang/String;", String.class.getName(), classLoader));
		frame.stack.push(123);
		final Slot negatedSlot = frame.invoke("negateExact", "(I)I", Math.class.getName(), classLoader);
		assertEquals(-123, negatedSlot.intValue());
	}
	@Test
	void testPut() {
		final Sample sample = new Sample();
		final Slot sampleSlot = Slot.of(sample);
		sample.i = 0;
		sample.l = 0;
		sample.f = 0;
		sample.d = 0;
		sample.s = "Not Null";
		expect((Object) null, javaClass, 0, sampleSlot, new byte[] {
				ICONST_1, PUTFIELD, 0x00, constant_field_i, RETURN
		});
		assertEquals(1, sample.i);
		expect((Object) null, javaClass, 0, sampleSlot, new byte[] {
				LCONST_1, PUTFIELD, 0x00, constant_field_l, RETURN
		});
		assertEquals(1, sample.l);
		expect((Object) null, javaClass, 0, sampleSlot, new byte[] {
				FCONST_1, PUTFIELD, 0x00, constant_field_f, RETURN
		});
		assertEquals(1.0f, sample.f);
		expect((Object) null, javaClass, 0, sampleSlot, new byte[] {
				DCONST_1, PUTFIELD, 0x00, constant_field_d, RETURN
		});
		assertEquals(1.0d, sample.d);
		expect((Object) null, javaClass, 0, sampleSlot, new byte[] {
				ACONST_NULL, PUTFIELD, 0x00, constant_field_s, RETURN
		});
		assertNull(sample.s);
		Sample.ss = "Not null";
		Sample.ii = 0;
		Sample.ll = 0;
		Sample.ff = 0;
		Sample.dd = 0;
		expect((Object) null, javaClass, 0, null, new byte[] {
				ICONST_1, PUTSTATIC, 0x00, constant_field_ii, RETURN
		});
		assertEquals(1, Sample.ii);
		expect((Object) null, javaClass, 0, null, new byte[] {
				LCONST_1, PUTSTATIC, 0x00, constant_field_ll, RETURN
		});
		assertEquals(1L, Sample.ll);
		expect((Object) null, javaClass, 0, null, new byte[] {
				FCONST_1, PUTSTATIC, 0x00, constant_field_ff, RETURN
		});
		assertEquals(1.0f, Sample.ff);
		expect((Object) null, javaClass, 0, null, new byte[] {
				DCONST_1, PUTSTATIC, 0x00, constant_field_dd, RETURN
		});
		assertEquals(1.0d, Sample.dd);
		expect((Object) null, javaClass, 0, null, new byte[] {
				ACONST_NULL, PUTSTATIC, 0x00, constant_field_ss, RETURN
		});
		assertNull(Sample.ss);
		sample.reset();
	}
}
