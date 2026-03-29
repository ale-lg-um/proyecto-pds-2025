package es.um.pds.tarjetas.domain.ports.input.dto;

import java.util.List;

import es.um.pds.tarjetas.domain.model.tarjeta.model.Checklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ItemChecklist;

public record ChecklistDTO(List<ItemChecklistDTO> items) implements ContenidoTarjetaDTO {

	public ChecklistDTO(Checklist checklist) {
		this(checklist.getItems().stream().map(ItemChecklistDTO::new).toList());
	}
	
	public Checklist toDomain() {
		List<ItemChecklist> items = this.items.stream()
				.map(ItemChecklistDTO::toDomain)
				.toList();
		return Checklist.of(items);
	}
}