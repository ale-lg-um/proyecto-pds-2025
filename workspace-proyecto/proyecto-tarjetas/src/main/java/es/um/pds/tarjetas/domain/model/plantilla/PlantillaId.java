package es.um.pds.tarjetas.domain.model.plantilla;

import java.util.Objects;

import es.um.pds.tarjetas.domain.exceptions.PlantillaInvalidaException;

public class PlantillaId {
	// Atributos
	private final Long id;
	
	// Constructor
	private PlantillaId(Long id) {
		this.id = id;
	}
	
	// Método factoría
	public static PlantillaId of(Long id) throws PlantillaInvalidaException {
		if (id == null || id <= 0) {
			throw new PlantillaInvalidaException("El identificador de la plantilla debe ser mayor que 0");
		}
		return new PlantillaId(id);
	}
	
	// Getters
	public Long getId() {
		return this.id;
	}
	
	// Overrides
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof PlantillaId)) {
			return false;
		}
		PlantillaId other = (PlantillaId) obj;
		return Objects.equals(this.id, other.id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}