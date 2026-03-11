package serviciosAplicacion;

public class ServicioPlantilla {
	// Valida el esquema
	PlantillaId crearPlantilla(String yaml, Actor actor);
	
	// Devuelve la estructura que utiliza ServicioGestionTablero para crear el tablero
	EspecsTablero parsearPlantilla(PlantillaId);
}
