package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MessageBoxCreator {

	public static void createMessageOk(Shell shell, int cat, String title,
			String text) {
		MessageBox messageBox = new MessageBox(shell, cat | SWT.OK);
		messageBox.setMessage(text);
		messageBox.setText(title);
		messageBox.open();
	}

	public static int createMessageQuestion(Shell shell, String title,
			String text) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
				| SWT.YES | SWT.NO);
		messageBox.setMessage(text);
		messageBox.setText(title);
		return messageBox.open();
	}

	public static void closeToyCarFactory(Shell shell) {
		final MessageBox messageBox = new MessageBox(shell, SWT.END
				| SWT.APPLICATION_MODAL | SWT.YES | SWT.NO | SWT.ARROW_DOWN);
		messageBox.setText("Information");
		messageBox.setMessage("Do you really want to quit CarFactory?");
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent arg0) {
				if (messageBox.open() == SWT.NO) {
					arg0.doit = false;
				} else {
					ToyCarFactory.factory.shutdown();
				}
			}
		});
	}
}
