package es.um.pds.tarjetas.domain.ports.input.dto;

import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarea;

public record TareaDTO(String descripcion) implements ContenidoTarjetaDTO {

	public TareaDTO(Tarea tarea) {
		this(tarea.getDescripcion());
	}
	
	@Override
	public Tarea toDomain() {
		return Tarea.of(this.descripcion);
	}
}