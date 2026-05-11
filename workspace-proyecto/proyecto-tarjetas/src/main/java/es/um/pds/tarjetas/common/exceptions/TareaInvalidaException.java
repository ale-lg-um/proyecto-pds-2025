package es.um.pds.tarjetas.common.exceptions;

public class TareaInvalidaException extends RuntimeException {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public TareaInvalidaException(String mensaje) {
		super(mensaje);
	}
}
