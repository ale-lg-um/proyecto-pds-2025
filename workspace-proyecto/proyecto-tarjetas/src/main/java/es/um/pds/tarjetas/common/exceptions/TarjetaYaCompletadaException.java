package es.um.pds.tarjetas.common.exceptions;

public class TarjetaYaCompletadaException extends RuntimeException {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public TarjetaYaCompletadaException(String mensaje) {
		super(mensaje);
	}
}
