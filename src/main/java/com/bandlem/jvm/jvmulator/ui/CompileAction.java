package com.bandlem.jvm.jvmulator.ui;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import com.bandlem.jvm.jvmulator.compiler.JavaC;
import com.bandlem.jvm.jvmulator.compiler.SourceFile;
class CompileAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final GUI gui;
	CompileAction(final GUI gui) {
		super("Compile");
		this.gui = gui;
	}
	@Override
	public void actionPerformed(final ActionEvent event) {
		gui.clearMethods();
		final JavaC compiler = new JavaC();
		final boolean success = compiler.compile(new SourceFile("Example", gui.getSource()));
		final List<Diagnostic<? extends JavaFileObject>> diagnostics = compiler.getDiagnostics();
		if (!success || !diagnostics.isEmpty()) {
			final List<String> messages = diagnostics.stream().map(Object::toString).collect(Collectors.toList());
			JOptionPane.showMessageDialog(gui, String.join("\n", messages), "Compiler Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				final Class<?> exampleClass = compiler.newClassLoader().loadClass("Example");
				for (final var method : exampleClass.getMethods()) {
					gui.addMethod(method);
				}
				gui.setClassBytes(compiler.getBytes("Example"));
			} catch (final ClassNotFoundException e) {
				JOptionPane.showMessageDialog(gui, "Unable to load class", "Compiler Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
