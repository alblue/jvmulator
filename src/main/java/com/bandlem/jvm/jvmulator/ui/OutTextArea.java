package com.bandlem.jvm.jvmulator.ui;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.JTextArea;
public class OutTextArea extends PrintStream {
	private final JTextArea console;
	public OutTextArea(final JTextArea console, final PrintStream wrapped) {
		super(wrapped);
		this.console = console;
	}
	@Override
	public void write(final byte[] bytes) throws IOException {
		console.append(new String(bytes));
		console.setVisible(true);
		super.write(bytes);
	}
	@Override
	public void write(final byte[] bytes, final int offset, final int length) {
		console.append(new String(bytes, offset, length));
		console.setVisible(true);
		super.write(bytes, offset, length);
	}
	@Override
	public void write(final int b) {
		console.append(new String(new byte[] {
				(byte) b
		}));
		console.setVisible(true);
		super.write(b);
	}
}
