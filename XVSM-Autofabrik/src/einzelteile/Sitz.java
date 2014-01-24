package einzelteile;

import autoKonfiguration.Einzelteil;

/**
 * @author Michael Borko
 * @see autoKonfiguration.Einzelteil
 */
public class Sitz extends Einzelteil {

	private static final long serialVersionUID = 1254436722148964017L;

	public Sitz(long einzelteilID, long produktionsRoboterID, boolean defekt) {
		super(einzelteilID, produktionsRoboterID, defekt);
	}

}
