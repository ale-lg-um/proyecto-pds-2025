package es.um.pds.tarjetas.ui.infrastructure.api;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import es.um.pds.tarjetas.application.dto.EntryHistorialDTO;
import es.um.pds.tarjetas.application.dto.PageDTO;
import es.um.pds.tarjetas.application.dto.PlantillaDTO;
import es.um.pds.tarjetas.application.dto.ResultadoCrearTableroDTO;
import es.um.pds.tarjetas.application.dto.TableroDTO;

public class DashboardApiClient extends BaseApiClient {
	
	// Tipos para las peticiones
	
	record CrearTableroRequest(String nombre, String email, String plantillaId, String nombrePlantilla, PlantillaDTO plantillaCreacion) {}
	record RenombrarRequest(String nombre) {}
	record BloqueoRequest(LocalDateTime inicio, LocalDateTime fin, String motivo) {}
	
	// Constructor
	
	public DashboardApiClient(String baseURL) {
		super(baseURL + "/tableros");
	}
	
	// Métodos
	
	public List<TableroDTO> obtenerTableros(String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = get("", token);
		if(response.statusCode() == 200) {
			List<TableroDTO> tableros = json.readValue(response.body(), new TypeReference<>() {});
			return tableros;
		} else {
			throw new RuntimeException("Error del servidor, " + response.body());
		}
	}
	
	public TableroDTO obtenerTableroPorUrl(String url, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = get("?url=" + enc(url), token);
		if(response.statusCode() == 200) {
			TableroDTO tablero = json.readValue(response.body(), TableroDTO.class);
			return tablero;
		} else {
			throw new RuntimeException("Error del servidor, " + response.body());
		}
	}
	
	public TableroDTO obtenerTableroPorId(String id, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = get("/" + enc(id), token);
		if(response.statusCode() == 200) {
			TableroDTO tablero = json.readValue(response.body(), TableroDTO.class);
			return tablero;
		} else {
			throw new RuntimeException("Error del servidor, " + response.body());
		}
	}
	
	public ResultadoCrearTableroDTO crearTablero(String nombre, String email, String plantillaId, String nombrePlantilla, PlantillaDTO plantillaCreacion, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = post("", token, new CrearTableroRequest(nombre, email, plantillaId, nombrePlantilla, plantillaCreacion));
		if(response.statusCode() == 201) {
			ResultadoCrearTableroDTO resultado = json.readValue(response.body(), ResultadoCrearTableroDTO.class);
			return resultado;
		} else {
			throw new RuntimeException("Error del servidor, " + response.body());
		}
	}
	
	public void renombrarTablero(String tableroId, String nombre, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = put("/" + enc(tableroId) + "/renombrar", token, new RenombrarRequest(nombre));
		if(response.statusCode() != 204) {
			throw new RuntimeException("Error del servidor, " + response.body());
		}
	}
	
	public void eliminarTablero(String tableroId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = delete("/" + enc(tableroId), token);
		if(response.statusCode() != 204) {
			throw new RuntimeException("Error del servidor, " + response.body());
		}
	}
	
	public void bloquearTablero(String tableroId, LocalDateTime inicio, LocalDateTime fin, String motivo, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = put("/" + enc(tableroId) + "/bloquear", token, new BloqueoRequest(inicio, fin, motivo));
		if(response.statusCode() != 204) {
			throw new RuntimeException("Error del servidor, " + response.body());
		}
	}
	
	public void desbloquearTablero(String tableroId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = delete("/" + enc(tableroId) + "/bloquear", token);
		if(response.statusCode() != 204) {
			throw new RuntimeException("Error del servidor, " + response.body());
		}
	}
	
	public PageDTO<EntryHistorialDTO> mostrarHistorialTablero(String tableroId, int page, int tamano, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = get("/" + enc(tableroId) + "/historial?pagina=" + enc(Integer.toString(page)) + "&tamano=" + enc(Integer.toString(tamano)), token);
		if(response.statusCode() == 200) {
			PageDTO<EntryHistorialDTO> historial = json.readValue(response.body(), new TypeReference<PageDTO<EntryHistorialDTO>>() {});
			return historial;
		} else {
			throw new RuntimeException("Error en el servidor, " + response.body());
		}
	}
	
	
}
