package es.um.pds.tarjetas.application.usecases;

public class TableroBloqueadoException extends Exception {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public TableroBloqueadoException(String mensaje) {
		super(mensaje);
	}
}
