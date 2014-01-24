package einzelteile;

import autoKonfiguration.Einzelteil;

/**
 * @author Michael Borko
 * @see autoKonfiguration.Einzelteil
 */
public class Bodenplatte extends Einzelteil {

	private static final long serialVersionUID = -7029100608161452016L;

	public Bodenplatte(long einzelteilID, long produktionsRoboterID,
			boolean defekt) {
		super(einzelteilID, produktionsRoboterID, defekt);
	}

}
