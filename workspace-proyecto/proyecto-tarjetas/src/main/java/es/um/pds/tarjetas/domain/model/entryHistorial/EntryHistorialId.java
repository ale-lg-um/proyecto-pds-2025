package es.um.pds.tarjetas.domain.model.entryHistorial;

import java.util.Objects;

import es.um.pds.tarjetas.domain.exceptions.EntryHistorialInvalidaException;

public class EntryHistorialId {
	// Atributos
	private final Long id;
	
	// Constructor
	private EntryHistorialId(Long id) {
		this.id = id;
	}
	
	// Método factoría
	public static EntryHistorialId of(Long id) throws EntryHistorialInvalidaException {
		if(id == null || id <= 0) {
			throw new EntryHistorialInvalidaException("El código del tablero debe ser mayor que 0");
		}
		return new EntryHistorialId(id);
	}
	
	// Getters
	public Long getId() {
		return this.id;
	}
	
	// Overrides
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(!(obj instanceof EntryHistorialId)) {
			return false;
		}
		EntryHistorialId other = (EntryHistorialId) obj;
		return Objects.equals(this.id, other.id);
	}
}
