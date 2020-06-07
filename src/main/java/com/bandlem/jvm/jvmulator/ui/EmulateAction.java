package com.bandlem.jvm.jvmulator.ui;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import com.bandlem.jvm.jvmulator.classfile.JavaClass;
class EmulateAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final GUI gui;
	EmulateAction(final GUI gui) {
		super("Emulate");
		setEnabled(false);
		this.gui = gui;
	}
	@Override
	public void actionPerformed(final ActionEvent e) {
		final JavaClass javaClass = new JavaClass(new DataInputStream(new ByteArrayInputStream(gui.getClassBytes())));
		final JVMulator jvmulator = new JVMulator(javaClass);
		final JFrame jvmulatorFrame = new JFrame("JVMulator");
		jvmulatorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jvmulatorFrame.add(jvmulator);
		jvmulatorFrame.pack();
		jvmulatorFrame.setVisible(true);
		jvmulatorFrame.setSize(jvmulator.getMinimumSize());
		jvmulator.setName(gui.getSelectedMethod().getName());
	}
}
