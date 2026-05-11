package es.um.pds.tarjetas.domain.model.lista.id;

import java.util.Objects;
import java.util.UUID;

import es.um.pds.tarjetas.common.exceptions.ListaInvalidaException;

public class ListaId {
	// Atributos
	private final String id;
	
	// Constructor
	private ListaId(String id) {
		this.id = id;
	}
	
	// Método 'of' aplicando patrón creador y método factoría con identificador
	public static ListaId of(String id) {
		if(id == null) {
			throw new ListaInvalidaException("El identificador de la lista no puede ser nulo");
		}
		return new ListaId(id);
	}
	
	// Método 'of' aplicando patrón creador y método factoría sin identificador
	public static ListaId of() {
		String id = UUID.randomUUID().toString();
		return new ListaId(id);
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
		} else if(!(obj instanceof ListaId)) {
			return false;
		}
		ListaId other = (ListaId) obj;
		return Objects.equals(this.id, other.id);
	}
}
