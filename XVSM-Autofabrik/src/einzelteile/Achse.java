package einzelteile;

import autoKonfiguration.Einzelteil;

/**
 * @author Michael Borko
 * @see autoKonfiguration.Einzelteil
 */
public class Achse extends Einzelteil {

	private static final long serialVersionUID = 5598211891157669211L;

	public Achse(long einzelteilID, long produktionsRoboterID, boolean defekt) {
		super(einzelteilID, produktionsRoboterID, defekt);
	}

}
