package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MainMenu {

	private Shell shell;
	private Menu menu;
	private Menu filemenu, helpmenu, windowmenu;
	public MenuItem help, about, window, api;
	public MenuItem file, exit;
	private Listener listener;

	public MainMenu(Shell shell) {
		this.shell = shell;
		menu = new Menu(shell, SWT.BAR);
		setListener();
	}

	public Menu getMenu() {
		return menu;
	}

	public void createMenuFile() {
		file = new MenuItem(menu, SWT.CASCADE);
		file.setText("File");
		filemenu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(filemenu);
	}

	public void addExit() {
		exit = new MenuItem(filemenu, SWT.PUSH);
		exit.setText("Exit");
		exit.addListener(SWT.Selection, listener);
	}

	public void createMenuHelp() {
		help = new MenuItem(menu, SWT.CASCADE);
		help.setText("Help");
		helpmenu = new Menu(shell, SWT.DROP_DOWN);
		help.setMenu(helpmenu);
		about = new MenuItem(helpmenu, SWT.CASCADE);
		about.setText("About CarFactory");
		about.addListener(SWT.Selection, listener);
	}

	public void createMenuWindow() {
		window = new MenuItem(menu, SWT.CASCADE);
		window.setText("Window");
		windowmenu = new Menu(shell, SWT.DROP_DOWN);
		window.setMenu(windowmenu);
	}

	public void setListener() {
		listener = new Listener() {
			public void handleEvent(Event e) {
				if (e.widget == exit) {
					shell.close();
				} else if (e.widget == about) {
					MessageBoxCreator
							.createMessageOk(shell, SWT.ICON_INFORMATION,
									"About",
									"CarFactory Version 1.0\nAuthors: Michael Borko & Lisa Vittori");
				}
			}
		};
	}
}
