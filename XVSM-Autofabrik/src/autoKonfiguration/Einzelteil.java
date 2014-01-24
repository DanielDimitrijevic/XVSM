package autoKonfiguration;

import java.io.Serializable;

/**
 * Einzelteile haben eine eindeutige ID und man soll zu jeder Zeit
 * \u00FCberpr\u00FCfen k\u00F6nnen, welcher Roboter welches Teil erzeugt hat.
 * F\u00FCr jedes Einzelteil muss außerdem gespeichert werden, ob es defekt ist
 * oder nicht.
 * 
 * @author Michael Borko
 */
public class Einzelteil implements Serializable {

	private static final long serialVersionUID = -5902145448729678726L;

	private final long einzelteilID;
	private final long produktionsRoboterID;
	private final boolean defekt;

	public Einzelteil(final long einzelteilID, final long produktionsRoboterID,
			final boolean defekt) {
		this.einzelteilID = einzelteilID;
		this.produktionsRoboterID = produktionsRoboterID;
		this.defekt = defekt;
	}

	/**
	 * Diese ID ist systemweit eindeutig und identifiziert das produzierte
	 * Einzelteil.
	 * 
	 * @return ID des Einzelteils
	 */
	public long getEinzelteilID() {
		return einzelteilID;
	}

	/**
	 * Diese ID ist systemweit eindeutig und identifiziert den
	 * ProduktionsRoboter, welcher das Einzelteil hergestellt hat.
	 * 
	 * @return ID des Produzenten
	 */
	public long getProduktionsRoboterID() {
		return produktionsRoboterID;
	}

	/**
	 * Beschreibt den Status des Einzelteils.
	 * 
	 * @return Wahr wenn Einzelteil defekt, sonst Falsch
	 */
	public boolean istDefekt() {
		return defekt;
	}

	/**
	 * Gibt die ID des Einzelteils und die ID des Produzenten aus, getrennt
	 * durch ein Semikolon. Sollte das Einzelteil defekt sein, wird der ID ein
	 * '#' vorangestellt.
	 * <p>
	 * Zum Beispiel fehlerhaftes Einzelteil: #1231;23
	 */
	@Override
	public String toString() {
		String ret = "";
		if (defekt)
			ret += "#" + einzelteilID;
		else
			ret += "" + einzelteilID;
		ret += ";" + produktionsRoboterID;

		return ret;
	}
}
