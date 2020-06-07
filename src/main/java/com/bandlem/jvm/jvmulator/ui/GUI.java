package com.bandlem.jvm.jvmulator.ui;
import java.awt.Font;
import java.lang.reflect.Method;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
public class GUI extends JPanel {
	private static final long serialVersionUID = 1L;
	public static void main(final String[] args) {
		final JFrame frame = new JFrame("JVMulator");
		frame.add(new GUI());
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	private final Action compile = new CompileAction(this);
	private final JTextArea console = new JTextArea("", 20, 20);
	private final Action invoke = new InvokeAction(this);
	private final JComboBox<Method> methods = new JComboBox<>();
	private final OutTextArea out = new OutTextArea(console, System.out);
	private final JTextArea source = new JTextArea(getExample(), 20, 20);
	public GUI() {
		final Font monospaced = new Font(Font.MONOSPACED, Font.PLAIN, 24);
		if (monospaced != null) {
			source.setFont(monospaced);
		}
		add(source);
		add(new JButton(compile));
		add(methods);
		add(new JButton(invoke));
		add(console);
		System.setOut(out);
	}
	public void addMethod(final Method method) {
		methods.addItem(method);
		enableButtons(true);
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
	}
	private String getExample() {
		return "public class Example {\n" //
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
}
