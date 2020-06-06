/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.compiler;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
public class JavaCTest {
	private static final String TEST_PROPERTY = "com.bandlem.jvm.jvmulator.example.run";
	private SourceFile getSourceFile() {
		return new SourceFile("HelloWorld", //
				"package com.bandlem.jvm.jvmulator.example;" //
						+ "public class HelloWorld implements Runnable {" //
						+ " public void run() {" //
						+ "   System.setProperty(\"" + TEST_PROPERTY + "\",\"true\");" //
						+ " }" //
						+ "}");
	}
	@Test
	void testCompile() throws ReflectiveOperationException {
		final JavaC javac = new JavaC();
		assertTrue(javac.compile(getSourceFile()));
		assertTrue(javac.getDiagnostics().isEmpty());
		assertNotNull(javac.getBytes("com.bandlem.jvm.jvmulator.example.HelloWorld"));
		final ClassLoader loader = javac.newClassLoader();
		final Class<?> helloWorld = loader.loadClass("com.bandlem.jvm.jvmulator.example.HelloWorld");
		assertNotNull(helloWorld);
		final Object instance = helloWorld.getDeclaredConstructor().newInstance();
		assertNotNull(instance);
		assertTrue(instance instanceof Runnable);
		System.setProperty(TEST_PROPERTY, "false");
		((Runnable) instance).run();
		assertEquals("true", System.getProperty(TEST_PROPERTY));
	}
	@Test
	void testNotFound() {
		final JavaC javac = new JavaC();
		assertThrows(ClassNotFoundException.class, () -> javac.newClassLoader().loadClass("missing"));
	}
}
