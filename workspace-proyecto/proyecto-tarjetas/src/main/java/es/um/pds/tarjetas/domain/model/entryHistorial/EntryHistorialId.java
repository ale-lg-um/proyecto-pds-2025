package es.um.pds.tarjetas.domain.model.entryHistorial;

import java.util.Objects;
import java.util.UUID;

import es.um.pds.tarjetas.domain.exceptions.EntryHistorialInvalidaException;

public class EntryHistorialId {
	// Atributos
	private final String id;
	
	// Constructor
	private EntryHistorialId(String id) {
		this.id = id;
	}
	
	// Método factoría con identificador
	public static EntryHistorialId of(String id) throws EntryHistorialInvalidaException {
		if(id == null) {
			throw new EntryHistorialInvalidaException("El código del tablero no puede ser nulo");
		}
		return new EntryHistorialId(id);
	}
	
	// Método factoría sin identificador
	public static EntryHistorialId of() throws EntryHistorialInvalidaException {
		String id = UUID.randomUUID().toString();
		return new EntryHistorialId(id);
	}
	
	// Getters
	public String getId() {
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
