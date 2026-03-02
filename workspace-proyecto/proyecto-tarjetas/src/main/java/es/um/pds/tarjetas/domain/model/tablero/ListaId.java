package es.um.pds.tarjetas.domain.model.tablero;

public record ListaId(Long codigo) {
	// Constructor compacto
	public ListaId {
		if(codigo == null) {
			throw new IllegalArgumentException("Codigo no puede ser nulo.");
		}
	}
}
