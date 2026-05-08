package es.um.pds.tarjetas.ui.infrastructure.api;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import es.um.pds.tarjetas.application.dto.ContenidoTarjetaCmd;
import es.um.pds.tarjetas.application.dto.TarjetaDTO;


public class TarjetaApiClient extends BaseApiClient {
	
	// Tipos para las peticiones
	record CrearTarjetaRequest(String nombre, ContenidoTarjetaCmd contenido) {}
	record MoverTarjetaRequest(String destino) {}
	record EditarTarjetaRequest(ContenidoTarjetaCmd cmd) {}
	record CrearEtiquetaRequest(String nombre, String color) {}
	record ModificarEtiquetaRequest(String nombreOld, String colorOld, String nombreNuevo, String colorNuevo) {}
	
	// Constructor
	
	public TarjetaApiClient(String baseURL) {
		super(baseURL);
	}
	
	// Métodos
	
	public List<TarjetaDTO> obtenerPorLista(String listaId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = get("/listas/" + enc(listaId) + "/tarjetas", token);
		if(response.statusCode() == 200) {
			List<TarjetaDTO> tarjetas = json.readValue(response.body(), new TypeReference<>() {});
			return tarjetas;
		} else {
			throw new RuntimeException("El servidor no ha podido obtener la tarjeta, " + response.body());
		}
	}
	
	public TarjetaDTO obtenerTarjeta(String tarjetaId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = get("/tarjetas/" + enc(tarjetaId), token);
		if(response.statusCode() == 200) {
			TarjetaDTO tarjeta = json.readValue(response.body(), TarjetaDTO.class);
			return tarjeta;
		} else {
			throw new RuntimeException("El servidor no ha podido obtener la tarjeta, " + response.body());
		}
	}
	
	public TarjetaDTO crearTarjeta(String tableroId, String listaId, String nombre, ContenidoTarjetaCmd contenido, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = post("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/tarjetas", token, new CrearTarjetaRequest(nombre, contenido));
		if(response.statusCode() == 201) {
			TarjetaDTO tarjeta = json.readValue(response.body(), TarjetaDTO.class);
			return tarjeta;
		} else {
			throw new RuntimeException("El servidor no ha podido crear la tarjeta, " + response.body());
		}
	}
	
	public void eliminarTarjeta(String tableroId, String listaId, String tarjetaId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = delete("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/tarjetas/" + enc(tarjetaId), token);
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido eliminar la tarjeta, " + response.body());
		}
	}
	
	public void moverTarjeta(String tableroId, String listaOrigenId, String listaDestinoId, String tarjetaId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = put("/tableros/" + enc(tableroId) + "/listas/" + enc(listaOrigenId) + "/tarjetas/" + enc(tarjetaId) + "/mover",
											  token,
											  new MoverTarjetaRequest(listaDestinoId)
										   );
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido mover la tarjeta, " + response.body());
		}
	}
	
	public void editarTarjeta(String tableroId, String listaId, String tarjetaId, ContenidoTarjetaCmd cmd, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = put("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/tarjetas/" + enc(tarjetaId) + "/editar", token, new EditarTarjetaRequest(cmd));
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido editar el contenido de la tarjeta, " + response.body());
		}
	}
	
	public void completarTarjeta(String tableroId, String listaId, String tarjetaId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = post("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/tarjetas/" + enc(tarjetaId) + "/completar", token, "");
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido marcar como completada la tarjeta, " + response.body());
		}
	}
	
	public void completarItemChecklist(String tableroId, String listaId, String tarjetaId, int indice, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = put("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/tarjetas/" + enc(tarjetaId) + "/checklist/" + enc(Integer.toString(indice)) + "/completar", token, "");
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido marcar como completado el ítem de la checklist, " + response.body());
		}
	}
	
	public void pendienteItemChecklist(String tableroId, String listaId, String tarjetaId, int indice, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = delete("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/tarjetas/" + enc(tarjetaId) + "/checklist/" + enc(Integer.toString(indice)) + "/completar", token);
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido marcar como pendiente el ítem de la checklist, " + response.body());
		}
	}
	
	public void etiquetarTarjeta(String tableroId, String listaId, String tarjetaId, String nombre, String color, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = post("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/tarjetas/" + enc(tarjetaId) + "/etiqueta", token, new CrearEtiquetaRequest(nombre, color));
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido etiquetar la tarjeta, " + response.body());
		}
	}
	
	public void eliminarEtiquetaTarjeta(String tableroId, String listaId, String tarjetaId, String nombre, String color, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = delete("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/tarjetas/" + enc(tarjetaId) + "/etiqueta?nombre=" + enc(nombre) + "&color=" + enc(color), token);
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido eliminar la etiqueta de la tarjeta, " + response.body());
		}
	}
	
	public void modificarEtiquetaTarjeta(String tableroId, String listaId, String tarjetaId, String nombreOld, String colorOld, String nombreNuevo, String colorNuevo, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = put("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/tarjetas/" + enc(tarjetaId) + "/etiqueta", token, new ModificarEtiquetaRequest(nombreOld, colorOld, nombreNuevo, colorNuevo));
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido modificar la etiqueta de la tarjeta, " + response.body());
		}
	}
}
