/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.classfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.ClassConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.DoubleConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.FieldRef;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.FloatConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.IntConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.InterfaceMethodRef;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.InvokeDynamic;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.Item;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.LongConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.MethodHandle;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.MethodRef;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.MethodType;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.Module;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.NameAndType;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.Package;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.StringConstant;
import com.bandlem.jvm.jvmulator.classfile.ConstantPool.UTFConstant;
public class ConstantPoolTest {
	Item item(final int... data) throws IOException {
		final byte[] bytes = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			bytes[i] = (byte) data[i];
		}
		final ConstantPool pool = new ConstantPool((short) 2, with(bytes));
		return pool.getItem(1);
	}
	@Test
	void testItems() throws IOException {
		final UTFConstant utfItem = (UTFConstant) item(0x01, 0x00, 0x06, 0x61, 0x6c, 0x62, 0x6c, 0x75, 0x65);
		assertEquals(1, utfItem.type);
		assertEquals("alblue", utfItem.value);
		assertEquals("alblue", utfItem.stringValue());
		final IntConstant intItem = (IntConstant) item(0x03, 0x00, 0x00, 0x00, 0x01);
		assertEquals(3, intItem.type);
		assertEquals(1, intItem.value);
		final FloatConstant floatItem = (FloatConstant) item(0x04, 0x7f, 0x80, 0x00, 0x00);
		assertEquals(4, floatItem.type);
		assertEquals(Float.POSITIVE_INFINITY, floatItem.value);
		final LongConstant longItem = (LongConstant) item(0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02);
		assertEquals(5, longItem.type);
		assertEquals(2, longItem.value);
		final DoubleConstant doubleItem = (DoubleConstant) item(0x06, 0xff, 0xf0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);
		assertEquals(6, doubleItem.type);
		assertEquals(Double.NEGATIVE_INFINITY, doubleItem.value);
		final ClassConstant classItem = (ClassConstant) item(0x07, 0x01, 0x04);
		assertEquals(7, classItem.type);
		assertEquals(0x104, classItem.index);
		final StringConstant stringItem = (StringConstant) item(0x08, 0x03, 0x04);
		assertEquals(8, stringItem.type);
		assertEquals(0x304, stringItem.index);
		final FieldRef fieldRefItem = (FieldRef) item(0x09, 0x01, 0x12, 0x03, 0x14);
		assertEquals(9, fieldRefItem.type);
		assertEquals(0x112, fieldRefItem.classIndex);
		assertEquals(0x314, fieldRefItem.nameAndTypeIndex);
		final MethodRef methodRefItem = (MethodRef) item(0x0a, 0x01, 0x22, 0x03, 0x24);
		assertEquals(10, methodRefItem.type);
		assertEquals(0x122, methodRefItem.classIndex);
		assertEquals(0x324, methodRefItem.nameAndTypeIndex);
		final InterfaceMethodRef interfaceMethodRefItem = (InterfaceMethodRef) item(0x0b, 0x01, 0x22, 0x03, 0x24);
		assertEquals(11, interfaceMethodRefItem.type);
		assertEquals(0x122, interfaceMethodRefItem.classIndex);
		assertEquals(0x324, interfaceMethodRefItem.nameAndTypeIndex);
		final NameAndType natItem = (NameAndType) item(0x0c, 0x01, 0x02, 0x03, 0x04);
		assertEquals(12, natItem.type);
		assertEquals(0x102, natItem.nameIndex);
		assertEquals(0x304, natItem.descriptorIndex);
		final MethodHandle methodHandleItem = (MethodHandle) item(0x0f, 0x03, 0x05, 0x04);
		assertEquals(15, methodHandleItem.type);
		assertEquals(0x3, methodHandleItem.referenceKind);
		assertEquals(0x504, methodHandleItem.referenceIndex);
		final MethodType methodTypeItem = (MethodType) item(0x10, 0x05, 0x04);
		assertEquals(16, methodTypeItem.type);
		assertEquals(0x504, methodTypeItem.descriptorIndex);
		final InvokeDynamic invokeDynamicItem = (InvokeDynamic) item(0x12, 0x02, 0x22, 0x04, 0x24);
		assertEquals(18, invokeDynamicItem.type);
		assertEquals(0x222, invokeDynamicItem.bootstrapIndex);
		assertEquals(0x424, invokeDynamicItem.nameAndTypeIndex);
		assertThrows(IllegalArgumentException.class, invokeDynamicItem::stringValue);
		assertThrows(IllegalArgumentException.class, () -> item(0x2));
		final Module moduleItem = (Module) item(0x13, 0x04, 0x02);
		assertEquals(19, moduleItem.type);
		assertEquals(0x402, moduleItem.nameIndex);
		final Package packageItem = (Package) item(0x14, 0x07, 0x47);
		assertEquals(20, packageItem.type);
		assertEquals(0x747, packageItem.nameIndex);
	}
	@Test
	void testPool() throws IOException {
		final ConstantPool empty = new ConstantPool((short) 1, with(new byte[] {}));
		assertThrows(IllegalArgumentException.class, () -> empty.getItem(0));
		final ConstantPool single = new ConstantPool((short) 3, with(new byte[] {
				0x01, 0x00, 0x06, 0x61, 0x6c, 0x62, 0x6c, 0x75, 0x65, // UTF-8 item
				0x07, 0x00, 0x01 // Class item
		}));
		assertEquals("alblue", single.getString(1));
		assertEquals("alblue", single.getClassName(2));
		assertEquals(1, empty.size());
		assertEquals(3, single.size());
	}
	DataInput with(final byte... data) throws IOException {
		return new DataInputStream(new ByteArrayInputStream(data));
	}
}
