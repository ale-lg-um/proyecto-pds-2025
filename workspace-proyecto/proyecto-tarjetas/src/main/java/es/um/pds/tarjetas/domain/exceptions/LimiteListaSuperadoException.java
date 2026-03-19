package es.um.pds.tarjetas.domain.exceptions;

public class LimiteListaSuperadoException extends Exception {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public LimiteListaSuperadoException(String mensaje) {
		super(mensaje);
	}
}
