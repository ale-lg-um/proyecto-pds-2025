package es.um.pds.tarjetas.domain.model.tablero;

import java.util.Objects;

import es.um.pds.tarjetas.domain.exceptions.TableroInvalidoException;

public class TableroId {
	// Atributos
	private final Long id;
	
	// Constructor
	private TableroId(Long id) {
		this.id = id;
	}
	
	// Método factoría
	public static TableroId of(Long id) throws TableroInvalidoException {
		if(id == null || id <= 0) {
			throw new TableroInvalidoException("El código del tablero debe ser mayor que 0");
		}
		return new TableroId(id);
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
		} else if(!(obj instanceof TableroId)) {
			return false;
		}
		TableroId other = (TableroId) obj;
		return Objects.equals(this.id, other.id);
	}
}
