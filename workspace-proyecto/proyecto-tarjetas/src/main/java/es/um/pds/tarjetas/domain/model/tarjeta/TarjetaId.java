package es.um.pds.tarjetas.domain.model.tarjeta;

import java.util.Objects;
import java.util.UUID;

import es.um.pds.tarjetas.domain.exceptions.TarjetaInvalidaException;

public class TarjetaId {
	// Atributos
	private final String id;
	
	// Constructor
	private TarjetaId(String id) {
		this.id = id;
	}
	
	// Método 'of' aplicando patrón creador y método factoría con identificador
	public static TarjetaId of(String id) throws TarjetaInvalidaException{
		if(id == null) {
			throw new TarjetaInvalidaException("El identificador de la tarjeta no puede ser nulo");
		}
		return new TarjetaId(id);
	}
	
	// Método 'of' aplicando patrón creador y método factoría sin identificador
	public static TarjetaId of() throws TarjetaInvalidaException{
		String id = UUID.randomUUID().toString();
		return new TarjetaId(id);
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
		} else if(!(obj instanceof TarjetaId)) {
			return false;
		}
		TarjetaId other = (TarjetaId) obj;
		return Objects.equals(this.id, other.id);
	}
}
