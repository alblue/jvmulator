/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.classfile;
import java.util.ArrayList;
import java.util.List;
import com.bandlem.jvm.jvmulator.classfile.Attribute.Code;
public abstract class Member {
	public static class Field extends Member {
		public Field(final short flags, final String name, final String descriptor, final Attribute[] attributes) {
			super(flags, name, descriptor, attributes);
		}
	}
	public static class Method extends Member {
		public static Class<?>[] argumentTypes(final String descriptor, ClassLoader loader) {
			if (loader == null) {
				loader = Method.class.getClassLoader();
			}
			final byte[] bytes = descriptor.getBytes();
			final List<Class<?>> types = new ArrayList<>();
			for (int i = 0; i < bytes.length; i++) {
				switch (bytes[i]) {
				case '(':
					continue;
				case ')':
					return types.toArray(new Class[0]);
				case 'Z':
					types.add(Boolean.TYPE);
					break;
				case 'S':
					types.add(Short.TYPE);
					break;
				case 'C':
					types.add(Character.TYPE);
					break;
				case 'I':
					types.add(Integer.TYPE);
					break;
				case 'J':
					types.add(Long.TYPE);
					break;
				case 'F':
					types.add(Float.TYPE);
					break;
				case 'D':
					types.add(Double.TYPE);
					break;
				case 'L':
					final int start = i + 1;
					// Converts java/lang/String to java.lang.String
					while (bytes[i] != ';') {
						if (bytes[i] == '/')
							bytes[i] = '.';
						i++;
					}
					final String clazz = new String(bytes, start, i - start);
					try {
						types.add(loader.loadClass(clazz));
					} catch (final ClassNotFoundException e) {
						throw new RuntimeException("Cannot load class " + clazz, e);
					}
					break;
				default:
					throw new IllegalArgumentException("Unknown type " + (char) bytes[i]);
				}
			}
			throw new IllegalStateException("Read to end of " + descriptor + " without closing )");
		}
		public Method(final short flags, final String name, final String descriptor, final Attribute[] attributes) {
			super(flags, name, descriptor, attributes);
		}
		public Class<?>[] argumentTypes(final ClassLoader classLoader) {
			return argumentTypes(descriptor, classLoader);
		}
	}
	public final Attribute[] attributes;
	public final String descriptor;
	public final short flags;
	public final String name;
	public Member(final short flags, final String name, final String descriptor, final Attribute[] attributes) {
		this.flags = flags;
		this.name = name;
		this.descriptor = descriptor;
		this.attributes = attributes;
	}
	public Attribute getAttribute(final String name) {
		for (final Attribute attribute : attributes) {
			if (name.equals(attribute.attributeName)) {
				return attribute;
			}
		}
		return null;
	}
	public Code getCodeAttribute() {
		return (Code) getAttribute(Code.NAME);
	}
}
