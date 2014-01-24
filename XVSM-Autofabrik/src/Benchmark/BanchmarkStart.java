package Benchmark;

import java.io.IOException;

import fabrik.IFactory;
import fabrik.rmi.roboter.LogistikRoboter;
import fabrik.rmi.roboter.MontageRoboter;
import fabrik.rmi.roboter.ProduktionsRoboter;
import fabrik.rmi.roboter.PruefRoboter;
import fabrik.rmi.roboter.XvsmSetup;
import gui.ToyCarFactory;
/**
 * Start Klasse
 * @author Thomas Traxler<ttraxler@student.tgm.ac.at>
 *
 */
public class BanchmarkStart {
	
	public static void main (String[] args){
		int autozahl =100;
		double fehlerrate = 0.1;
		long logID = -1;
		long monID=-1;
		long prueID=-1;
		try{
			autozahl=Integer.parseInt(args[0]);
			fehlerrate = Double.parseDouble(args[1]);
			logID = Long.parseLong(args[2]);
			monID = Long.parseLong(args[3]);
			prueID= Long.parseLong(args[4]);
		}catch(ArrayIndexOutOfBoundsException |NumberFormatException e){
			
		}
		XvsmSetup xs = new XvsmSetup();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MontageRoboter monteur = new MontageRoboter(monID);
		monteur.start();
		
		PruefRoboter pruefRoboter;
		pruefRoboter= new PruefRoboter(prueID);
		pruefRoboter.start();
		
		LogistikRoboter lieferant = new LogistikRoboter(logID);
		lieferant.start();
		
		new ProduktionsRoboter(autozahl,fehlerrate,"Achse").start();
		
		
		ToyCarFactory.main(null);
//		//			System.in.read();
//		//			System.out.println("Roboter wird zerstoert!");
//					monteur.xvsmShutdown();
//					monteur.interrupt();
//					pruefRoboter.xvsmShutdown();
//					pruefRoboter.interrupt();
//					lieferant.xvsmShutdown();
//					lieferant.interrupt();
//					xs.shutdown();
	}

}
