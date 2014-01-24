package fabrik;

/**
 * Interface der ToyCarFactory, die von allen alternativen Technologien
 * implementiert werden muss. Die GUI basiert auf diesen Methoden.
 * 
 * @author Michael Borko
 */
public interface IFactory {

	public void startProduction(int quantity, int err, String type);

	/**
	 * Anzahl eines gewissen Einzelteils im Produktionsspeicher der Fabrik.
	 * 
	 * @param type
	 *            Art des Einzelteils
	 * @return
	 */
	public int getQuantity(String type);

	/**
	 * GUI erfragt die Fabrik nach den fertiggestellten und zur Auslieferung
	 * bereiten Autos. Die Abfrage kommt einer Lieferung gleich und entfernt
	 * somit die Autos aus der Fabrik.
	 * 
	 * @return Aufbereiteter String der zu liefernden Autos
	 */
	public String[] getDeliveredCars();

	/**
	 * GUI erfragt die Fabrik nach den defekten und in der Sammelstelle
	 * aufbewahrten Autos. Die Abfrage kommt einer Lieferung gleich und entfernt
	 * somit die Autos aus der Fabrik.
	 * 
	 * @return Aufbereiteter String der defekten Autos
	 */
	public String[] getFaultyCars();

	/**
	 * Befehl an die Fabrik um die gesamte Produktion sauber abzuschlie\u00DFen.
	 */
	public void shutdown();
}
