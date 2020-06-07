package com.bandlem.jvm.jvmulator.ui;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
class StepAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final JVMulator jvmulator;
	StepAction(final JVMulator jvmulator) {
		super("Step");
		this.jvmulator = jvmulator;
	}
	@Override
	public void actionPerformed(final ActionEvent event) {
		jvmulator.step();
	}
}
