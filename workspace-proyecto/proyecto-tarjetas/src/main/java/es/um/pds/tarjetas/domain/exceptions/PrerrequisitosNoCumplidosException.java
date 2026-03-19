package es.um.pds.tarjetas.domain.exceptions;

public class PrerrequisitosNoCumplidosException extends Exception {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public PrerrequisitosNoCumplidosException(String mensaje) {
		super(mensaje);
	}
}
