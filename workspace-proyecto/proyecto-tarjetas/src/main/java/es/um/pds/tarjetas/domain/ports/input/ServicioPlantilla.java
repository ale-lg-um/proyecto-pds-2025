package es.um.pds.tarjetas.domain.ports.input;

public interface ServicioPlantilla {

    String crearPlantilla(String yaml, String emailUsuario);

    String obtenerYamlPlantilla(String plantillaId);
}