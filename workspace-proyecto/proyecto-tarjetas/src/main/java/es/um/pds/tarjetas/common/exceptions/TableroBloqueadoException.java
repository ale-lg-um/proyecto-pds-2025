package es.um.pds.tarjetas.common.exceptions;

public class TableroBloqueadoException extends RuntimeException {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public TableroBloqueadoException(String mensaje) {
		super(mensaje);
	}
}
