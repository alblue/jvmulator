/*
 * Copyright (c) 2020, Alex Blewitt, Bandlem Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.bandlem.jvm.jvmulator;
import java.util.ArrayList;
import java.util.List;
public class Stack {
	private final List<Slot> internal = new ArrayList<>();
	public Slot peek() {
		return internal.get(internal.size() - 1);
	}
	public Slot pop() {
		return internal.remove(internal.size() - 1);
	}
	public double popDouble() {
		return pop().doubleValue();
	}
	public float popFloat() {
		return pop().floatValue();
	}
	public int popInt() {
		return pop().intValue();
	}
	public long popLong() {
		return pop().longValue();
	}
	public void push(final double d) {
		push(Slot.of(d));
	}
	public void push(final float f) {
		push(Slot.of(f));
	}
	public void push(final int i) {
		push(Slot.of(i));
	}
	public void push(final long l) {
		push(Slot.of(l));
	}
	public void push(final Slot s) {
		internal.add(s);
	}
}
