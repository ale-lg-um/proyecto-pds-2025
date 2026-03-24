package es.um.pds.tarjetas.domain.model.tablero;

import java.util.Objects;
import java.util.UUID;

import es.um.pds.tarjetas.application.common.exceptions.TableroInvalidoException;

public class TableroId {
	// Atributos
	private final String id;
	
	// Constructor
	private TableroId(String id) {
		this.id = id;
	}
	
	// Método factoría con identificador
	public static TableroId of(String id) throws TableroInvalidoException {
		if(id == null) {
			throw new TableroInvalidoException("El código del tablero no puede ser nulo");
		}
		return new TableroId(id);
	}
	
	// Método factoría sin identificador
	public static TableroId of() throws TableroInvalidoException {
		String id = UUID.randomUUID().toString();
		return new TableroId(id);
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
		} else if(!(obj instanceof TableroId)) {
			return false;
		}
		TableroId other = (TableroId) obj;
		return Objects.equals(this.id, other.id);
	}
}
