package fabrik.rmi.roboter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.mozartspaces.capi3.Capi3AspectPort;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.SubTransaction;
import org.mozartspaces.capi3.Transaction;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.MzsCoreRuntimeException;
import org.mozartspaces.core.TransactionReference;
import org.mozartspaces.core.aspects.AspectReference;
import org.mozartspaces.core.aspects.AspectResult;
import org.mozartspaces.core.aspects.SpaceAspect;
import org.mozartspaces.core.requests.AddAspectRequest;
import org.mozartspaces.core.requests.CommitTransactionRequest;
import org.mozartspaces.core.requests.CreateContainerRequest;
import org.mozartspaces.core.requests.CreateTransactionRequest;
import org.mozartspaces.core.requests.DeleteEntriesRequest;
import org.mozartspaces.core.requests.DestroyContainerRequest;
import org.mozartspaces.core.requests.LockContainerRequest;
import org.mozartspaces.core.requests.LookupContainerRequest;
import org.mozartspaces.core.requests.PrepareTransactionRequest;
import org.mozartspaces.core.requests.ReadEntriesRequest;
import org.mozartspaces.core.requests.RemoveAspectRequest;
import org.mozartspaces.core.requests.RollbackTransactionRequest;
import org.mozartspaces.core.requests.ShutdownRequest;
import org.mozartspaces.core.requests.TakeEntriesRequest;
import org.mozartspaces.core.requests.TestEntriesRequest;
import org.mozartspaces.core.requests.WriteEntriesRequest;

import autoKonfiguration.Auto;
import einzelteile.Achse;
import einzelteile.Bodenplatte;
import einzelteile.Karosserie;
import einzelteile.Lenkrad;
import einzelteile.ReifenPaar;
import einzelteile.Sitz;
import fabrik.rmi.Config;
import fabrik.rmi.ContainerNames;
/**
 * Mozartspaces Aspect der aus deneinzelteilen neue Autos fertigt wenn ausreichend einzelteil vorhanden sind
 * @author 
 *
 */
public class MontageAspect implements SpaceAspect {

	private static final long serialVersionUID = 1L;
	private long id = -1;
	/**
	 * Konstruktor, der beim Start aufgerufen wird
	 * @param montageID Der uebergebene ID-Parameter des Users
	 */
	public MontageAspect(long montageID) {
		this.id = montageID;
		autosErzeugen();
	}

	public void autosErzeugen() {
		try {
			/*
			 * Variablen um die Anzahl der einzelnen EInzelteile zu speichern
			 */
			int anzahlAchse = 0;
			int anzahlReifen = 0;
			int anzahlPlatte = 0;
			int anzahlSitze = 0;
			int anzahlKarosserie = 0;
			int anzahlLenkrad = 0;
			boolean anotherCar = true;
			MzsCore core = null;
			Capi capi;
			try {
				core = DefaultMzsCore.newInstanceWithoutSpace();
			} catch (MzsCoreRuntimeException me) {
			}
			capi = new Capi(core);
			
			TransactionReference transET = capi.createTransaction(10000, Config.locET);
			TransactionReference transAutos = capi.createTransaction(10000, Config.locAutos);
			ContainerReference achsen, reifen, platten, sitze, karosserie, lenkrad, fertig, idContainer;

			achsen = capi.lookupContainer(ContainerNames.ACHSEN, Config.locET, RequestTimeout.ZERO, transET);
			reifen = capi.lookupContainer(ContainerNames.REIFEN, Config.locET, RequestTimeout.ZERO, transET);
			platten = capi.lookupContainer(ContainerNames.PLATTEN, Config.locET, RequestTimeout.ZERO, transET);
			sitze = capi.lookupContainer(ContainerNames.SITZE, Config.locET, RequestTimeout.ZERO, transET);
			karosserie = capi.lookupContainer(ContainerNames.KAROSSERIE, Config.locET, RequestTimeout.ZERO, transET);
			lenkrad = capi.lookupContainer(ContainerNames.LENKRAD, Config.locET, RequestTimeout.ZERO, transET);
			fertig = capi.lookupContainer(ContainerNames.FERTIG, Config.locAutos, RequestTimeout.ZERO, transAutos);
			idContainer = capi.lookupContainer(ContainerNames.ID, Config.locAutos, RequestTimeout.ZERO, transAutos);
			
			/*
			 * Diese Schleife rennt, solange die benoetigten Einzelteile fuer ein Auto im Space liegen. Ueber
			 * die test-Methode wird die Anzahl ueberprueft und wenn diese passt, wird die ID fuer
			 * das fertige Auto erzeugt und ein neues Auto-Objekt bekommt diese Teile dann zugeteilt.Im Anschluss
			 * wird dieses in den Space geschrieben.
			 * 
			 */
			while (anotherCar) {
				Auto auto = null;

				anzahlAchse = capi.test(achsen, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
						RequestTimeout.ZERO, transET);
				anzahlReifen = capi.test(reifen, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
						RequestTimeout.ZERO, transET);
				anzahlPlatte = capi.test(platten, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
						RequestTimeout.ZERO, transET);
				anzahlSitze = capi.test(sitze, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
						RequestTimeout.ZERO, transET);
				anzahlKarosserie = capi.test(karosserie, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
						RequestTimeout.ZERO, transET);
				anzahlLenkrad = capi.test(lenkrad, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
						RequestTimeout.ZERO, transET);

				System.err.println(anzahlAchse + " " + anzahlReifen + " " + anzahlPlatte + " " + anzahlSitze + " "
						+ anzahlKarosserie + " " + anzahlLenkrad);
				if (anzahlAchse > 1 && anzahlReifen > 1 && anzahlPlatte > 0 && anzahlSitze > 0 && anzahlKarosserie > 0
						&& anzahlLenkrad > 0) {
					ArrayList<Long> ids = capi.read(idContainer,
							FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
							MzsConstants.RequestTimeout.ZERO, transAutos);
					/*
					 * Dieser komplette if-else Block hat die Aufgabe, die ID des Roboters zu bestimmen.
					 * Hierzu wurden bereits die IDs aus dem Space gelesen. Wenn diese IDs leer waren, bedeutet
					 * das, dass dieser Roboter den ersten Schreibvorgang uebernimmt. Wenn dem nicht so ist, wird
					 * geprueft, ob die uebergebene ID des Benutzers verfuegbar ist und diese benutzt. Wenn sie nicht
					 * verfuegbar ist, wird die hoechste im Space gelesen und um 1 erhoeht.
					 */
					if (this.id != -1) {

						if (ids.size() != 0) {

							for (long currentId : ids) {
								if (currentId == this.id)
									this.id = -1;
							}
							if (this.id == -1)
								this.id = (Long) capi.read(idContainer,
										FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
										MzsConstants.RequestTimeout.ZERO, transAutos).get(ids.size() - 1) + 1;
						} else {
							this.id = 1;
						}
						capi.write(idContainer, new Entry(id));
					}
					System.err.println(id + " ist vergeben!");
					long entryID = (Long) capi.read(idContainer,
							FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
							MzsConstants.RequestTimeout.ZERO, transAutos).get(ids.size()-1) +1;
					auto = new Auto(entryID, id);

					auto.setAchseVorn((Achse) capi.take(achsen, FifoCoordinator.newSelector(), RequestTimeout.ZERO,
							transET).get(0));
					auto.setAchseHinten((Achse) capi.take(achsen, FifoCoordinator.newSelector(), RequestTimeout.ZERO,
							transET).get(0));
					auto.setReifenPaarVorn((ReifenPaar) capi.take(reifen, FifoCoordinator.newSelector(),
							RequestTimeout.ZERO, transET).get(0));
					auto.setReifenPaarHinten((ReifenPaar) capi.take(reifen, FifoCoordinator.newSelector(),
							RequestTimeout.ZERO, transET).get(0));
					auto.setBodenplatte((Bodenplatte) capi.take(platten, FifoCoordinator.newSelector(),
							RequestTimeout.ZERO, transET).get(0));
					auto.setSitz((Sitz) capi.take(sitze, FifoCoordinator.newSelector(), RequestTimeout.ZERO, transET)
							.get(0));
					auto.setKarosserie((Karosserie) capi.take(karosserie, FifoCoordinator.newSelector(),
							RequestTimeout.ZERO, transET).get(0));
					auto.setLenkrad((Lenkrad) capi.take(lenkrad, FifoCoordinator.newSelector(), RequestTimeout.ZERO,
							transET).get(0));

					capi.write(fertig, new Entry(auto));
					capi.write(idContainer, new Entry(entryID));
					
					System.err.println("Car successfully produced! with IDs: " + id + " and " + entryID);
				} else {
					anotherCar = false;
					System.err.println("Es liegen nicht alle benoetigten Einzelteile im Space vor!");
				}
			}
			capi.commitTransaction(transET);
			capi.commitTransaction(transAutos);
		} catch (MzsCoreException me) {
			System.err.println("Es liegen nicht alle benoetigten Einzelteile im Space vor!");
		}
	}
	
	/**
	 * Eigentlich Post-write aspekt, nach dem etwas in den Space geschrieben wurde wir ddie autosErzeugen aufgerufen
	 */
	@Override
	public AspectResult postWrite(WriteEntriesRequest arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4) {
		autosErzeugen();
		return AspectResult.OK;
	}

	@Override
	public AspectResult postAddAspect(AddAspectRequest arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4, AspectReference arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postDelete(DeleteEntriesRequest arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4, List<Serializable> arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postDestroyContainer(DestroyContainerRequest arg0, Transaction arg1, SubTransaction arg2,
			int arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postLockContainer(LockContainerRequest arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postLookupContainer(LookupContainerRequest arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4, ContainerReference arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postRead(ReadEntriesRequest<?> arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4, List<Serializable> arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postRemoveAspect(RemoveAspectRequest arg0, Transaction arg1, SubTransaction arg2,
			ContainerReference arg3, Capi3AspectPort arg4, int arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postTake(TakeEntriesRequest<?> arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4, List<Serializable> arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postTest(TestEntriesRequest arg0, Transaction arg1, SubTransaction arg2, Capi3AspectPort arg3,
			int arg4, List<Serializable> arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preAddAspect(AddAspectRequest arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preDelete(DeleteEntriesRequest arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preDestroyContainer(DestroyContainerRequest arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preLockContainer(LockContainerRequest arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preRead(ReadEntriesRequest<?> arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preRemoveAspect(RemoveAspectRequest arg0, Transaction arg1, SubTransaction arg2,
			ContainerReference arg3, Capi3AspectPort arg4, int arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preTake(TakeEntriesRequest<?> arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preTest(TestEntriesRequest arg0, Transaction arg1, SubTransaction arg2, Capi3AspectPort arg3,
			int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preWrite(WriteEntriesRequest arg0, Transaction arg1, SubTransaction arg2, Capi3AspectPort arg3,
			int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postCommitTransaction(CommitTransactionRequest arg0, Transaction arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postCreateContainer(CreateContainerRequest arg0, Transaction arg1, SubTransaction arg2,
			Capi3AspectPort arg3, int arg4, ContainerReference arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postCreateTransaction(CreateTransactionRequest arg0, TransactionReference arg1, Transaction arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postPrepareTransaction(PrepareTransactionRequest arg0, Transaction arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult postRollbackTransaction(RollbackTransactionRequest arg0, Transaction arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preCommitTransaction(CommitTransactionRequest arg0, Transaction arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preCreateContainer(CreateContainerRequest arg0, Transaction arg1, SubTransaction arg2, int arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preCreateTransaction(CreateTransactionRequest arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preLookupContainer(LookupContainerRequest arg0, Transaction arg1, SubTransaction arg2, int arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult prePrepareTransaction(PrepareTransactionRequest arg0, Transaction arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preRollbackTransaction(RollbackTransactionRequest arg0, Transaction arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectResult preShutdown(ShutdownRequest arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
