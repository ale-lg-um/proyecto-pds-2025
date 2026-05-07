package es.um.pds.tarjetas.domain.ports.input;

import java.util.List;

import es.um.pds.tarjetas.domain.ports.input.dto.PlantillaDTO;

public interface ServicioPlantilla {

    PlantillaDTO crearPlantilla(String yaml, String emailUsuario);

    PlantillaDTO obtenerPlantilla(String plantillaId);
    
    List<PlantillaDTO> listarPlantillas();
}