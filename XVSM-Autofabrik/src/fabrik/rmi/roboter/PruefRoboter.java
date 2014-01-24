package fabrik.rmi.roboter;

import java.io.IOException;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import fabrik.rmi.Config;
import fabrik.rmi.ContainerNames;

/**
 * Die Pr\u00FCfroboter \u00FCberpr\u00FCfen die fertigen Autos. Jeder
 * Pr\u00FCfroboter ist f\u00FCr eine bestimmte Art von \u00DCberpr\u00FCfung
 * programmiert worden. Ein Auto muss vor der Auslieferung folgende Tests
 * bestehen: Gewichtstest und Komponentenprobe. Beim Gewichtstest m\u00FCssen
 * Sie keine Logik implementieren. Es reicht, wenn der Test immer positiv ist.
 * Bei der Komponentenprobe wird nach defekten Teilen gesucht. Falls ein Auto
 * ein oder mehrere defekte Teile hat, wird das ganze Auto als defekt markiert.
 * Die Tests k\u00F6nnen in verschiedenen Reihenfolgen durchgef\u00FChrt werden,
 * abh\u00E4ngig davon, welcher Roboter nichts zu tun hat, allerdings nie
 * gleichzeitig an einem Auto. Welche Messung ein Roboter durchf\u00FChren kann,
 * wird beim Start angegeben.
 * 
 * @author Michael Borko
 * 
 */
public class PruefRoboter extends Thread {

	private Capi capi;
	private MzsCore core;
	private long id;

	public PruefRoboter(long pruefID) {
		System.out.println();
		System.out.println("Pruefroboter meldet sich zum Dienst");
		System.out.println();
		this.id = pruefID;
	}

	public void xvsmShutdown() {
		core.shutdown(true);
	}
	/**
	 * Erstellt die Bedingung fuer notifications und den entsprechenden Listener
	 */
	public void run() {
		ContainerReference fertigContainer = null;
		try {
			/**
			 * Erstellt den Core ohne Space und erzeugt das CAPI fuer den Zugriff
			 */
			core = DefaultMzsCore.newInstanceWithoutSpace();
			capi = new Capi(core);
			TransactionReference trans = capi.createTransaction(MzsConstants.TransactionTimeout.INFINITE, Config.locAutos);
			fertigContainer = capi.lookupContainer(ContainerNames.FERTIG, Config.locAutos, 0, trans);
			capi.commitTransaction(trans);
		} catch (MzsCoreException e) {
		}
		NotificationManager nm = new NotificationManager(core);
		try {
			nm.createNotification(fertigContainer, new PruefListener(capi,id), Operation.WRITE);
		} catch (MzsCoreException e1) {
		} catch (InterruptedException e1) {
		}
	}

	public static void main(String[] args) {
		long pruefID = -1;
		try {
			pruefID = Long.parseLong(args[0]);
		} catch (ArrayIndexOutOfBoundsException ae) {

		}
		PruefRoboter prfRoboter;
		prfRoboter= new PruefRoboter(pruefID);
		prfRoboter.start();

		try {
			System.in.read();
			prfRoboter.xvsmShutdown();
			System.out.println("PruefRoboter heruntergefahren");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
