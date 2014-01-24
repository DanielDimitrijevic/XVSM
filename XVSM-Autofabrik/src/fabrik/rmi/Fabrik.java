package fabrik.rmi;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.eclipse.swt.widgets.Event;
import org.mozartspaces.capi3.CountNotMetException;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.MzsCoreRuntimeException;
import org.mozartspaces.core.RequestContext;
import org.mozartspaces.core.TransactionReference;

import autoKonfiguration.Auto;
import autoKonfiguration.Einzelteil;
import fabrik.IFactory;
import fabrik.rmi.roboter.ProduktionsRoboter;

/**
 * Implementierung der RMI ToyCarFactory. Die Klasse wird als Singleton
 * instanziert und startet auch alle notwendigen RMI Services um den Robotern
 * eine gemeinsame Arbeitsumgebung zu erm\u00F6glichen.
 * 
 * @author Michael Borko
 * @see fabrik.IFactory
 */
public class Fabrik implements IFactory {

	private HashMap<String, LinkedList<Einzelteil>> produktion = new HashMap<String, LinkedList<Einzelteil>>();
	private HashMap<String, Auto> montage = new HashMap<String, Auto>();
	private HashMap<String, Auto> geprueft = new HashMap<String, Auto>();
	private HashMap<String, Auto> fertig = new HashMap<String, Auto>();
	private HashMap<String, Auto> sammelstelle = new HashMap<String, Auto>();

	private Long counter = new Long(1);
	private static Fabrik instance = null;

	private static MzsCore mozad = null;
	private static Capi capi = null;
	private static ContainerReference container = null;

	/**
	 * Initialisierung der Fabrik-Instanz durch Aufruf der getInstance()
	 * Methode.
	 * 
	 * @param args
	 *            Keine Verwendung vorgesehen
	 */
	public static void main(String args[]) {
		getInstance();
		// getInstance().shutdown();
	}

	/**
	 * Fabrik gehorcht dem Singleton-Pattern um zu vermeiden, dass mehrer
	 * Objekte dieser Klasse implementiert werden. Somit ist gesichert, dass
	 * nicht mehrere Registries innerhalb der Applikation erzeugt werden.
	 * 
	 * @return Singleton-Instanz der Klasse Fabrik
	 */
	public static Fabrik getInstance() {
		if (instance == null) {
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}

			instance = new Fabrik();

		}
		return instance;
	}

	private Fabrik() {
		xvsmSetup();
	}

	public void xvsmSetup() {

		MzsCore core = null;
		try {
			core = DefaultMzsCore.newInstance();
		} catch (MzsCoreRuntimeException me) {
			me.printStackTrace();
		}
		capi = new Capi(core);

	}

	/**
	 * @see fabrik.IFactory#startProduction(int, int, String)
	 */
	@Override
	public void startProduction(int quantity, int err, String type) {
		// TODO MUST :: Einfaches Starten von Produktionsroboter
		// <REMOVE>
		new ProduktionsRoboter(quantity, (double) err / 100, type).start();
		// </REMOVE>
		// TODO NICE :: Optimierungsmoeglichkeit mittels Threadpool?
	}

	/**
	 * @see fabrik.IFactory#getQuantity(String)
	 */
	@Override
	public int getQuantity(String type) {
		String containerName = "";
		// TODO NEED :: Abrufen der Anzahl eines Einzelteils
		// <REMOVE>
		if (type.equals("einzelteile.Achse")) {
			containerName = ContainerNames.ACHSEN;
		} else if (type.equals("einzelteile.Bodenplatte")) {
			containerName = ContainerNames.PLATTEN;
		} else if (type.equals("einzelteile.Karosserie")) {
			containerName = ContainerNames.KAROSSERIE;
		} else if (type.equals("einzelteile.Lenkrad")) {
			containerName = ContainerNames.LENKRAD;
		} else if (type.equals("einzelteile.ReifenPaar")) {
			containerName = ContainerNames.REIFEN;
		} else if (type.equals("einzelteile.Sitz")) {
			containerName = ContainerNames.SITZE;
		}
		try {
			/*
			 * Da die Anzahl von dem Einzelteil-Typ zurueckzugeben ist, wird
			 * ueber die test-Methode gecheckt, wieviele Stueck des Einzelteiles
			 * sich im Space befinden und zurueckgegeben.
			 */
			int num = 0;
			RequestContext context = new RequestContext();
			TransactionReference trans = capi.createTransaction(
					MzsConstants.TransactionTimeout.INFINITE, Config.locET);
			ContainerReference einzelteil = capi.lookupContainer(containerName,
					Config.locET, MzsConstants.RequestTimeout.TRY_ONCE, trans);
			num = capi.test(einzelteil, FifoCoordinator
					.newSelector(MzsConstants.Selecting.COUNT_ALL),
					MzsConstants.RequestTimeout.TRY_ONCE, trans);
			capi.commitTransaction(trans);
			
			return num;
		} catch (CountNotMetException ce) {
			ce.printStackTrace();
		} catch (MzsCoreException e) {
			e.printStackTrace();
			System.exit(0);
		}

		return 0;

		// </REMOVE>
	}

	/**
	 * @see fabrik.IFactory#getDeliveredCars()
	 */
	@Override
	public String[] getDeliveredCars() {
		ArrayList<String> ret = new ArrayList<String>();

		// TODO NEED :: Abrufen der fertigen Autos
		// <REMOVE>
		try {
			TransactionReference trans = capi.createTransaction(10000,
					Config.locAutos);
			ContainerReference ausliefern = capi.lookupContainer(
					ContainerNames.AUSLIEFERN, Config.locAutos,
					MzsConstants.RequestTimeout.ZERO, trans);
			ArrayList<Auto> autos = capi.take(ausliefern, FifoCoordinator
					.newSelector(MzsConstants.Selecting.COUNT_ALL),
					MzsConstants.RequestTimeout.ZERO, trans);
			capi.commitTransaction(trans);
			for (Auto auto : autos) {
				ret.add(auto.toString());
			}
		} catch (CountNotMetException ce) {
			ce.printStackTrace();
		} catch (MzsCoreException e) {
			e.printStackTrace();
		}
		return ret.toArray(new String[ret.size()]);
	}

	/**
	 * @see fabrik.IFactory#getFaultyCars()
	 */
	@Override
	public String[] getFaultyCars() {
		ArrayList<String> ret = new ArrayList<String>();

		try {
			TransactionReference trans = capi.createTransaction(10000, Config.locAutos);
			ContainerReference sammelstelle = capi.lookupContainer(ContainerNames.SAMMELSTELLE,Config.locAutos,0,trans);
			ArrayList<Auto> autos = capi.take(sammelstelle, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL), 0, trans);
			for(Auto auto : autos ){
				ret.add(auto.toString());
			}
			capi.commitTransaction(trans);
		} catch (CountNotMetException ce) {
			ce.printStackTrace();
		} catch (MzsCoreException e) {
			e.printStackTrace();
		}

		return ret.toArray(new String[ret.size()]);
	}

	/**
	 * @see fabrik.IFactory#shutdown()
	 */
	@Override
	public void shutdown() {
		try {
			System.out.println("Server shutdown ...");
			mozad.shutdown(true);
		} catch (Exception e) {
			System.err.println("Can not close Server." + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages. <a
	 * href="http://snippets.dzone.com/posts/show/4831">snippets.dzone</a>
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
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
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
				ret.add(className.getName());
			}
			return ret.toArray(new String[ret.size()]);
		} catch (Exception ex) {
			return null;
		}

	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs. <a
	 * href="http://snippets.dzone.com/posts/show/4831">snippets.dzone</a>
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return the classes
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}
}
