package es.um.pds.tarjetas.ui.infrastructure.api;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import es.um.pds.tarjetas.application.dto.PlantillaDTO;

public class PlantillaApiClient extends BaseApiClient {
	
	// Tipos para las peticiones
	
	record CrearPlantillaRequest(String yaml) {}
	
	// Constructor
	
	public PlantillaApiClient(String baseURL) {
		super(baseURL + "/plantillas");
	}
	
	// Métodos
	
	public PlantillaDTO obtenerPorId(String plantillaId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = get("/" + enc(plantillaId), token);
		if(response.statusCode() == 200) {
			PlantillaDTO plantilla = json.readValue(response.body(), PlantillaDTO.class);
			return plantilla;
		} else {
			throw new RuntimeException("Error en el servidor, " + response.body());
		}
	}
	
	public List<PlantillaDTO> obtenerTodas(String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = get("", token);
		if(response.statusCode() == 200) {
			List<PlantillaDTO> plantillas = json.readValue(response.body(), new TypeReference<>() {});
			return plantillas;
		} else {
			throw new RuntimeException("Error en el servidor, " + response.body());
		}
	}
	
	public PlantillaDTO crearPlantilla(String yaml, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = post("", token, new CrearPlantillaRequest(yaml));
		if(response.statusCode() == 201) {
			PlantillaDTO plantilla = json.readValue(response.body(), PlantillaDTO.class);
			return plantilla;
		} else {
			throw new RuntimeException("El servidor no ha podido crear la plantilla, " + response.body());
		}
	}
}
