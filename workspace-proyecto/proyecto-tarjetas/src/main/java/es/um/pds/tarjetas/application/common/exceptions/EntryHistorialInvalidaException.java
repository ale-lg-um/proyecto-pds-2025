package es.um.pds.tarjetas.application.common.exceptions;

public class EntryHistorialInvalidaException extends Exception {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public EntryHistorialInvalidaException(String mensaje) {
		super(mensaje);
	}
}
