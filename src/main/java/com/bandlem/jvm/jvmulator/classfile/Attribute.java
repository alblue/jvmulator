/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.classfile;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
public abstract class Attribute {
	public static class Code extends Attribute {
		public static final String NAME = "Code";
		private final byte[] bytecode;
		private final short maxLocals;
		private final short maxStack;
		public Code(final byte[] data) {
			super(NAME, data);
			try {
				final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
				maxStack = dis.readShort();
				maxLocals = dis.readShort();
				bytecode = new byte[dis.readInt()];
				dis.readFully(bytecode);
				// exception table
				// code attributes
			} catch (final IOException e) {
				throw new IllegalArgumentException("Unable to parse CodeAttribute", e);
			}
		}
		public byte[] getBytecode() {
			return bytecode;
		}
		public short getMaxLocals() {
			return maxLocals;
		}
		public short getMaxStack() {
			return maxStack;
		}
	}
	public static class Unknown extends Attribute {
		public Unknown(final String name, final byte[] data) {
			super(name, data);
		}
	}
	public static Attribute of(final String name, final byte[] data) {
		if (Code.NAME.equals(name)) {
			return new Code(data);
		} else {
			return new Unknown(name, data);
		}
	}
	public final byte[] data;
	public final String name;
	public Attribute(final String name, final byte[] data) {
		this.name = name;
		this.data = data;
	}
}
