package es.um.pds.tarjetas.domain.model.tablero;

import java.util.Objects;

public class TableroId {
	// Atributos
	private Long codigo;
	
	// Excepción
	public static class IdentificadorTableroException extends Exception {
		// Identificador de versión para que no salga el warning
		private static final long serialVersionUID = 1L;
		public IdentificadorTableroException(String mensaje) {
			super(mensaje);
		}
		public IdentificadorTableroException(String mensaje, Exception e) {
			super(mensaje, e);
		}
	}
	
	// Constructor
	private TableroId(Long codigo) {
		this.codigo = codigo;
	}
	
	// Método 'of' que aplica patrón creador y método factoría
	public static TableroId of(Long codigo) throws IdentificadorTableroException {
		if(codigo == null || codigo <= 0) {
			throw new IdentificadorTableroException("El código del tablero debe ser mayor que 0");
		}
		return new TableroId(codigo);
	}
	
	// Getters
	public Long getCodigo() {
		return this.codigo;
	}
	
	// Overrides
	@Override
	public int hashCode() {
		return Objects.hash(codigo);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(!(obj instanceof TableroId)) {
			return false;
		}
		TableroId other = (TableroId) obj;
		return Objects.equals(this.codigo, other.codigo);
	}
}
