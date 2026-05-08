package es.um.pds.tarjetas.ui.infrastructure.api;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;

import es.um.pds.tarjetas.application.dto.ListaDTO;


public class ListaApiClient extends BaseApiClient {
	
	// Tipos para las peticiones
	
	record CrearListaRequest(String nombre, String correo) {}
	record RenombrarRequest(String nuevoNombre) {}
	record LimiteRequest(Integer limite) {}
	record PrerrequisitoRequest(Set<String> prerrequisitos) {}
	// Constructor
	
	public ListaApiClient(String baseURL) {
		super(baseURL);
	}
	
	// Métodos
	
	public List<ListaDTO> obtenerListas(String tableroId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = get("/tableros/" + enc(tableroId) + "/listas", token);
		if(response.statusCode() == 200) {
			List<ListaDTO> listas = json.readValue(response.body(), new TypeReference<>() {});
			return listas;
		} else {
			throw new RuntimeException("El servidor no ha podido obtener las listas, " + response.body());
		}
	}
	
	public ListaDTO obtenerListaPorId(String listaId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = get("/listas/" + enc(listaId), token);
		if(response.statusCode() == 200) {
			ListaDTO lista = json.readValue(response.body(), new TypeReference<>() {});
			return lista;
		} else {
			throw new RuntimeException("El servidor no ha podido obtener las listas, " + response.body());
		}
	}
	
	public ListaDTO crearLista(String tableroId, String nombre, String correo, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = post("/tableros/" + enc(tableroId) + "/listas", token, new CrearListaRequest(nombre, correo));
		if(response.statusCode() == 201) {
			ListaDTO lista = json.readValue(response.body(), ListaDTO.class);
			return lista;
		} else {
			throw new RuntimeException("El servidor no ha podido crear la lista, " + response.body());
		}
	}
	
	public void renombrarLista(String tableroId, String listaId, String nuevoNombre, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = put("/tableros/" + enc(tableroId) + "/listas/"+ enc(listaId) + "/renombrar", token, new RenombrarRequest(nuevoNombre));
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido renombrar la lista, " + response.body());
		}
	}
	
	public void eliminarLista(String tableroId, String listaId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = delete("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId), token);
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido eliminar la lista, " + response.body());
		}
	}
	
	public void definirListaEspecial(String tableroId, String listaId, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = put("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/especial", token, "");
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido definir la lista creada como especial, " + response.body());
		}
	}
	
	public void configurarLimiteLista(String tableroId, String listaId, Integer limite, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = put("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/configurar", token, new LimiteRequest(limite));
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido configurar el límite de la lista, " + response.body());
		}
	}
	
	public void configurarPrerrequisitosLista(String tableroId, String listaId, Set<String> prerreq, String token) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = put("/tableros/" + enc(tableroId) + "/listas/" + enc(listaId) + "/prerrequisitos", token, new PrerrequisitoRequest(prerreq));
		if(response.statusCode() != 204) {
			throw new RuntimeException("El servidor no ha podido configurar los prerrequisitos de la lista, " + response.body());
		}
	}
}
