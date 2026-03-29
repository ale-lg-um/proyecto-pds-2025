package es.um.pds.tarjetas.application.usecases;

public class EntryHistorialInvalidaException extends Exception {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public EntryHistorialInvalidaException(String mensaje) {
		super(mensaje);
	}
}
