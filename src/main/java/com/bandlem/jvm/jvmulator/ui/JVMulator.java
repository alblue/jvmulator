package com.bandlem.jvm.jvmulator.ui;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import com.bandlem.jvm.jvmulator.JVMFrame;
import com.bandlem.jvm.jvmulator.Opcodes;
import com.bandlem.jvm.jvmulator.Slot;
import com.bandlem.jvm.jvmulator.Stack;
import com.bandlem.jvm.jvmulator.classfile.Attribute.Code;
import com.bandlem.jvm.jvmulator.classfile.JavaClass;
import com.bandlem.jvm.jvmulator.classfile.Member.Method;
public class JVMulator extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JTextArea bytecode = new JTextArea("", 100, 100);
	private byte[] code;
	private JVMFrame frame;
	private final JavaClass javaClass;
	private final JTextArea locals = new JTextArea("", 100, 100);
	private int pc;
	private final JTextArea stack = new JTextArea("", 100, 100);
	public JVMulator(final JavaClass javaClass) {
		this.javaClass = javaClass;
		final Font monospaced = new Font(Font.MONOSPACED, Font.PLAIN, 24);
		if (monospaced != null) {
			bytecode.setFont(monospaced);
			locals.setFont(monospaced);
			stack.setFont(monospaced);
		}
		bytecode.setEditable(false);
		locals.setEditable(false);
		stack.setEditable(false);
		bytecode.setBorder(BorderFactory.createTitledBorder("Bytecode"));
		locals.setBorder(BorderFactory.createTitledBorder("Locals"));
		stack.setBorder(BorderFactory.createTitledBorder("Stack"));
		setLayout(new GridBagLayout());
		add(bytecode, GUI.constraints(0, 0));
		add(locals, GUI.constraints(1, 0));
		add(stack, GUI.constraints(2, 0));
		final Dimension minimumSize = new Dimension(400, 400);
		bytecode.setMinimumSize(minimumSize);
		locals.setMinimumSize(minimumSize);
		stack.setMinimumSize(minimumSize);
		add(new JButton(new StepAction(this)), GUI.constraints(0, 1));
	}
	private void displayCode() {
		final StringBuilder builder = new StringBuilder(code.length * 10);
		int i = 0;
		while (i < code.length) {
			final byte opcode = code[i];
			builder.append(String.format("%3s%3d: %2x %s\n", pc == i ? "=>" : "", i, opcode, Opcodes.name(opcode)));
			i++;
			final int operands = Opcodes.operands(opcode);
			if (operands < 0) {
				throw new UnsupportedOperationException("Unknown opcode " + opcode);
			} else {
				i += operands;
			}
		}
		bytecode.setText(builder.toString());
	}
	private void displayLocals() {
		final Slot[] l = frame.getLocals();
		final StringBuilder builder = new StringBuilder(l.length * 10);
		for (int s = 0; s < l.length; s++) {
			builder.append(String.format("[%02d] %s\n", s, l[s].toString()));
		}
		locals.setText(builder.toString());
	}
	private void displayStack() {
		final Stack s = frame.getStack();
		final StringBuilder builder = new StringBuilder(s.size() * 10);
		for (int ss = 0; ss < s.size(); ss++) {
			builder.append(String.format("[%02d] %s\n", ss, s.at(ss).toString()));
		}
		stack.setText(builder.toString());
	}
	@Override
	public void setName(final String name) {
		final Method method = javaClass.getMethod(name);
		if (method == null) {
			code = new byte[0];
		} else {
			final Code codeAttribute = method.getCodeAttribute();
			code = codeAttribute.getBytecode();
			frame = new JVMFrame(javaClass, codeAttribute.getMaxLocals(), code);
			displayCode();
			displayLocals();
			displayStack();
		}
	}
	public void step() {
		if (pc >= 0 && frame.step()) {
			pc = frame.getPC();
			displayCode();
			displayLocals();
			displayStack();
		} else {
			pc = -1;
			JOptionPane.showMessageDialog(null, "Return value: " + frame.getReturnValue(), "Returned",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
