package einzelteile;

import autoKonfiguration.Einzelteil;

/**
 * @author Michael Borko
 * @see autoKonfiguration.Einzelteil
 */
public class ReifenPaar extends Einzelteil {

	private static final long serialVersionUID = 8870259099546995755L;

	public ReifenPaar(long einzelteilID, long produktionsRoboterID,
			boolean defekt) {
		super(einzelteilID, produktionsRoboterID, defekt);
	}

}
