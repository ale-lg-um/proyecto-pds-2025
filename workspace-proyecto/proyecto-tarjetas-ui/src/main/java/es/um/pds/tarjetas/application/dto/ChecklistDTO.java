package es.um.pds.tarjetas.application.dto;

import java.util.List;

public record ChecklistDTO(List<ItemChecklistDTO> items) implements ContenidoTarjetaDTO {

}
