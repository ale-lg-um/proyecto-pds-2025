package es.um.pds.tarjetas.adapters.rest.requests;

import es.um.pds.tarjetas.domain.model.plantilla.model.Plantilla;

public record CrearTableroRequest(String nombre, String email, String plantillaId, String nombrePlantilla, Plantilla plantillaCreacion) {

}
