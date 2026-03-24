package es.um.pds.tarjetas.application.common.exceptions;

public class NoExisteListaEspecialException extends Exception {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public NoExisteListaEspecialException(String mensaje) {
		super(mensaje);
	}
}
