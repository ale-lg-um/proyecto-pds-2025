package es.um.pds.tarjetas.application.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({  @JsonSubTypes.Type(value = TareaDTO.class), @JsonSubTypes.Type(value = ChecklistDTO.class)})
public sealed interface ContenidoTarjetaDTO permits TareaDTO, ChecklistDTO {

}
