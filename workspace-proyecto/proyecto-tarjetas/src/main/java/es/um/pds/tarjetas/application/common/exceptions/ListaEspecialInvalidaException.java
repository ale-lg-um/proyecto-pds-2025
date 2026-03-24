package es.um.pds.tarjetas.application.common.exceptions;

public class ListaEspecialInvalidaException extends Exception {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public ListaEspecialInvalidaException(String mensaje) {
		super(mensaje);
	}
}
