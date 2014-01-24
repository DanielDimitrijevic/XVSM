package fabrik.rmi.roboter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;

import autoKonfiguration.Auto;
import fabrik.rmi.Config;
import fabrik.rmi.ContainerNames;
/**
 * Mozartspaces Listener fuer logistik aufgaben
 * @author 
 *
 */
public class LogistikListener implements NotificationListener{

	private Capi capi;
	private long id;
	/**
	 * Konstruktor, der beim Start aufgerufen wird
	 * @param capi Uebergebenes CAPI
	 * @param id ID des Roboters
	 */
	public LogistikListener(Capi capi, long id) {
		this.capi = capi;
		this.id = id;
		autosAusliefern();
		// TODO Auto-generated constructor stub
	}

	@Override
	/**
	 * Wird aufgerufen, wenn ein fertiges Auto in den Space geschrieben wurde
	 */
	public void entryOperationFinished(Notification source, Operation operation,
			List<? extends Serializable> entries) {
		autosAusliefern();
		// TODO Auto-generated method stub
		
	}
	/**
	 * Liefert das Auto schlussendlich aus.
	 */
	public void autosAusliefern(){
		ContainerReference geprueft,sammelstelle,ausliefern,idContainer;
		
		
		try {
			TransactionReference trans = capi.createTransaction(MzsConstants.TransactionTimeout.INFINITE, Config.locAutos);
			/*
			 * Lookup der benoetigten Container um die Autos entsprechend auszuliefern.
			 * Die ID benoetigt auch einen Lookup, da ueber diesen die eigene RoboterID bestimmt wird.
			 */
			geprueft = capi.lookupContainer(ContainerNames.GEPRUEFT, Config.locAutos, MzsConstants.RequestTimeout.ZERO, trans);
			sammelstelle = capi.lookupContainer(ContainerNames.SAMMELSTELLE, Config.locAutos, MzsConstants.RequestTimeout.ZERO, trans);
			ausliefern = capi.lookupContainer(ContainerNames.AUSLIEFERN, Config.locAutos, MzsConstants.RequestTimeout.ZERO, trans);
			idContainer = capi.lookupContainer(ContainerNames.ID, Config.locAutos, MzsConstants.RequestTimeout.ZERO, trans);
			/*
			 * Speichert die auszuliefernden Autos
			 */
			ArrayList<Auto> auszuliefern = capi.take(geprueft, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.ZERO, trans);
			
			ArrayList<Long> ids = capi.read(idContainer,
					FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
					MzsConstants.RequestTimeout.ZERO, trans);
			/*
			 * Dieser komplette if-else Block hat die Aufgabe, die ID des Roboters zu bestimmen.
			 * Hierzu wurden bereits die IDs aus dem Space gelesen. Wenn diese IDs leer waren, bedeutet
			 * das, dass dieser Roboter den ersten Schreibvorgang uebernimmt. Wenn dem nicht so ist, wird
			 * geprueft, ob die uebergebene ID des Benutzers verfuegbar ist und diese benutzt. Wenn sie nicht
			 * verfuegbar ist, wird die hoechste im Space gelesen und um 1 erhoeht.
			 */
			if (ids.size() != 0) {
				for (long currentId : ids) {
					if (currentId == this.id)
						this.id = -1;
				}
				if (this.id == -1)
					this.id = (Long) capi.read(idContainer,
							FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
							MzsConstants.RequestTimeout.ZERO, trans).get(ids.size()-1) + 1;
			}else{
				this.id = 1;
			}
			
			/*
			 * Fuer jedes der ausgelesenen Autos wird gecheckt, ob diese defekt sind und dann werden
			 * sie in den entsprechenden Container geschrieben.
			 */
			for(Auto liefern:auszuliefern){
				liefern.setLieferantID(this.id);
				if(liefern.isDefekt()){
					capi.write(sammelstelle, MzsConstants.RequestTimeout.ZERO, trans, new Entry(liefern));
				}else{
					capi.write(ausliefern, MzsConstants.RequestTimeout.ZERO, trans, new Entry(liefern));
				}
				
			}
		capi.commitTransaction(trans);
			
		} catch (MzsCoreException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

}
