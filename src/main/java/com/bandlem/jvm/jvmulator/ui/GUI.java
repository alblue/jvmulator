package com.bandlem.jvm.jvmulator.ui;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import com.formdev.flatlaf.FlatLightLaf;
public class GUI extends JPanel {
	private static final long serialVersionUID = 1L;
	static GridBagConstraints constraints(final int x, final int y) {
		return new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0);
	}
	public static void main(final String[] args) {
		FlatLightLaf.install();
		final JFrame frame = new JFrame("JVMulator");
		final GUI gui = new GUI();
		frame.add(gui);
		frame.setSize(gui.getMinimumSize());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	private byte[] classBytes;
	private final Action compile = new CompileAction(this);
	private final JTextArea console = new JTextArea("", 100, 100);
	private final Action emulate = new EmulateAction(this);
	private final Action invoke = new InvokeAction(this);
	private final JComboBox<Method> methods = new JComboBox<>();
	private final OutTextArea out = new OutTextArea(console, System.out);
	private final JTextArea source = new JTextArea(getExample(), 100, 100);
	public GUI() {
		final Font monospaced = new Font(Font.MONOSPACED, Font.PLAIN, 24);
		if (monospaced != null) {
			source.setFont(monospaced);
			console.setFont(monospaced);
		}
		source.setBorder(BorderFactory.createTitledBorder("Source"));
		console.setBorder(BorderFactory.createTitledBorder("Console"));
		setLayout(new GridBagLayout());
		add(source, constraints(0, 0));
		add(console, constraints(1, 0));
		add(new JButton(compile), constraints(0, 1));
		add(new JButton(invoke), constraints(1, 1));
		add(methods, constraints(0, 2));
		add(new JButton(emulate), constraints(1, 2));
		System.setOut(out);
	}
	public void addMethod(final Method method) {
		if (method.getDeclaringClass() != Object.class && (method.getModifiers() & Modifier.STATIC) != 0) {
			methods.addItem(method);
			enableButtons(true);
		}
	}
	public void clearConsole() {
		console.setText("");
	}
	public void clearMethods() {
		methods.removeAllItems();
		enableButtons(false);
	}
	private void enableButtons(final boolean enabled) {
		invoke.setEnabled(enabled);
		methods.setEnabled(enabled);
		emulate.setEnabled(enabled);
	}
	public byte[] getClassBytes() {
		return classBytes;
	}
	private String getExample() {
		return "public class Example {\n" //
				+ "  public static void gc() { System.gc(); }\n" //
				+ "  public static void sayHello() {\n" //
				+ "    System.out.println(\"Hello World\");\n" //
				+ "  }\n" //
				+ "  public static int add(int a, int b) {\n" //
				+ "    return a + b;\n" //
				+ "  }\n" //
				+ "}\n";
	}
	public Method getSelectedMethod() {
		return (Method) methods.getSelectedItem();
	}
	public String getSource() {
		return source.getText();
	}
	private Object[] getValues(final String methodName, final Class<?>[] types) {
		final Object[] values = new Object[types.length];
		for (int i = 0; i < values.length; i++) {
			final String answer = JOptionPane.showInputDialog(null,
					"Argument " + i + " (" + types[i].getSimpleName() + ")", methodName, JOptionPane.QUESTION_MESSAGE);
			values[i] = toObject(types[i], answer);
		}
		return values;
	}
	public Object[] promptForArguments() {
		final Method method = getSelectedMethod();
		final Class<?>[] types = method.getParameterTypes();
		return getValues(method.getName(), types);
	}
	public void setClassBytes(final byte[] bytes) {
		this.classBytes = bytes;
	}
	private Object toObject(final Class<?> type, final String value) {
		if (type == Integer.TYPE || type == Integer.class //
				|| type == Short.TYPE || type == Short.class //
				|| type == Byte.TYPE || type == Byte.class //
				|| type == Character.TYPE || type == Character.class) {
			return Integer.valueOf(value);
		} else if (type == Boolean.TYPE || type == Boolean.class) {
			return Boolean.valueOf(value);
		} else if (type == Long.TYPE || type == Long.class) {
			return Long.valueOf(value);
		} else if (type == Float.TYPE || type == Float.class) {
			return Float.valueOf(value);
		} else if (type == Double.TYPE || type == Double.class) {
			return Double.valueOf(value);
		} else {
			return value;
		}
	}
}
