package es.um.pds.tarjetas.domain.model.tablero;

// Value Object
public class EstadoBloqueo {
	// Atributos
	private String motivo; // El motivo por el que se bloquea el tablero
	
	// Excepción
	public static class EspecBloqueoInvalidaException extends Exception {
		public EspecBloqueoInvalidaException(String mensaje) {
			super(mensaje);
		}
	}
	
	// Constructor
	private EstadoBloqueo(String motivo) {
		this.motivo = motivo;
	}
	
	// Método 'of'
	public static EstadoBloqueo of(String motivo) throws EspecBloqueoInvalidaException {
		if(motivo == null || motivo.isBlank()) {
			throw new EspecBloqueoInvalidaException("El motivo del bloqueo no puede ser nulo");
		}
		
		return new EstadoBloqueo(motivo);
	}
}
