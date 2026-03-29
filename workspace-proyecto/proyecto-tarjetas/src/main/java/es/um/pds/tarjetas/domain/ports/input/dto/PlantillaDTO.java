package es.um.pds.tarjetas.domain.ports.input.dto;

import es.um.pds.tarjetas.domain.model.plantilla.model.Plantilla;

public record PlantillaDTO(String id, String nombre, String contenidoYaml) {
	public PlantillaDTO(Plantilla plantilla) {
		this(plantilla.getIdentificador().getId(), plantilla.getNombre(), plantilla.getContenidoYaml());
	}
}