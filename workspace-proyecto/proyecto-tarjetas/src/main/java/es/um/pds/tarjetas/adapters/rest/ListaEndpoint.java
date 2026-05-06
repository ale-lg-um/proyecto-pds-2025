package es.um.pds.tarjetas.adapters.rest;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.um.pds.tarjetas.adapters.rest.requests.CrearListaRequest;
import es.um.pds.tarjetas.adapters.rest.requests.LimiteRequest;
import es.um.pds.tarjetas.adapters.rest.requests.PrerrequisitoRequest;
import es.um.pds.tarjetas.adapters.rest.requests.RenombrarRequest;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioLista;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;

@RestController
@RequestMapping("/tableros/{tableroId}/listas")
public class ListaEndpoint {

	private final ServicioSesion servicioSesion;
	private final ServicioLista servicioLista;
	private final RepositorioListas repositorioListas;
	
	public ListaEndpoint(ServicioSesion servicioSesion, ServicioLista servicioLista, RepositorioListas repositorioListas) {
		this.servicioSesion = servicioSesion;
		this.servicioLista = servicioLista;
		this.repositorioListas = repositorioListas;
	}
	
	// GET http://localhost:8080/tableros/{tableroId}/listas
	@GetMapping
	public ResponseEntity<?> obtener(@RequestHeader("Authorization") String token,
									 @PathVariable TableroId tableroId) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			servicioSesion.validarYRenovarToken(authToken);	
			
			Set<Lista> listas = repositorioListas.buscarPorTableroId(tableroId);
			
			return ResponseEntity.status(HttpStatus.OK)
								 .body(listas);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// POST http://localhost:8080/tableros/{tableroId}/listas
	@PostMapping
	public ResponseEntity<?> crear(@RequestHeader("Authorization") String token,
								   @PathVariable String tableroId,
								   @RequestBody CrearListaRequest body) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			ListaDTO lista = servicioLista.crearLista(tableroId, body.nombre(), usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.CREATED)
								 .body(lista);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PUT http://localhost:8080/tableros/{tableroId}/listas/{listaId}/renombrar
	@PutMapping("/{listaId}/renombrar")
	public ResponseEntity<?> renombrar(@RequestHeader("Authorization") String token,
									   @PathVariable String tableroId,
									   @PathVariable String listaId,
									   @RequestBody RenombrarRequest body) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			servicioLista.renombrarLista(tableroId, listaId, body.nuevoNombre(), usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// DELETE http://localhost:8080/tableros/{tableroId}/listas/{listaId}
	@DeleteMapping("/{listaId}")
	public ResponseEntity<?> eliminar(@RequestHeader("Authorization") String token,
									  @PathVariable String tableroId,
									  @PathVariable String listaId) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			servicioLista.eliminarLista(tableroId, listaId,	usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PUT http://localhost:8080/tableros/{tableroId}/listas/{listaId}/especial
	@PutMapping("/{listaId}/especial")
	public ResponseEntity<?> definirEspecial(@RequestHeader("Authorization") String token,
											 @PathVariable String tableroId,
											 @PathVariable String listaId) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			servicioLista.definirListaEspecial(tableroId, listaId, usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PATCH http://localhost:8080/tableros/{tableroId}/listas/{listaId}/configurar
	@PatchMapping("/{listaId}/configurar")
	public ResponseEntity<?> configurarLimite(@RequestHeader("Authorization") String token,
											  @PathVariable String tableroId,
											  @PathVariable String listaId,
											  @RequestBody LimiteRequest body) {
		try {	
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);
			servicioLista.configurarLimiteLista(tableroId, listaId, body.limite(), usuario.getCorreo());
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PUT http://localhost:8080/tableros/{tableroId}/listas/{listaId}/prerrequisitos
	@PutMapping("/{listaId}/prerrequisitos")
	public ResponseEntity<?> configurarPrerrequisitos(@RequestHeader("Authorization") String token,
													  @PathVariable String tableroId,
													  @PathVariable String listaId,
													  @RequestBody PrerrequisitoRequest body) {
		try {	
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);
			servicioLista.configurarPrerrequisitosLista(tableroId, listaId, body.prerrequisitos(), usuario.getCorreo());
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
}
