package es.um.pds.tarjetas.domain.model.tarjeta.model;

// Value object
public record Etiqueta(String nombre, String color) {
	// Constructor
	public Etiqueta {
		if(nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre de la etiqueta no puede ser nulo o vacío");
		} else if(color == null || color.isBlank()) {
			throw new IllegalArgumentException("El color de la etiqueta no puede ser nulo o vacío");
		}
	}
}
