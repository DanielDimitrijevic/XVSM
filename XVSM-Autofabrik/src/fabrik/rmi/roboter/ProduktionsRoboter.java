package fabrik.rmi.roboter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;

import autoKonfiguration.Einzelteil;
import fabrik.rmi.Config;
import fabrik.rmi.ContainerNames;

/**
 * Die Einzelteile werden von spezialisierten Produktionsroboter erzeugt, d.h.
 * jeder Roboter erzeugt entweder Bodenplatten, Karosserien, Reifen, Achsen,
 * Sitze oder Lenkr\u00E4der. Produktionsroboter sind Akkordarbeiter. Das heißt,
 * sie produzieren die ihnen vorgegebene Anzahl an Teilen und haben dann ihre
 * Arbeit erledigt. Sollen noch mehr Teile produziert werden, muss man einen
 * neuen Roboter damit beauftragen. Auch Roboter machen ab und zu Fehler und
 * erstellen defekte Teile. Die Produktion jedes Teiles soll eine gewisse Zeit
 * dauern (ein Zufallswert von 1-3 Sekunden reicht).
 * 
 * @author Michael Borko
 * 
 */
public class ProduktionsRoboter extends Thread {

	private Class<? extends Einzelteil> type;

	private long id;
	private int anzahl;
	private double fehlerrate;
	
	private Capi capi;

	@SuppressWarnings("unchecked")
	public ProduktionsRoboter(int anzahl, double fehlerrate, String type) {
		try {
			this.type = (Class<? extends Einzelteil>) Class.forName(type);
		} catch (ClassNotFoundException e) {
			System.err.println("Houston, we have a problem ...");
		}

		System.out.println();
		System.out.println("Produktionsroboter fuer " + "<" + type + ">"
				+ " meldet sich zum Dienst");
		System.out.println();

		this.anzahl = anzahl;
		this.fehlerrate = fehlerrate;



	}
	/**
	 * Thread der die gewuenschten Einzelteile erzeug/in den Space schreibt
	 */
	public void run() {
		MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
		capi = new Capi(core);
		int anzahlDefekte = (int) (anzahl * fehlerrate);
		TransactionReference transET = null;
		TransactionReference transAutos = null;
		ContainerReference idContainer = null;
		ArrayList<Long> ids = null;
		try {
			transAutos = capi.createTransaction(MzsConstants.TransactionTimeout.INFINITE, Config.locAutos);
			transET = capi.createTransaction(MzsConstants.TransactionTimeout.INFINITE, Config.locET);
			idContainer = capi.lookupContainer(ContainerNames.ID, Config.locAutos, MzsConstants.RequestTimeout.TRY_ONCE, transAutos);
			ids = capi.read(idContainer,
					FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
					MzsConstants.RequestTimeout.ZERO, transAutos);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
			this.id = ids.get(ids.size()-1);
		}catch(Exception e){
			this.id = 1;
			e.printStackTrace();
		}
		long entryID = id;
		try {
			capi.write(capi.lookupContainer(ContainerNames.ID,Config.locAutos,MzsConstants.RequestTimeout.TRY_ONCE,transAutos), new Entry(id));
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int zaehler = 1; zaehler <= anzahl; zaehler++) {
			// Warte 1-3 Sekunden

			// Bastel neues Teil
			entryID++;
			boolean defekt = false;
			if (zaehler <= anzahlDefekte)
				defekt = true;
				

			System.err.println("Got component id: " + entryID);


			Einzelteil teil = null;

			// Erstellung eines neuen Objekts von einem generischen Datentypen
			try {
				@SuppressWarnings("rawtypes")
				Class[] argsC = new Class[] { long.class, long.class,
						boolean.class };
				Constructor<? extends Einzelteil> constructor = type
						.getConstructor(argsC);
	
				teil = constructor.newInstance(new Object[] { entryID, id,
						defekt });
			} catch (Exception e) {
				System.err.println("Houston, we have a problem ...");
			}

			System.err.println("Created new " + type.getName());
			String containerName = "";
			if (type.getName().contains("einzelteile.Achse")) {
				containerName = ContainerNames.ACHSEN;
			} else if (type.getName().contains("einzelteile.Bodenplatte")) {
				containerName = ContainerNames.PLATTEN;
			} else if (type.getName().contains("einzelteile.Karosserie")) {
				containerName = ContainerNames.KAROSSERIE;
			} else if (type.getName().contains("einzelteile.Lenkrad")) {
				containerName = ContainerNames.LENKRAD;
			} else if (type.getName().contains("einzelteile.ReifenPaar")){
				containerName = ContainerNames.REIFEN;
			} else if (type.getName().contains("einzelteile.Sitz")){
				containerName = ContainerNames.SITZE;
			}
			try {
				capi.write(capi.lookupContainer(containerName,Config.locET,MzsConstants.RequestTimeout.TRY_ONCE,transET), new Entry(teil));
				capi.write(capi.lookupContainer(ContainerNames.ID,Config.locAutos,MzsConstants.RequestTimeout.TRY_ONCE,transAutos), new Entry(entryID));
			} catch (MzsCoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			capi.commitTransaction(transET);
			capi.commitTransaction(transAutos);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
