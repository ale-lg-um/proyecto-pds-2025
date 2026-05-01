package es.um.pds.tarjetas.common.exceptions;

public class NoExisteListaEspecialException extends RuntimeException {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public NoExisteListaEspecialException(String mensaje) {
		super(mensaje);
	}
}
