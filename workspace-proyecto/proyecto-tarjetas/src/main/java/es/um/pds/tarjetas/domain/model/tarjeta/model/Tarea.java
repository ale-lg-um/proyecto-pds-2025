package es.um.pds.tarjetas.domain.model.tarjeta.model;

import es.um.pds.tarjetas.common.exceptions.TareaInvalidaException;

public class Tarea extends ContenidoTarjeta{
	// Atributos
	private String descripcion;

	// Constructor
	public Tarea(String descripcion) {
		this.descripcion = descripcion;
	}
	
	// Método 'of'
	public static Tarea of(String descripcion) {
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
	public TipoContenidoTarjeta getTipo() {
		return TipoContenidoTarjeta.TAREA;
	}
}
