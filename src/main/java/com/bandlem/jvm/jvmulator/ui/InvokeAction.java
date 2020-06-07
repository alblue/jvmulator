package com.bandlem.jvm.jvmulator.ui;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
class InvokeAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final GUI gui;
	InvokeAction(final GUI gui) {
		super("Invoke");
		setEnabled(false);
		this.gui = gui;
	}
	@Override
	public void actionPerformed(final ActionEvent event) {
		try {
			gui.clearConsole();
			final Method method = gui.getSelectedMethod();
			final Object result = method.invoke(null);
			if (result != null)
				JOptionPane.showMessageDialog(gui, result.toString(), "Result", JOptionPane.INFORMATION_MESSAGE);
		} catch (final Exception e) {
			JOptionPane.showMessageDialog(gui, e.getMessage(), "Unable to invoke method", JOptionPane.ERROR_MESSAGE);
		}
	}
}
