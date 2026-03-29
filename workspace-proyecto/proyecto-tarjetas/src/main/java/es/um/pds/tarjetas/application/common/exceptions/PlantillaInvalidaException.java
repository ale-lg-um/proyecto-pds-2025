package es.um.pds.tarjetas.application.common.exceptions;

public class PlantillaInvalidaException extends RuntimeException {
	// Identificador de versión para que no salga el warning
	private static final long serialVersionUID = 1L;

	public PlantillaInvalidaException(String mensaje) {
		super(mensaje);
	}
}
