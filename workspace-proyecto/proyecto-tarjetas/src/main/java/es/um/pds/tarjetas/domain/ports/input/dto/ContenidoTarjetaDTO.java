package es.um.pds.tarjetas.domain.ports.input.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import es.um.pds.tarjetas.domain.model.tarjeta.model.Checklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ContenidoTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarea;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({ @JsonSubTypes.Type(value = TareaDTO.class, name = "TAREA"),
		@JsonSubTypes.Type(value = ChecklistDTO.class, name = "CHECKLIST") })
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
	
	default ContenidoTarjeta toDomain() throws Exception {
		if(this instanceof TareaDTO tareaDTO) {
			return tareaDTO.toDomain();
		} else if(this instanceof ChecklistDTO checklistDTO) {
			return checklistDTO.toDomain();
		} else {
			throw new IllegalArgumentException("Tipo de contenido DTO no soportado");
		}
	}
}