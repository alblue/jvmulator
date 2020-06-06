/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.compiler;
import java.util.Map;
public class InMemoryClassLoader extends ClassLoader {
	private final Map<String, byte[]> map;
	public InMemoryClassLoader(final Map<String, byte[]> map, final ClassLoader parent) {
		super(parent);
		this.map = map;
	}
	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		final byte[] bytes = map.get(name);
		if (bytes == null) {
			throw new ClassNotFoundException(name);
		}
		return defineClass(name, bytes, 0, bytes.length);
	}
}
