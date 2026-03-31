package es.um.pds.tarjetas.domain.ports.input;

import es.um.pds.tarjetas.domain.ports.input.dto.PlantillaDTO;

public interface ServicioPlantilla {

    PlantillaDTO crearPlantilla(String yaml, String emailUsuario);

    PlantillaDTO obtenerYamlPlantilla(String plantillaId);
}