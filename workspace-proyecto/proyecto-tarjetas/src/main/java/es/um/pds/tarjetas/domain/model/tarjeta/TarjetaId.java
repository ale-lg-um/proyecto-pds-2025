package es.um.pds.tarjetas.domain.model.tarjeta;

import java.util.Objects;

public class TarjetaId {
	// Atributos
	public Long id;
	
	// Excepción
	public static class TarjetaInvalidaException extends Exception {
		public TarjetaInvalidaException(String mensaje) {
			super(mensaje);
		}
	}
	
	// Constructor
	private TarjetaId(Long id) {
		this.id = id;
	}
	
	// Método 'of' aplicando patrón creador y método factoría
	public static TarjetaId of(Long id) throws TarjetaInvalidaException{
		if(id == null || id <= 0) {
			throw new TarjetaInvalidaException("El identificador de la tarjeta debe ser mayor que 0.");
		}
		return new TarjetaId(id);
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
		} else if(!(obj instanceof TarjetaId)) {
			return false;
		}
		TarjetaId other = (TarjetaId) obj;
		return Objects.equals(this.id, other.id);
	}
}
