package es.um.pds.tarjetas.domain.ports.input.dto;

import java.util.List;

import es.um.pds.tarjetas.domain.model.tarjeta.model.Checklist;

public record ChecklistDTO(List<ItemChecklistDTO> items) implements ContenidoTarjetaDTO {

	public ChecklistDTO(Checklist checklist) {
		this(checklist.getItems().stream().map(ItemChecklistDTO::new).toList());
	}
}