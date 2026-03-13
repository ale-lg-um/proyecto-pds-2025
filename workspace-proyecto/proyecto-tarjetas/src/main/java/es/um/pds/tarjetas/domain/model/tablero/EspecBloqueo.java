package es.um.pds.tarjetas.domain.model.tablero;

// Value Object
public class EspecBloqueo {
	// Atributos
	private String motivo; // El motivo por el que se bloquea el tablero
	
	// Excepción
	public static class EspecBloqueoInvalidaException extends Exception {
		public EspecBloqueoInvalidaException(String mensaje) {
			super(mensaje);
		}
	}
	
	// Constructor
	private EspecBloqueo(String motivo) {
		this.motivo = motivo;
	}
	
	// Método 'of'
	public static EspecBloqueo of(String motivo) throws EspecBloqueoInvalidaException {
		if(motivo == null || motivo.isBlank()) {
			throw new EspecBloqueoInvalidaException("El motivo del bloqueo no puede ser nulo");
		}
		
		return new EspecBloqueo(motivo);
	}
}
