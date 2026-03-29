package es.um.pds.tarjetas.domain.ports.input.dto;

import es.um.pds.tarjetas.domain.model.tarjeta.model.ItemChecklist;

public record ItemChecklistDTO(String descripcion, boolean completado) {
	public ItemChecklistDTO(ItemChecklist item) {
		this(item.getDescripcion(), item.isCompletado());
	}
}