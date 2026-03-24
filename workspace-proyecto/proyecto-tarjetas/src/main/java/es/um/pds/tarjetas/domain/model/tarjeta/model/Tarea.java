package es.um.pds.tarjetas.domain.model.tarjeta.model;

public class Tarea extends ContenidoTarjeta{
	// Atributos
	private String descripcion;
	
	// Excepción
	public static class TareaInvalidaException extends Exception {
		// Identificador de versión para que no salga el warning
		private static final long serialVersionUID = 1L;

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
			throw new TareaInvalidaException("El texto de la tarea no puede estar vacío");
		}
		return new Tarea(descripcion);
	}
	
	// Getters
	public String getDescripcion() {
		return this.descripcion;
	}
	
	// No redefinimos equals y hashCode porque podemos tener tareas con la misma
	// descripción en diferentes tarjetas. Solo comparamos por oid
	

	@Override
	public TipoContenido getTipo() {
		return ContenidoTarjeta.TipoContenido.TAREA;
	}
}
