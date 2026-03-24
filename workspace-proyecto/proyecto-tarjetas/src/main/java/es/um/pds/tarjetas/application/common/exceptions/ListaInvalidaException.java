package es.um.pds.tarjetas.application.common.exceptions;

public class ListaInvalidaException extends Exception {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public ListaInvalidaException(String mensaje) {
		super(mensaje);
	}
}
