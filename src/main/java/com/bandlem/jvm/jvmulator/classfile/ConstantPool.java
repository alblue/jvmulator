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
public class ConstantPool {
	public static class ClassConstant extends Item {
		private static final int TYPE = 7;
		public final short index;
		ClassConstant(final short index) {
			super(TYPE);
			this.index = index;
		}
	}
	public static class DoubleConstant extends Item {
		private static final int TYPE = 6;
		public final double value;
		DoubleConstant(final double value) {
			super(TYPE);
			this.value = value;
		}
	}
	public static class FieldRef extends Item {
		private static final int TYPE = 9;
		public final short classIndex;
		public final short nameAndTypeIndex;
		FieldRef(final short classIndex, final short nameAndTypeIndex) {
			super(TYPE);
			this.classIndex = classIndex;
			this.nameAndTypeIndex = nameAndTypeIndex;
		}
	}
	public static class FloatConstant extends Item {
		private static final int TYPE = 4;
		public final float value;
		FloatConstant(final float value) {
			super(TYPE);
			this.value = value;
		}
	}
	public static class IntConstant extends Item {
		private static final int TYPE = 3;
		public final int value;
		IntConstant(final int value) {
			super(TYPE);
			this.value = value;
		}
	}
	public static class InterfaceMethodRef extends Item {
		private static final int TYPE = 11;
		public final short classIndex;
		public final short nameAndTypeIndex;
		InterfaceMethodRef(final short classIndex, final short nameAndTypeIndex) {
			super(TYPE);
			this.classIndex = classIndex;
			this.nameAndTypeIndex = nameAndTypeIndex;
		}
	}
	public static class InvokeDynamic extends Item {
		public static final int TYPE = 18;
		public final short bootstrapIndex;
		public final short nameAndTypeIndex;
		InvokeDynamic(final short bootstrapIndex, final short nameAndTypeIndex) {
			super(TYPE);
			this.bootstrapIndex = bootstrapIndex;
			this.nameAndTypeIndex = nameAndTypeIndex;
		}
	}
	public static class Item {
		public static Item read(final DataInput di) throws IOException {
			final byte type = di.readByte();
			switch (type) {
			case UTFConstant.TYPE: // 1
				return new UTFConstant(di.readUTF());
			case IntConstant.TYPE: // 3
				return new IntConstant(di.readInt());
			case FloatConstant.TYPE: // 4
				return new FloatConstant(di.readFloat());
			case LongConstant.TYPE: // 5
				return new LongConstant(di.readLong());
			case DoubleConstant.TYPE: // 6
				return new DoubleConstant(di.readDouble());
			case ClassConstant.TYPE: // 7
				return new ClassConstant(di.readShort());
			case StringConstant.TYPE: // 8
				return new StringConstant(di.readShort());
			case FieldRef.TYPE: // 9
				return new FieldRef(di.readShort(), di.readShort());
			case MethodRef.TYPE: // 10
				return new MethodRef(di.readShort(), di.readShort());
			case InterfaceMethodRef.TYPE: // 11
				return new InterfaceMethodRef(di.readShort(), di.readShort());
			case NameAndType.TYPE: // 12
				return new NameAndType(di.readShort(), di.readShort());
			case MethodHandle.TYPE: // 15
				return new MethodHandle(di.readByte(), di.readShort());
			case MethodType.TYPE: // 16
				return new MethodType(di.readShort());
			case InvokeDynamic.TYPE: // 18
				return new InvokeDynamic(di.readShort(), di.readShort());
			case Module.TYPE: // 19
				return new Module(di.readShort());
			case Package.TYPE: // 20
				return new Package(di.readShort());
			default:
				throw new IllegalArgumentException("Unknown type " + type);
			}
		}
		public final int type;
		Item(final int type) {
			this.type = type;
		}
		public String stringValue() {
			throw new IllegalArgumentException("Wrong type");
		}
	}
	public static class LongConstant extends Item {
		private static final int TYPE = 5;
		public final long value;
		LongConstant(final long value) {
			super(TYPE);
			this.value = value;
		}
	}
	public static class MethodHandle extends Item {
		public static final int TYPE = 15;
		public final short referenceIndex;
		public final byte referenceKind;
		MethodHandle(final byte referenceKind, final short referenceIndex) {
			super(TYPE);
			this.referenceKind = referenceKind;
			this.referenceIndex = referenceIndex;
		}
	}
	public static class MethodRef extends Item {
		private static final int TYPE = 10;
		public final short classIndex;
		public final short nameAndTypeIndex;
		MethodRef(final short classIndex, final short nameAndTypeIndex) {
			super(TYPE);
			this.classIndex = classIndex;
			this.nameAndTypeIndex = nameAndTypeIndex;
		}
	}
	public static class MethodType extends Item {
		public static final int TYPE = 16;
		public final short descriptorIndex;
		MethodType(final short descriptorIndex) {
			super(TYPE);
			this.descriptorIndex = descriptorIndex;
		}
	}
	public static class Module extends Item {
		public static final int TYPE = 19;
		public final short nameIndex;
		Module(final short nameIndex) {
			super(TYPE);
			this.nameIndex = nameIndex;
		}
	}
	public static class NameAndType extends Item {
		private static final int TYPE = 12;
		public final short descriptorIndex;
		public final short nameIndex;
		NameAndType(final short nameIndex, final short descriptorIndex) {
			super(TYPE);
			this.nameIndex = nameIndex;
			this.descriptorIndex = descriptorIndex;
		}
	}
	public static class Package extends Item {
		public static final int TYPE = 20;
		public final short nameIndex;
		Package(final short nameIndex) {
			super(TYPE);
			this.nameIndex = nameIndex;
		}
	}
	public static class StringConstant extends Item {
		private static final int TYPE = 8;
		public final short index;
		StringConstant(final short index) {
			super(TYPE);
			this.index = index;
		}
	}
	public static class UTFConstant extends Item {
		private static final int TYPE = 1;
		public final String value;
		UTFConstant(final String value) {
			super(TYPE);
			this.value = value;
		}
		@Override
		public String stringValue() {
			return value;
		}
	}
	private final Item[] items;
	public ConstantPool(final short size, final DataInput di) throws IOException {
		items = new Item[size & 0xffff];
		for (int i = 1; i < items.length; i++) {
			items[i] = Item.read(di);
		}
	}
	public String getClassName(final int index) {
		return getString(((ClassConstant) getItem(index)).index);
	}
	public Item getItem(final int index) {
		if (index == 0) {
			throw new IllegalArgumentException("Constant Pool is 1-indexed");
		}
		return items[index & 0xfff];
	}
	public String getString(final int index) {
		return getItem(index).stringValue();
	}
}
