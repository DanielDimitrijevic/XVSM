package fabrik.rmi.roboter;

import java.io.IOException;

import org.mozartspaces.capi3.ContainerNameNotAvailableException;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.MzsCoreRuntimeException;
import org.mozartspaces.core.TransactionReference;

import fabrik.rmi.Config;
import fabrik.rmi.ContainerNames;
/**
 * Erstellt den Space und bereitet alles fuer reibungslosen Zugriff vor
 * @author 
 *
 */
public class XvsmSetup {
	
	MzsCore core=null; 
	
	public static void main(String []args){
		
		XvsmSetup xs = new XvsmSetup();
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xs.shutdown();
		
		
	}
	public XvsmSetup(){
		
		try{
			core = DefaultMzsCore.newInstance(1234);
		} catch(MzsCoreRuntimeException me){
			System.err.println("XVSM wurde bereits aufgesetzt!");
			System.exit(0);
		}
		
		Capi capi = new Capi(core);
		
		try {
			TransactionReference trans = capi.createTransaction(100, Config.locET);
			capi.createContainer(ContainerNames.ACHSEN, Config.locET,
					MzsConstants.Container.UNBOUNDED, trans,
					new FifoCoordinator());
			capi.createContainer(ContainerNames.REIFEN, Config.locET,
					MzsConstants.Container.UNBOUNDED, trans,
					new FifoCoordinator());
			capi.createContainer(ContainerNames.PLATTEN, Config.locET,
					MzsConstants.Container.UNBOUNDED, trans,
					new FifoCoordinator());
			capi.createContainer(ContainerNames.SITZE, Config.locET,
					MzsConstants.Container.UNBOUNDED, trans,
					new FifoCoordinator());
			capi.createContainer(ContainerNames.KAROSSERIE, Config.locET,
					MzsConstants.Container.UNBOUNDED, trans,
					new FifoCoordinator());
			capi.createContainer(ContainerNames.LENKRAD, Config.locET,
					MzsConstants.Container.UNBOUNDED, trans,
					new FifoCoordinator());
			
			capi.commitTransaction(trans);
		}catch (ContainerNameNotAvailableException ce) {
			ce.printStackTrace();
			System.out.println("Container wurden bereits erstellt.");
		} catch (MzsCoreException e) {
			e.printStackTrace();
		}
		
		try{
			core = DefaultMzsCore.newInstance(1235);
		} catch(MzsCoreRuntimeException me){
			System.err.println("Port wird bereits genutzt");
		}
		
		capi = new Capi(core);
		
		try{
			TransactionReference trans = capi.createTransaction(100, Config.locAutos);
			capi.createContainer(ContainerNames.FERTIG, Config.locAutos,
					MzsConstants.Container.UNBOUNDED, trans,
					new FifoCoordinator());
			capi.createContainer(ContainerNames.SAMMELSTELLE, Config.locAutos,
					MzsConstants.Container.UNBOUNDED, trans,
					new FifoCoordinator());
			capi.createContainer(ContainerNames.AUSLIEFERN, Config.locAutos,
					MzsConstants.Container.UNBOUNDED, trans,
					new FifoCoordinator());
			capi.createContainer(ContainerNames.GEPRUEFT, Config.locAutos,
					MzsConstants.Container.UNBOUNDED, trans,
					new FifoCoordinator());
			capi.createContainer(ContainerNames.ID, Config.locAutos,
					MzsConstants.Container.UNBOUNDED, trans,
					new FifoCoordinator());
			capi.commitTransaction(trans);
		} catch (ContainerNameNotAvailableException ce) {
			ce.printStackTrace();
			System.out.println("Container wurden bereits erstellt.");
		} catch (MzsCoreException e) {
			e.printStackTrace();
		}
		
		System.err.println("Container wurden erstellt");
	}
	public void shutdown(){
		core.shutdown(true);
	}
}
