package fabrik.rmi.roboter;


import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import fabrik.rmi.Config;
import fabrik.rmi.ContainerNames;

/**
 * In der Logistik werden die fertigen Autos von Logistikroboter ausgeliefert (=
 * als fertig markiert), nachdem sie alle Tests bestanden haben. Defekte Autos
 * werden nicht transportiert, sondern enden an einer Sammelstelle.
 * 
 * @author Michael Borko
 */
public class LogistikRoboter extends Thread {

	private long id;
	private Capi capi;
	private MzsCore core;

	public LogistikRoboter(long logistikID) {
		System.out.println();
		System.out.println("LogistikRoboter meldet sich zum Dienst");
		System.out.println();
		this.id = logistikID;
	}
	/**
	 * Logistik Thread
	 */
	public void run() {
		ContainerReference geprueftContainer = null;
		try {
			/*
			 * Erstellt den Core ohne Space und erzeugt das CAPI fuer den Zugriff
			 */
			core = DefaultMzsCore.newInstanceWithoutSpace();
			capi = new Capi(core);
			geprueftContainer = capi.lookupContainer(ContainerNames.GEPRUEFT, Config.locAutos, MzsConstants.RequestTimeout.INFINITE, null);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			System.err.println("Space wurde nicht aufgesetzt!");
			System.exit(0);
		}
		NotificationManager nm = new NotificationManager(core);
		try {
			nm.createNotification(geprueftContainer, new LogistikListener(capi,id), Operation.WRITE);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public void xvsmShutdown(){
		core.shutdown(false);
	}

	public static void main(String[] args) {
		long logistikID = -1;
		try {
			logistikID = Long.parseLong(args[0]);
		} catch (ArrayIndexOutOfBoundsException ae) {

		}
		try {
			LogistikRoboter lieferant = new LogistikRoboter(logistikID);
			lieferant.start();
			System.in.read();
			lieferant.xvsmShutdown();
			System.exit(0);
			
		} catch (Exception e) {
			System.err.println("Registry not online!");
			System.exit(0);
		}
	}
}
