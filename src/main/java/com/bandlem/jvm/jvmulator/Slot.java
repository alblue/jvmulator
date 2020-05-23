/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
public abstract class Slot {
	private static class DoubleSlot extends Slot {
		public DoubleSlot(final double value) {
			super(value, JVMType.DOUBLE, true);
		}
	}
	public static class Empty extends Slot {
		protected Empty() {
			super(null, JVMType.VOID, false);
		}
	}
	private static class FloatSlot extends Slot {
		public FloatSlot(final float value) {
			super(value, JVMType.FLOAT, false);
		}
	}
	private static class IntSlot extends Slot {
		public IntSlot(final int value) {
			super(value, JVMType.INTEGER, false);
		}
	}
	private static class LongSlot extends Slot {
		public LongSlot(final long value) {
			super(value, JVMType.LONG, true);
		}
	}
	private static final Slot EMPTY = new Empty();
	public static Slot empty() {
		return EMPTY;
	}
	public static Slot of(final double d) {
		return new DoubleSlot(d);
	}
	public static Slot of(final float f) {
		return new FloatSlot(f);
	}
	public static Slot of(final int i) {
		return new IntSlot(i);
	}
	public static Slot of(final long l) {
		return new LongSlot(l);
	}
	private final JVMType type;
	protected final Object value;
	private final boolean wide;
	protected Slot(final Object value, final JVMType type, final boolean wide) {
		this.value = value;
		this.type = type;
		this.wide = wide;
	}
	public double doubleValue() {
		return (double) ((DoubleSlot) this).value;
	}
	public float floatValue() {
		return (float) ((FloatSlot) this).value;
	}
	public JVMType getType() {
		return type;
	}
	public Object getValue() {
		return value;
	}
	public int intValue() {
		return (int) ((IntSlot) this).value;
	}
	public final boolean isWide() {
		return wide;
	}
	public long longValue() {
		return (long) ((LongSlot) this).value;
	}
}
