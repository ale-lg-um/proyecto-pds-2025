package es.um.pds.tarjetas.domain.ports.output.external;


import es.um.pds.tarjetas.application.common.EspecificacionTableroPlantilla;
import es.um.pds.tarjetas.domain.exceptions.PlantillaInvalidaException;

public interface PuertoParserYAML {

    EspecificacionTableroPlantilla parse(String yaml) throws PlantillaInvalidaException;
}