package es.um.pds.tarjetas.domain.ports.input.dto;

import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarea;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarea.TareaInvalidaException;

public record TareaDTO(String descripcion) implements ContenidoTarjetaDTO {

	public TareaDTO(Tarea tarea) {
		this(tarea.getDescripcion());
	}
	
	public Tarea toDomain() throws TareaInvalidaException {
		return Tarea.of(this.descripcion);
	}
}