package einzelteile;

import autoKonfiguration.Einzelteil;

/**
 * @author Michael Borko
 * @see autoKonfiguration.Einzelteil
 */
public class Karosserie extends Einzelteil {

	private static final long serialVersionUID = 1873786832726224363L;

	public Karosserie(long einzelteilID, long produktionsRoboterID,
			boolean defekt) {
		super(einzelteilID, produktionsRoboterID, defekt);
	}

}
