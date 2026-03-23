package es.um.pds.tarjetas.domain.ports.input.plantilla;

import es.um.pds.tarjetas.application.common.EspecificacionTableroPlantilla;
import es.um.pds.tarjetas.domain.model.plantilla.PlantillaId;

public interface ServicioPlantilla {
	// Valida el esquema de la plantilla
	PlantillaId crearPlantilla(String yaml, String emailUsuario);
	
	// Devuelve la estructura que utiliza ServicioGestionTablero para crear el tablero
	EspecificacionTableroPlantilla parsearPlantilla(PlantillaId plantilla);
}