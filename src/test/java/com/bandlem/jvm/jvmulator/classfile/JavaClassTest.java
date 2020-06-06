package com.bandlem.jvm.jvmulator.classfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.bandlem.jvm.jvmulator.Opcodes;
public class JavaClassTest {
	private static class ClassUnderTest implements Runnable {
		private String field;
		@Override
		public void run() {
			field = "Executed";
		}
		@Override
		public String toString() {
			return field;
		}
	}
	private JavaClass classUnderTest;
	private ConstantPool pool;
	private DataInput dis(final byte... bytes) {
		return new DataInputStream(new ByteArrayInputStream(bytes));
	}
	@BeforeEach
	void setupClass() {
		final String name = ClassUnderTest.class.getName().replace('.', '/') + ".class";
		final InputStream stream = ClassUnderTest.class.getClassLoader().getResourceAsStream(name);
		classUnderTest = new JavaClass(new DataInputStream(stream));
		assertNotNull(classUnderTest);
		pool = classUnderTest.pool;
	}
	@Test
	void testClass() {
		assertEquals(ClassUnderTest.class.getName().replace('.', '/'), classUnderTest.this_class);
		assertEquals(Object.class.getName().replace('.', '/'), classUnderTest.super_class);
		assertEquals(1, classUnderTest.interfaces.length);
		assertEquals(Runnable.class.getName().replace('.', '/'), classUnderTest.interfaces[0]);
		assertEquals(1, classUnderTest.fields.length);
		assertEquals("field", classUnderTest.fields[0].name);
		assertEquals(3, classUnderTest.methods.length);
		assertEquals("<init>", classUnderTest.methods[0].name);
		final Attribute.Code code = (Attribute.Code) classUnderTest.methods[0].getAttribute("Code");
		assertEquals("Code", code.attributeName);
		final byte[] bytecode = code.getBytecode();
		// Default constructor is:
		// aload_0
		// invokespecial <byte> <byte>
		// return
		assertEquals(5, bytecode.length);
		assertEquals(Opcodes.ALOAD_0, bytecode[0]);
		assertEquals(Opcodes.INVOKESPECIAL, bytecode[1]);
		assertEquals(Opcodes.RETURN, bytecode[4]);
		assertEquals(JavaClassTest.class.getSimpleName() + ".java",
				classUnderTest.getAttribute("SourceFile").toString());
		assertNull(classUnderTest.getAttribute("WhoNose"));
		assertThrows(IllegalArgumentException.class, () -> pool.getItem(0));
		assertEquals(System.getProperty("java.class.version"), classUnderTest.major + "." + classUnderTest.minor);
	}
	@Test
	void testInvalidClass() {
		assertThrows(IllegalArgumentException.class,
				() -> new JavaClass(dis((byte) 0xb0, (byte) 0x00, (byte) 0xb0, (byte) 0x00)));
		assertThrows(IllegalArgumentException.class, () -> new JavaClass(dis()));
	}
}
