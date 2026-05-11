package es.um.pds.tarjetas.common.exceptions;

public class PrerrequisitosNoCumplidosException extends RuntimeException {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public PrerrequisitosNoCumplidosException(String mensaje) {
		super(mensaje);
	}
}
