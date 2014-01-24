package gui;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * GUI f\u00FCr eine Autofabrik, mit welchem Produktionroboter erzeugt und
 * aktiviert werden k\u00F6nnen, die Einzelteile produzieren. Jeder Roboter hat
 * eine benutzerdefinierte ID und ist f\u00FCr Herstellung eines spezifischen
 * Teils zust\u00E4ndig. F\u00FCr jeden Roboter soll festgelegt werden, wie
 * viele St\u00FCck er produzieren soll und welche Fehlerrate er hat.
 * Au\u00DFerdem soll das GUI eine Informationstafel f\u00FCr den
 * Fabriksbesitzer enthalten.
 * <p>
 * <ul>
 * <li>Erstellung von Produktionsrobotern (Benennung und Auswahl des
 * Produktionsteils, Menge und Fehlerrate)
 * <li>Es sollen beliebig viele Produktionsroboter gleichzeitig gestartet werden
 * k\u00F6nnen. (Implementierung als Threads)
 * <li>Automatisches Auslesen der ausgelieferten Autos. Dabei soll die gesamte
 * Information des Autos angezeigt werden (Einzelteile, beteiligte Roboter,
 * usw.).
 * <li>Automatisches Auslesen von defekten Autos. Die gesamte Information des
 * Autos sollte angezeigt werden. Es muss ersichtlich sein, welche Teile defekt
 * sind.
 * <li>Anzeige der Anzahl aller Einzelteile in der Fabrik.
 * </ul>
 * <p>
 * Das GUI soll nur die Produzenten starten und die aktuellen Daten \u00FCber
 * die Fabrik auf der Anzeigetafel darstellen. Das eigentliche Erstellen der
 * Autos wird durch eigenst\u00E4ndige Programme, f\u00FCr die kein GUI
 * notwendig ist, durchgef\u00FChrt.
 * 
 * @author Michael Borko
 * 
 */
public class MainDisplay {

	private Display display;
	private Shell shell;
	private MainMenu mainMenu;
	private Composite cproduction, cdelivered, cfaulty;
	private Group gproduction;
	private Table componentsTable, deliveredTable, faultyTable;

	static String[] componentsTableColumnTitles = { "Component\t\t", "quantity\t" };
	static String[] deliveredTableColumnTitles = { "ID\t", "achse#1\t", "achse#2\t", "reifenPaar#1\t",
			"reifenPaar#2\t", "bodenplatte\t", "sitz\t", "karosserie\t", "lenkrad\t", "produzentenIDs\t\t\t",
			"monteurID\t", "pruefIDs\t", "logistikID\t" };
	static String[] faultyTableColumnTitles = { "ID\t", "achse#1\t", "achse#2\t", "reifenPaar#1\t", "reifenPaar#2\t",
			"bodenplatte\t", "sitz\t", "karosserie\t", "lenkrad\t", "produzentenIDs\t\t\t", "monteurID\t",
			"pruefIDs\t", "logistikID\t" };

	public enum TableType {
		DELIVERED_TABLE, FAULTY_TABLE
	}

	public MainDisplay(Display display) {
		this.display = display;
		this.shell = new Shell(display);
	}

	public void refreshComponentsTable() {
		componentsTable.clearAll();
		componentsTable.setItemCount(0);

		for (String component : getClasses("einzelteile")) {
			String quantity = "0";

			// get produced quantity of components
			quantity = "" + ToyCarFactory.factory.getQuantity(component);

			TableItem item = new TableItem(componentsTable, SWT.NULL);
			String[] itemText = { component, quantity };
			item.setText(itemText);
		}
	}

	public void refreshDeliveredTable() {
		// deliveredTable.clearAll();
		// deliveredTable.setItemCount(0);

		String[] delivery = ToyCarFactory.factory.getDeliveredCars();
		// if (delivery.length == deliveredTableColumnTitles.length)
		for (String line : delivery) {
			TableItem item = new TableItem(deliveredTable, SWT.NULL);
			String[] itemText = line.split(" ");
			item.setText(itemText);
		}

	}

	public void refreshFaultyTable() {
		// faultyTable.clearAll();
		// faultyTable.setItemCount(0);

		String[] faulty = ToyCarFactory.factory.getFaultyCars();
		System.err.println("Faulty: " + faulty.length);
		// if (faulty.length == faultyTableColumnTitles.length)
		for (String line : faulty) {
			TableItem item = new TableItem(faultyTable, SWT.NULL);
			String[] itemText = line.split(" ");
			item.setText(itemText);

			Color red = display.getSystemColor(SWT.COLOR_RED);
			for (int i = 0; i < faultyTableColumnTitles.length; i++) {
				if (itemText[i].startsWith("#"))
					item.setForeground(i, red);
			}
		}

	}

	public Menu createMenuBar() {
		mainMenu = new MainMenu(shell);
		mainMenu.createMenuFile();
		mainMenu.createMenuHelp();
		mainMenu.addExit();
		return mainMenu.getMenu();
	}

	public void create() {
		shell.setText("ToyCarFactory");
		//shell.setImage(new Image(display, "pic/car.jpg"));
		shell.setLayout(new FillLayout());
		MessageBoxCreator.closeToyCarFactory(shell);
		createContent();

		shell.setMenuBar(createMenuBar());
		shell.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - shell.getSize().x / 2),
				(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - shell.getSize().y / 2));
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public void createContent() {

		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		shell.setLayout(fillLayout);

		// Production
		cproduction = new Composite(shell, SWT.NONE);
		FillLayout cproductionLayout = new FillLayout();
		cproductionLayout.type = SWT.HORIZONTAL;
		cproductionLayout.marginHeight = 10;
		cproductionLayout.marginWidth = 10;
		cproductionLayout.spacing = 10;
		cproduction.setLayout(cproductionLayout);

		// Production roboters
		buildProductionGroup(cproduction);

		// Car components
		buildComponentsTable(cproduction);

		// Delivered cars
		cdelivered = new Composite(shell, SWT.NONE);
		FillLayout cdeliveredLayout = new FillLayout();
		cdeliveredLayout.marginHeight = 10;
		cdeliveredLayout.marginWidth = 10;
		cdelivered.setLayout(cdeliveredLayout);
		cdelivered.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		buildDeliveredTable(cdelivered);

		// Faulty cars
		cfaulty = new Composite(shell, SWT.NONE);
		FillLayout cfaultyLayout = new FillLayout();
		cfaultyLayout.marginHeight = 10;
		cfaultyLayout.marginWidth = 10;
		cfaulty.setLayout(cfaultyLayout);
		cfaulty.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		buildFaultyTable(cfaulty);
		/*
		 * Dieser Part refresht alle Sekunden die Tables neu, da fuer die GUI kein XVSM
		 * implementiert wurde.
		 */
		display.timerExec(1000, new Runnable() {
			public void run() {
					refreshComponentsTable();
					refreshDeliveredTable();
					refreshFaultyTable();
					display.timerExec(1000, this);
			}
		});

	}

	private void buildProductionGroup(Composite cproduction) {

		gproduction = new Group(cproduction, SWT.NULL);
		GridLayout pGroupLayout = new GridLayout(2, true);
		pGroupLayout.marginLeft = 10;
		pGroupLayout.marginRight = 10;
		gproduction.setLayout(pGroupLayout);
		gproduction.setText("Production");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;

		// combo for entities
		Label lcomponents = new Label(gproduction, SWT.NONE);
		lcomponents.setText("Components:");
		final Combo comboComponents = new Combo(gproduction, SWT.DROP_DOWN | SWT.READ_ONLY);
		String[] items = getClasses("einzelteile");
		comboComponents.setItems(items);
		comboComponents.setText(items[0]);
		comboComponents.setLayoutData(gridData);

		// input for quantity
		Label lquantity = new Label(gproduction, SWT.NONE);
		lquantity.setText("Quantity:");
		final Text quantity = new Text(gproduction, SWT.BORDER | SWT.WRAP | SWT.FILL);
		quantity.setTextLimit(8);
		quantity.setText("0");
		quantity.setLayoutData(gridData);

		// scale error rate
		Group errScaleGroup = new Group(gproduction, SWT.NULL);
		errScaleGroup.setLayout(new GridLayout());
		errScaleGroup.setText("Error rate");
		errScaleGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// scale widget
		final Scale errScale = new Scale(errScaleGroup, SWT.NULL);
		errScale.setMaximum(100);
		errScale.setSelection(50);
		errScale.setIncrement(10);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 100;
		errScale.setLayoutData(data);

		// button "Produce"
		Button startProduction = new Button(gproduction, SWT.PUSH);
		startProduction.setText("Start Production Roboter");
		startProduction.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				// start the production
				System.out.println("Start production ... Component: " + comboComponents.getText() + " Quantity: "
						+ quantity.getText() + " ErrorRate: " + errScale.getSelection());
				if (new Integer(quantity.getText()).intValue() > 0) {
					ToyCarFactory.factory.startProduction(new Integer(quantity.getText()).intValue(),
							errScale.getSelection(), comboComponents.getText());
				}
			}
		});
		startProduction.setLayoutData(gridData);

	}

	private void buildComponentsTable(Composite cproduction) {

		Group componentsTableGroup = new Group(cproduction, SWT.NULL);
		componentsTableGroup.setLayout(new FillLayout());
		componentsTableGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL));
		componentsTableGroup.setText("List of produced car components");

		componentsTable = new Table(componentsTableGroup, SWT.SINGLE | SWT.BORDER);

		for (int i = 0; i < componentsTableColumnTitles.length; i++) {
			TableColumn tableColumn = new TableColumn(componentsTable, SWT.NULL);
			tableColumn.setText(componentsTableColumnTitles[i]);
		}

		refreshComponentsTable();

		for (int i = 0; i < componentsTableColumnTitles.length; i++) {
			TableColumn tableColumn = componentsTable.getColumn(i);
			tableColumn.pack();
		}

		componentsTable.setHeaderVisible(true);
		componentsTable.setLinesVisible(true);
	}

	private void buildDeliveredTable(Composite corganize) {

		Group orgTableGroup = new Group(corganize, SWT.NULL);
		orgTableGroup.setLayout(new FillLayout());
		orgTableGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL));
		orgTableGroup.setText("Delivered cars");

		deliveredTable = new Table(orgTableGroup, SWT.SINGLE | SWT.BORDER);

		for (int i = 0; i < deliveredTableColumnTitles.length; i++) {
			TableColumn tableColumn = new TableColumn(deliveredTable, SWT.NULL);
			tableColumn.setText(deliveredTableColumnTitles[i]);
		}

		refreshDeliveredTable();

		for (int i = 0; i < deliveredTableColumnTitles.length; i++) {
			TableColumn tableColumn = deliveredTable.getColumn(i);
			tableColumn.pack();
		}

		deliveredTable.setHeaderVisible(true);
		deliveredTable.setLinesVisible(true);
	}

	private void buildFaultyTable(Composite cparticipate) {

		Group parTableGroup = new Group(cparticipate, SWT.NULL);
		parTableGroup.setLayout(new FillLayout());
		parTableGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL));
		parTableGroup.setText("Faulty cars");

		faultyTable = new Table(parTableGroup, SWT.SINGLE | SWT.BORDER);

		for (int i = 0; i < faultyTableColumnTitles.length; i++) {
			TableColumn tableColumn = new TableColumn(faultyTable, SWT.NULL);
			tableColumn.setText(faultyTableColumnTitles[i]);
		}

		refreshFaultyTable();

		for (int i = 0; i < faultyTableColumnTitles.length; i++) {
			TableColumn tableColumn = faultyTable.getColumn(i);
			tableColumn.pack();
		}

		faultyTable.setHeaderVisible(true);
		faultyTable.setLinesVisible(true);
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * http://snippets.dzone.com/posts/show/4831
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static String[] getClasses(String packageName) {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
			ArrayList<Class> classes = new ArrayList<Class>();
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, packageName));
			}
			ArrayList<String> ret = new ArrayList<String>();
			for (Class className : classes) {
				if (!(className.getName().equals("einzelteile.Einzelteil") || className.getName().equals(
						"einzelteile.Auto")))
					ret.add(className.getName());
			}
			return ret.toArray(new String[ret.size()]);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs. http://snippets.dzone.com/posts/show/4831
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

}
