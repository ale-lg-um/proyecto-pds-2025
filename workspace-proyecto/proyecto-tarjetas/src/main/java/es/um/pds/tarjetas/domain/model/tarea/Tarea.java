package es.um.pds.tarjetas.domain.model.tarea;

import java.util.Objects;

public class Tarea {
	// Atributos
	private String descripcion;
	
	// Excepción
	public static class TareaInvalidaException extends Exception {
		public TareaInvalidaException(String mensaje) {
			super(mensaje);
		}
	}
	
	// Constructor
	private Tarea(String descripcion) {
		this.descripcion = descripcion;
	}
	
	// Método 'of'
	public static Tarea of(String descripcion) throws TareaInvalidaException {
		if(descripcion == null || descripcion.isBlank()) {
			throw new TareaInvalidaException("El texto de la tarea no puede estar vacío.");
		}
		return new Tarea(descripcion);
	}
	
	// Getters
	public String getDescripcion() {
		return this.descripcion;
	}
	
	// Overrides
	@Override
	public int hashCode() {
		return Objects.hash(descripcion);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(!(obj instanceof Tarea)) {
			return false;
		}
		Tarea other = (Tarea) obj;
		return Objects.equals(this.descripcion, other.descripcion);
	}
}
