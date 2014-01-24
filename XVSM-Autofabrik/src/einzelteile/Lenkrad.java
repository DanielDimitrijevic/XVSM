package einzelteile;

import autoKonfiguration.Einzelteil;

/**
 * @author Michael Borko
 * @see autoKonfiguration.Einzelteil
 */
public class Lenkrad extends Einzelteil {

	private static final long serialVersionUID = -5182588068349297877L;

	public Lenkrad(long einzelteilID, long produktionsRoboterID, boolean defekt) {
		super(einzelteilID, produktionsRoboterID, defekt);
	}

}
