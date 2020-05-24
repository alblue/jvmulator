package com.bandlem.jvm.jvmulator;
import static net.bytebuddy.jar.asm.Opcodes.BIPUSH;
import static net.bytebuddy.jar.asm.Opcodes.DADD;
import static net.bytebuddy.jar.asm.Opcodes.DCONST_0;
import static net.bytebuddy.jar.asm.Opcodes.DCONST_1;
import static net.bytebuddy.jar.asm.Opcodes.DDIV;
import static net.bytebuddy.jar.asm.Opcodes.DMUL;
import static net.bytebuddy.jar.asm.Opcodes.DREM;
import static net.bytebuddy.jar.asm.Opcodes.DSUB;
import static net.bytebuddy.jar.asm.Opcodes.FADD;
import static net.bytebuddy.jar.asm.Opcodes.FCONST_0;
import static net.bytebuddy.jar.asm.Opcodes.FCONST_1;
import static net.bytebuddy.jar.asm.Opcodes.FCONST_2;
import static net.bytebuddy.jar.asm.Opcodes.FDIV;
import static net.bytebuddy.jar.asm.Opcodes.FMUL;
import static net.bytebuddy.jar.asm.Opcodes.FREM;
import static net.bytebuddy.jar.asm.Opcodes.FSUB;
import static net.bytebuddy.jar.asm.Opcodes.IADD;
import static net.bytebuddy.jar.asm.Opcodes.ICONST_0;
import static net.bytebuddy.jar.asm.Opcodes.ICONST_1;
import static net.bytebuddy.jar.asm.Opcodes.ICONST_2;
import static net.bytebuddy.jar.asm.Opcodes.ICONST_3;
import static net.bytebuddy.jar.asm.Opcodes.ICONST_4;
import static net.bytebuddy.jar.asm.Opcodes.ICONST_5;
import static net.bytebuddy.jar.asm.Opcodes.ICONST_M1;
import static net.bytebuddy.jar.asm.Opcodes.IDIV;
import static net.bytebuddy.jar.asm.Opcodes.IMUL;
import static net.bytebuddy.jar.asm.Opcodes.IREM;
import static net.bytebuddy.jar.asm.Opcodes.ISUB;
import static net.bytebuddy.jar.asm.Opcodes.LADD;
import static net.bytebuddy.jar.asm.Opcodes.LCONST_0;
import static net.bytebuddy.jar.asm.Opcodes.LCONST_1;
import static net.bytebuddy.jar.asm.Opcodes.LDIV;
import static net.bytebuddy.jar.asm.Opcodes.LMUL;
import static net.bytebuddy.jar.asm.Opcodes.LREM;
import static net.bytebuddy.jar.asm.Opcodes.LSUB;
import static net.bytebuddy.jar.asm.Opcodes.NOP;
import static net.bytebuddy.jar.asm.Opcodes.SIPUSH;
import static net.bytebuddy.jar.asm.Opcodes.SWAP;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
class JVMTest {
	private void expect(final double result, final byte[] code) {
		final JVM jvm = run(code);
		assertEquals(result, jvm.stack.pop().doubleValue());
		assertThrows(IndexOutOfBoundsException.class, jvm.stack::peek);
	}
	private void expect(final float result, final byte[] code) {
		final JVM jvm = run(code);
		assertEquals(result, jvm.stack.pop().floatValue());
		assertThrows(IndexOutOfBoundsException.class, jvm.stack::peek);
	}
	private void expect(final int result, final byte[] code) {
		final JVM jvm = run(code);
		assertEquals(result, jvm.stack.pop().intValue());
		assertThrows(IndexOutOfBoundsException.class, jvm.stack::peek);
	}
	private void expect(final long result, final byte[] code) {
		final JVM jvm = run(code);
		assertEquals(result, jvm.stack.pop().longValue());
		assertThrows(IndexOutOfBoundsException.class, jvm.stack::peek);
	}
	private JVM run(final byte[] code) {
		final JVM jvm = new JVM();
		jvm.setBytecode(code);
		jvm.run();
		return jvm;
	}
	@Test
	void testBadStackSwap() {
		final JVM jvm = new JVM();
		jvm.setBytecode(new byte[] {
				LCONST_0, DCONST_0, SWAP
		});
		jvm.step();
		jvm.step();
		assertThrows(IllegalStateException.class, jvm::step);
	}
	@Test
	void testConstantPush() {
		expect(10, new byte[] {
				BIPUSH, 0x0a
		});
		expect(314, new byte[] {
				SIPUSH, 0x01, 0x3a
		});
	}
	@Test
	void testDouble() {
		expect(1.0D, new byte[] {
				DCONST_0, DCONST_1, DADD
		});
		expect(-1.0D, new byte[] {
				DCONST_1, DCONST_0, DSUB
		});
		expect(4.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DMUL
		});
		expect(1.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DDIV
		});
		expect(0.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DCONST_1, DADD, DREM
		});
		expect(-1.0D, new byte[] {
				DCONST_1, DCONST_1, DADD, DCONST_1, DSUB
		});
	}
	@Test
	void testFloat() {
		expect(3.0F, new byte[] {
				FCONST_0, FCONST_1, FCONST_2, FADD, FADD
		});
		expect(4.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FMUL
		});
		expect(1.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FDIV
		});
		expect(0.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FCONST_1, FADD, FREM
		});
		expect(-1.0F, new byte[] {
				FCONST_1, FCONST_1, FADD, FCONST_1, FSUB
		});
	}
	@Test
	void testInteger() {
		expect(-24, new byte[] {
				ICONST_4, ICONST_3, ICONST_1, ICONST_0, ICONST_M1, IADD, ISUB, IMUL, IMUL
		});
		expect(2, new byte[] {
				ICONST_5, NOP, ICONST_2, IREM
		});
		expect(1, new byte[] {
				ICONST_2, ICONST_5, IREM
		});
		expect(0, new byte[] {
				ICONST_5, ICONST_2, IDIV
		});
		expect(2, new byte[] {
				ICONST_2, ICONST_5, IDIV
		});
	}
	@Test
	void testInvalid() {
		final JVM jvm = new JVM();
		jvm.setBytecode(new byte[] {
				(byte) 0xff
		});
		assertThrows(IllegalStateException.class, jvm::run);
	}
	@Test
	void testLong() {
		expect(1L, new byte[] {
				LCONST_0, LCONST_1, LADD
		});
		expect(4L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LMUL
		});
		expect(1L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LDIV
		});
		expect(0L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LCONST_1, LADD, LREM
		});
		expect(-1L, new byte[] {
				LCONST_1, LCONST_1, LADD, LCONST_1, LSUB
		});
	}
	@Test
	void testStackManiupulation() {
		expect(-1, new byte[] {
				ICONST_0, ICONST_1, SWAP, ISUB
		});
	}
	@Test
	void testSupportedBytecodes() {
		for (byte b = 0; b < 17; b++) {
			final JVM jvm = new JVM();
			jvm.setBytecode(new byte[] {
					b, 0x01, 0x02
			});
			assertDoesNotThrow(jvm::step, "Bytecode " + b + " not supported");
		}
	}
}
