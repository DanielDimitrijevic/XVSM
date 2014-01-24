package fabrik.rmi.roboter;

import java.io.IOException;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.aspects.AspectReference;
import org.mozartspaces.core.aspects.SpaceIPoint;

import fabrik.rmi.Config;

/**
 * Montageroboter bauen die Autos zusammen, sobald alle Teile verf\u00FCgbar
 * sind. Fertige Autos unterscheiden sich durch ihre eindeutige ID. Die
 * Montageroboter arbeiten unabh\u00E4ngig voneinander und d\u00FCrfen sich auf
 * keinen Fall gegenseitig behindern, d.h. ein Montageroboter darf z.B. nicht
 * den letzten Sitz f\u00FCr sich beanspruchen und ein anderer die letzte
 * Karosserie, wodurch beide kein fertiges Auto herstellen k\u00F6nnen, obwohl
 * eigentlich noch genug Teile f\u00FCr ein Auto vorhanden w\u00E4ren.
 * 
 * @author Michael Borko
 * 
 */
public class MontageRoboter extends Thread {

	private long id;
	private Capi capi;
	private MzsCore core;
	private AspectReference ar;

	public MontageRoboter(long pruefID) {
		System.out.println();
		System.out.println("Montageroboter meldet sich zum Dienst");
		System.out.println();	
		this.id = pruefID;
		
		
		
	}
	

	public void run() {
		try{
			/*
			 * Erstellt den Core ohne Space und erzeugt das CAPI fuer den Zugriff
			 * Ausserdem wird im Anschluss der Aspekt hinzugefuegt
			 */
			core = DefaultMzsCore.newInstanceWithoutSpace();
			capi = new Capi(core);
			ar = capi.addSpaceAspect(new MontageAspect(id), Config.locET, SpaceIPoint.POST_WRITE);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			System.err.println("Space wurde nicht aufgesetzt!");
			System.exit(0);
		}
	}
	public void xvsmShutdown(){
		try {
			capi.removeAspect(ar);
			capi.shutdown(ar.getSpace());
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		


	public static void main(String[] args) {
		long pruefID = -1;
		try {
			pruefID = Long.parseLong(args[0]);
		} catch (ArrayIndexOutOfBoundsException ae) {

		}
		try{
			MontageRoboter monteur = new MontageRoboter(pruefID);
			monteur.start();
			System.in.read();
			System.out.println("Roboter wird zerstoert!");
			monteur.xvsmShutdown();
			monteur.interrupt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
