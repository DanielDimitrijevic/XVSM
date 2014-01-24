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

import fabrik.rmi.Config;
import fabrik.rmi.ContainerNames;

import autoKonfiguration.Auto;

public class PruefListener implements NotificationListener {
	private Capi capi;
	private long id;
	/**
	 * Konstruktor, der beim Start aufgerufen wird
	 * @param capi Uebergebenes CAPI
	 * @param id ID des Roboters
	 */
	public PruefListener(Capi capi, long id) {
		this.capi = capi;
		this.id = id;
		autosPruefen();
	}

	@Override
	/**
	 * Wenn ein Entry in den Space geschrieben wurde, wird die Logik ausgefuehrt
	 */
	public void entryOperationFinished(Notification arg0, Operation arg1, List<? extends Serializable> arg2) {
		System.err.println("OperationFinished0");
		autosPruefen();
		// TODO Auto-generated method stub
	}
	
	/**
	 * Diese Methode prueft die Autos auf Defektheit. Hierzu werden zwei separate Methoden
	 * aufgerufen, die von ihrem Grundaufbau in der Hinsicht ueberarbeitet wurden, dass dies nun ein
	 * einzelner Roboter erledigt um die Codeteile auf den Listener auslagern zu koennen.
	 */
	public void autosPruefen() {
		ContainerReference fertigContainer = null;
		ContainerReference geprueftContainer = null;
		ContainerReference idContainer = null;

		try {
			TransactionReference trans = capi.createTransaction(MzsConstants.TransactionTimeout.INFINITE,
					Config.locAutos);
			fertigContainer = capi.lookupContainer(ContainerNames.FERTIG, Config.locAutos,
					MzsConstants.RequestTimeout.ZERO, trans);
			geprueftContainer = capi.lookupContainer(ContainerNames.GEPRUEFT, Config.locAutos,
					MzsConstants.RequestTimeout.ZERO, trans);
			idContainer = capi.lookupContainer(ContainerNames.ID, Config.locAutos, MzsConstants.RequestTimeout.ZERO,
					trans);
			
			/*
			 * Speichert die zu pruefenden Autos und die bereits vergebenen IDs
			 */
			ArrayList<Auto> zuPruefen = capi.take(fertigContainer,
					FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
					MzsConstants.RequestTimeout.TRY_ONCE, trans);
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
			 * Fuer jedes Auto, dass im Space stand, wird geprueft ob die Einzelteile und das Gewicht
			 * in Ordnung sind und dananch in den Space geschrieben.
			 */
			for (Auto pruefen : zuPruefen) {
				
				pruefen.setPrueferDefekteID(this.id);
				pruefen.setPrueferGewichtID(this.id);
				if (!pruefen.isDefekt()){
					pruefen.setDefekt(!(sindTeileOK(pruefen)&&istGewichtOK(pruefen)));
				}



				capi.write(geprueftContainer, new Entry(pruefen));
				capi.write(idContainer, new Entry(this.id));

			}
			capi.commitTransaction(trans);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			System.exit(0);
		}
	}

	/**
	 * Gewichtpruefung eines Autos, immer true
	 * @param auto 
	 * @return true
	 */
	private boolean istGewichtOK(Auto auto) {

		return true;
	}

	/**
	 * Einzelteile eines Autos werden hier geprueft
	 * @param auto
	 * @return Ob alle Teile funktionstuechtig sind
	 */
	private boolean sindTeileOK(Auto auto) {

		if (auto.isDefekt())
			return false;

		boolean defekt = false;

		if (auto.getAchseVorn().istDefekt())
			defekt = true;
		if (auto.getAchseHinten().istDefekt())
			defekt = true;
		if (auto.getReifenPaarVorn().istDefekt())
			defekt = true;
		if (auto.getReifenPaarHinten().istDefekt())
			defekt = true;
		if (auto.getBodenplatte().istDefekt())
			defekt = true;
		if (auto.getSitz().istDefekt())
			defekt = true;
		if (auto.getKarosserie().istDefekt())
			defekt = true;
		if (auto.getLenkrad().istDefekt())
			defekt = true;

		if (defekt) {
			auto.setDefekt(true);
			return false;
		}

		return true;
	}

}
