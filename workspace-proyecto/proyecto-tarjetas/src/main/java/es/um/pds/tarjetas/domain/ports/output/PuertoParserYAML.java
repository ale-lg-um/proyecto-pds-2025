package es.um.pds.tarjetas.domain.ports.output;


import es.um.pds.tarjetas.application.common.EspecificacionTableroPlantilla;

public interface PuertoParserYAML {

    EspecificacionTableroPlantilla parse(String yaml);
}