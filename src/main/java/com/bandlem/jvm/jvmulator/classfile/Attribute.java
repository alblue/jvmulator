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
		public Code(final DataInputStream dis) throws IOException {
			super(NAME);
			maxStack = dis.readShort();
			maxLocals = dis.readShort();
			bytecode = new byte[dis.readInt()];
			dis.readFully(bytecode);
			// exception table
			// code attributes
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
	public static class SourceFile extends Attribute {
		public static final String NAME = "SourceFile";
		public final String file;
		public SourceFile(final DataInputStream dis, final ConstantPool pool) throws IOException {
			super(NAME);
			this.file = pool.getString(dis.readShort());
		}
		@Override
		public String toString() {
			return file;
		}
	}
	public static class Unknown extends Attribute {
		public final byte[] data;
		public Unknown(final String attributeName, final byte[] data) {
			super(attributeName);
			this.data = data;
		}
	}
	public static Attribute of(final String attributeName, final ConstantPool pool, final byte[] data) {
		try {
			final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
			if (Code.NAME.equals(attributeName)) {
				return new Code(dis);
			} else if (SourceFile.NAME.equals(attributeName)) {
				return new SourceFile(dis, pool);
			} else {
				return new Unknown(attributeName, data);
			}
		} catch (final IOException e) {
			throw new IllegalArgumentException("Unable to parse " + attributeName, e);
		}
	}
	public final String attributeName;
	public Attribute(final String attributeName) {
		this.attributeName = attributeName;
	}
}
