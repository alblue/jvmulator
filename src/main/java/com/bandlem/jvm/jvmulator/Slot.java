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
			super(value, true);
		}
		@Override
		protected Object toObject() {
			return value;
		}
		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}
	public static class Empty extends Slot {
		protected Empty() {
			super(null, false);
		}
		@Override
		public String toString() {
			return "---";
		}
	}
	private static class FloatSlot extends Slot {
		public FloatSlot(final float value) {
			super(value, false);
		}
		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}
	private static class IntSlot extends Slot {
		public IntSlot(final int value) {
			super(value, false);
		}
		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}
	private static class LongSlot extends Slot {
		public LongSlot(final long value) {
			super(value, true);
		}
		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}
	private static class ReferenceSlot extends Slot {
		public ReferenceSlot(final Object value) {
			super(value, false);
		}
		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}
	private static final Slot EMPTY = new Empty();
	public static Slot empty() {
		return EMPTY;
	}
	public static Slot of(final boolean b) {
		return new IntSlot(b ? 1 : 0);
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
	public static Slot of(final Object object) {
		if (object instanceof Slot) {
			throw new IllegalStateException("Attempted to wrap slot in slot");
		}
		return new ReferenceSlot(object);
	}
	protected final Object value;
	private final boolean wide;
	protected Slot(final Object value, final boolean wide) {
		this.value = value;
		this.wide = wide;
	}
	public boolean booleanValue() {
		return 0 != (int) ((IntSlot) this).value;
	}
	public double doubleValue() {
		return (double) ((DoubleSlot) this).value;
	}
	public float floatValue() {
		return (float) ((FloatSlot) this).value;
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
	public Object referenceValue() {
		// The cast to reference slot ensures this is a reference
		// The code could be changed to 'return this.value' but then
		// the code wouldn't correctly check that it is a ReferenceSlot type
		return ((ReferenceSlot) this).value;
	}
	protected Object toObject() {
		return value;
	}
}
