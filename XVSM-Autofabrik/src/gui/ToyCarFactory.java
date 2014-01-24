package gui;

import javax.swing.JOptionPane;

import org.eclipse.swt.widgets.Display;

import fabrik.IFactory;
import fabrik.rmi.roboter.XvsmSetup;

/**
 * Die Hauptklasse aus der man die Applikation starten kann. Durch das
 * factory-Pattern kann die Implementierung einfach ausgetauscht werden, z.B.
 * durch eine Space-Based-Computing Implementierung.
 * 
 * @author Michael Borko
 * @version 20110516rev2
 * 
 */
public class ToyCarFactory {

	/**
	 * @see org.eclipse.swt.widgets.Display
	 */
	private Display display;
	/**
	 * @see fabrik.IFactory
	 */
	static IFactory factory;

	/**
	 * Der Default-Konstruktor erstellt ein neues SWT-Display.
	 */
	public ToyCarFactory() {
		this.display = new Display();
	}

	/**
	 * Es wird ein neues Objekt der Klasse ToyCarFactory instanziert.
	 * Anschlie\u00dfend wird das factory Attribut mit einer Instanz der
	 * entsprechenden Implementierung gesetzt. In dem folgenden Fall wird die
	 * RMI Implementierung instanziert.
	 * 
	 * @param args
	 *            Keine Verwendung der Argumente vorgesehen.
	 */
	public static void main(String args[]) {
		ToyCarFactory application = new ToyCarFactory();
		factory = fabrik.rmi.Fabrik.getInstance();
		application.launchGUI();
	}

	private void launchGUI() {
		new MainDisplay(display).create();
	}
}
