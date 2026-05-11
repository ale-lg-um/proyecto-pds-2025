package serviciosAplicacion;

public interface ServicioPlantilla {

    PlantillaDTO crearPlantilla(String yaml, String emailUsuario);

    PlantillaDTO obtenerPlantilla(String plantillaId);
}
