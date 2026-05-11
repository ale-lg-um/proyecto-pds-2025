package es.um.pds.tarjetas.domain.ports.input.dto;

import es.um.pds.tarjetas.domain.model.tarjeta.model.Checklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ContenidoTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarea;

public sealed interface ContenidoTarjetaDTO permits TareaDTO, ChecklistDTO {

	static ContenidoTarjetaDTO fromDomain(ContenidoTarjeta contenido) {
		if (contenido instanceof Tarea tarea) {
			return new TareaDTO(tarea);
		}
		if (contenido instanceof Checklist checklist) {
			return new ChecklistDTO(checklist);
		}

		throw new IllegalArgumentException("Tipo de contenido de tarjeta no soportado: "
				+ (contenido == null ? "null" : contenido.getClass().getName()));
	}

	default ContenidoTarjeta toDomain() {
		return switch (this) {
		case TareaDTO tareaDTO -> tareaDTO.toDomain();
		case ChecklistDTO checklistDTO -> checklistDTO.toDomain();
		default -> throw new IllegalArgumentException("Tipo de contenido DTO no soportado");
		};
	}
}