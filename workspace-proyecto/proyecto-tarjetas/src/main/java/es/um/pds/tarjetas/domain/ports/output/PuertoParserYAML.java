package es.um.pds.tarjetas.domain.ports.output;


import es.um.pds.tarjetas.domain.model.plantilla.EspecificacionTableroPlantilla;

public interface PuertoParserYAML {

    EspecificacionTableroPlantilla parse(String yaml);
}