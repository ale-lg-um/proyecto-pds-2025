package es.um.pds.tarjetas.domain.ports.input.dto;

import es.um.pds.tarjetas.domain.model.tarjeta.model.Etiqueta;

public record EtiquetaDTO(String nombre, String color) {
	public EtiquetaDTO(Etiqueta etiqueta) {
		this(etiqueta.nombre(), etiqueta.color());
	}
}