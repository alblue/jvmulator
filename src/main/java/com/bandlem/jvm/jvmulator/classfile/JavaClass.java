/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.classfile;
import java.io.DataInput;
import java.io.IOException;
import com.bandlem.jvm.jvmulator.classfile.Member.Field;
import com.bandlem.jvm.jvmulator.classfile.Member.Method;
public class JavaClass {
	public final Attribute[] classAttributes;
	public final Field[] fields;
	public final short flags;
	public final String[] interfaces;
	public final short major;
	public final Method[] methods;
	public final short minor;
	public final ConstantPool pool;
	public final String super_class;
	public final String this_class;
	public JavaClass(final DataInput di) throws IllegalArgumentException {
		try {
			if (di.readInt() != 0xcafebabe) {
				throw new IllegalArgumentException("Content is not a class file");
			}
			this.minor = di.readShort();
			this.major = di.readShort();
			this.pool = new ConstantPool(di.readShort(), di);
			this.flags = di.readShort();
			this.this_class = pool.getClassName(di.readShort());
			this.super_class = pool.getClassName(di.readShort());
			this.interfaces = new String[di.readShort() & 0xffff];
			for (int i = 0; i < interfaces.length; i++) {
				interfaces[i] = pool.getClassName(di.readShort());
			}
			this.fields = new Field[di.readShort() & 0xffff];
			for (int i = 0; i < fields.length; i++) {
				final short flags = di.readShort();
				final String name = pool.getString(di.readShort());
				final String descriptor = pool.getString(di.readShort());
				fields[i] = new Field(flags, name, descriptor, readAttributes(di, pool));
			}
			this.methods = new Method[di.readShort() & 0xffff];
			for (int i = 0; i < methods.length; i++) {
				final short flags = di.readShort();
				final String name = pool.getString(di.readShort());
				final String descriptor = pool.getString(di.readShort());
				methods[i] = new Method(flags, name, descriptor, readAttributes(di, pool));
			}
			this.classAttributes = readAttributes(di, pool);
		} catch (final IOException e) {
			throw new IllegalArgumentException("Unable to parse bytecode", e);
		}
	}
	public Attribute getAttribute(final String name) {
		for (final var attribute : classAttributes) {
			if (name.equals(attribute.attributeName)) {
				return attribute;
			}
		}
		return null;
	}
	private Attribute[] readAttributes(final DataInput di, final ConstantPool pool) throws IOException {
		final Attribute[] attributes = new Attribute[di.readShort()];
		for (int i = 0; i < attributes.length; i++) {
			final String name = pool.getString(di.readShort());
			final int length = di.readInt();
			final byte[] data = new byte[length];
			di.readFully(data);
			attributes[i] = Attribute.of(name, pool, data);
		}
		return attributes;
	}
}
