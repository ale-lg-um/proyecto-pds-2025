package es.um.pds.tarjetas.domain.model.etiqueta;

// Value object
public record Etiqueta(String nombre, String color) {
	// Constructor
	public Etiqueta {
		if(nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre de la etiqueta no puede ser nulo");
		} else if(color == null || color.isBlank()) {
			throw new IllegalArgumentException("El coolor de la etiqueta no puede ser nulo");
		}
	}
}
