package fabrik.rmi;

import java.net.URI;

/**
 * Konfigurationspool der RMI CarFactory-Implementierung.
 * 
 * @author Michael Borko
 */
public class Config {

	public static URI locET = URI.create("xvsm://localhost:1234"); //Location fuer Einzelzeile
	public static URI locAutos = URI.create("xvsm://localhost:1235"); //Location fuer Autos
}
