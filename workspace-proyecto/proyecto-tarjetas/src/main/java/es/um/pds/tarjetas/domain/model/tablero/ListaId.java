package es.um.pds.tarjetas.domain.model.tablero;

import java.util.Objects;

public class ListaId {
	// Atributos
	private Long id;
	
	// Excepcion
	public static class ListaInvalidaException extends Exception {
		public ListaInvalidaException(String mensaje) {
			super(mensaje);
		}
	}
	
	// Constructor
	private ListaId(Long id) {
		this.id = id;
	}
	
	// Método 'of' aplicando patrón creador y método factoría
	public static ListaId of(Long id) throws ListaInvalidaException {
		if(id == null || id <= 0) {
			throw new ListaInvalidaException("El identificador de la lista debe ser mayor que 0.");
		}
		return new ListaId(id);
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
		} else if(!(obj instanceof ListaId)) {
			return false;
		}
		ListaId other = (ListaId) obj;
		return Objects.equals(this.id, other.id);
	}
}
