package es.um.pds.tarjetas.domain.exceptions;

public class UsuarioInvalidoException extends Exception {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;
	public UsuarioInvalidoException(String mensaje) {
		super(mensaje);
	}
}
