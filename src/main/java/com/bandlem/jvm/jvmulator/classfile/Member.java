/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator.classfile;
import com.bandlem.jvm.jvmulator.classfile.Attribute.Code;
public abstract class Member {
	public static class Field extends Member {
		public Field(final short flags, final String name, final String descriptor, final Attribute[] attributes) {
			super(flags, name, descriptor, attributes);
		}
	}
	public static class Method extends Member {
		public Method(final short flags, final String name, final String descriptor, final Attribute[] attributes) {
			super(flags, name, descriptor, attributes);
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
			if (name.equals(attribute.name)) {
				return attribute;
			}
		}
		return null;
	}
	public Code getCodeAttribute() {
		return (Code) getAttribute(Code.NAME);
	}
}
