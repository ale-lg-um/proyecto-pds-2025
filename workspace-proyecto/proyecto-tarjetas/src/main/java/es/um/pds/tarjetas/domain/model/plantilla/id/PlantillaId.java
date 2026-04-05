package es.um.pds.tarjetas.domain.model.plantilla.id;

import java.util.Objects;
import java.util.UUID;

import es.um.pds.tarjetas.application.common.exceptions.PlantillaInvalidaException;

public class PlantillaId {
	// Atributos
	private final String id;
	
	// Constructor
	private PlantillaId(String id) {
		this.id = id;
	}
	
	// Método factoría con identificador
	public static PlantillaId of(String id) {
		if (id == null) {
			throw new PlantillaInvalidaException("El identificador de la plantilla no puede ser nulo");
		}
		return new PlantillaId(id);
	}
	
	// Método factoría sin identificador
	public static PlantillaId of() {
		String id = UUID.randomUUID().toString();
		return new PlantillaId(id);
	}
	
	// Getters
	public String getId() {
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