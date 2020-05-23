/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
public enum JVMType {
	BOOLEAN('Z', 1), //
	BYTE('B', 8), //
	CHAR('C', 16), //
	DOUBLE('D', 64), //
	FLOAT('F', 32), //
	INTEGER('I', 32), //
	LONG('L', 64), //
	SHORT('S', 16), //
	VOID('V', 0);
	public final char id;
	public final int size;
	private JVMType(final char id, final int size) {
		this.id = id;
		this.size = size;
	}
}
