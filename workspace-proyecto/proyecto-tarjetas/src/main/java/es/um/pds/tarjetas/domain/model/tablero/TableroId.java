package es.um.pds.tarjetas.domain.model.tablero;

public record TableroId(Long codigo) {
	// Constructor compacto
	public TableroId {
		if(codigo == null) {
			throw new IllegalArgumentException("Código no puede estar vacío.");
		}
	}
}
