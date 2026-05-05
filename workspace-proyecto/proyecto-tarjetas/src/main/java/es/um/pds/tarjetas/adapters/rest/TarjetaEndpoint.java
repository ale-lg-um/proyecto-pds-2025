	package es.um.pds.tarjetas.adapters.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.um.pds.tarjetas.adapters.rest.requests.CrearEtiquetaRequest;
import es.um.pds.tarjetas.adapters.rest.requests.CrearTarjetaRequest;
import es.um.pds.tarjetas.adapters.rest.requests.EditarTarjetaRequest;
import es.um.pds.tarjetas.adapters.rest.requests.ModificarEtiquetaRequest;
import es.um.pds.tarjetas.adapters.rest.requests.MoverTarjetaRequest;
import es.um.pds.tarjetas.adapters.rest.requests.RenombrarRequest;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;
import es.um.pds.tarjetas.domain.ports.input.ServicioTarjeta;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;

@RestController
@RequestMapping("/tableros/{tableroId}/listas/{listaId}/tarjetas")
public class TarjetaEndpoint {
	
	private final ServicioSesion servicioSesion;
	private final ServicioTarjeta servicioTarjeta;
	
	public TarjetaEndpoint(ServicioSesion servicioSesion, ServicioTarjeta servicioTarjeta) {
		this.servicioSesion = servicioSesion;
		this.servicioTarjeta = servicioTarjeta;
	}
	
	// POST http://localhost:8080/tableros/{tableroId}/listas/{listaId}/tarjetas
	@PostMapping
	public ResponseEntity<?> crear(@RequestHeader("Authorization") String token,
								   @PathVariable String tableroId,
								   @PathVariable String listaId,
								   @RequestBody CrearTarjetaRequest body) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			servicioSesion.validarYRenovarToken(authToken);	
			
			TarjetaDTO tarjeta = servicioTarjeta.crearTarjeta(tableroId, listaId, body.nombre(), body.contenido());
			
			return ResponseEntity.status(HttpStatus.CREATED)
								 .body(tarjeta);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PUT http://localhost:8080/tableros/{tableroId}/listas/{listaId}/tarjetas/{tarjetaId}/editar
	@PutMapping("/{tarjetaId}/editar")
	public ResponseEntity<?> editar(@RequestHeader("Authorization") String token,
									@PathVariable String tableroId,
									@PathVariable String listaId,
									@PathVariable String tarjetaId,
									@RequestBody EditarTarjetaRequest body) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			servicioSesion.validarYRenovarToken(authToken);	
			
			servicioTarjeta.editarContenidoTarjeta(tableroId, listaId, tarjetaId, body.contenido());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PUT http://localhost:8080/tableros/{tableroId}/listas/{listaId}/tarjetas/{tarjetaId}/renombrar
	@PutMapping("/{tarjetaId}/renombrar")
	public ResponseEntity<?> renombrar(@RequestHeader("Authorization") String token,
			   @PathVariable String tableroId,
			   @PathVariable String listaId,
			   @PathVariable String tarjetaId,
			   @RequestBody RenombrarRequest body) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
		
			servicioTarjeta.renombrarTarjeta(tableroId, listaId, tarjetaId, body.nuevoNombre(), usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					 			 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					 			 .body(e.getMessage());
		}
	}
	
	// DELETE http://localhost:8080/tableros/{tableroId}/listas/{listaId}/tarjetas/{tarjetaId}
	@DeleteMapping("/{tarjetaId}")
	public ResponseEntity<?> eliminar(@RequestHeader("Authorization") String token,
									  @PathVariable String tableroId,
									  @PathVariable String listaId,
									  @PathVariable String tarjetaId) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			servicioTarjeta.eliminarTarjeta(tableroId, listaId, tarjetaId, usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PATCH http://localhost:8080/tableros/{tableroId}/listas/{listaId}/tarjetas/{tarjetaId}/mover
	@PatchMapping("/{tarjetaId}/mover")
	public ResponseEntity<?> mover(@RequestHeader("Authorization") String token,
								   @PathVariable String tableroId,
								   @PathVariable String listaId,
								   @PathVariable String tarjetaId,
								   @RequestBody MoverTarjetaRequest body) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			servicioTarjeta.moverTarjeta(tableroId, tarjetaId, listaId, body.destino(), usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// POST http://localhost:8080/tableros/{tableroId}/listas/{listaId}/tarjetas/{tarjetaId}/mover
	@PostMapping("/{tarjetaId}/completar")
	public ResponseEntity<?> completar(@RequestHeader("Authorization") String token,
								   	   @PathVariable String tableroId,
								   	   @PathVariable String listaId,
								   	   @PathVariable String tarjetaId) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			servicioTarjeta.completarTarjeta(tableroId, listaId, tarjetaId, usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PUT http://localhost:8080/tableros/{tableroId}/listas/{listaId}/tarjetas/{tarjetaId}/checklist/{indice}/completar
	@PutMapping("/{tarjetaId}/checklist/{indice}/completar")
	public ResponseEntity<?> completarItem(@RequestHeader("Authorization") String token,
										   @PathVariable String tableroId,
										   @PathVariable String listaId,
										   @PathVariable String tarjetaId,
										   @PathVariable int indice) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			servicioTarjeta.completarItemChecklist(tableroId, listaId, tarjetaId, indice, usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PUT http://localhost:8080/tableros/{tableroId}/listas/{listaId}/tarjetas/{tarjetaId}/checklist/{indice}/completar
	@DeleteMapping("/{tarjetaId}/checklist/{indice}/completar")
	public ResponseEntity<?> pendienteItem(@RequestHeader("Authorization") String token,
										   @PathVariable String tableroId,
										   @PathVariable String listaId,
										   @PathVariable String tarjetaId,
										   @PathVariable int indice) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			servicioTarjeta.marcarItemChecklistComoPendiente(tableroId, listaId, tarjetaId, indice, usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// POST http://localhost:8080/tableros/{tableroId}/listas/{listaId}/tarjetas/{tarjetaId}/etiqueta
	@PostMapping("/{tarjetaId}/etiqueta")
	public ResponseEntity<?> etiquetar(@RequestHeader("Authorization") String token,
									   @PathVariable String tableroId,
									   @PathVariable String listaId,
									   @PathVariable String tarjetaId,
									   @RequestBody CrearEtiquetaRequest body) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			servicioTarjeta.addEtiquetaATarjeta(tableroId, listaId, tarjetaId, body.nombre(), body.color(), usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// DELETE http://localhost:8080/tableros/{tableroId}/listas/{listaId}/tarjetas/{tarjetaId}/etiqueta?nombre={nombre}&color={color}
	@DeleteMapping("/{tarjetaId}/etiqueta")
	public ResponseEntity<?> eliminarEtiqueta(@RequestHeader("Authorization") String token,
									   		  @PathVariable String tableroId,
									   		  @PathVariable String listaId,
									   		  @PathVariable String tarjetaId,
									   		  @RequestParam String nombre,
									   		  @RequestParam String color) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			servicioTarjeta.eliminarEtiquetaDeTarjeta(tableroId, listaId, tarjetaId, nombre, color, usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PUT http://localhost:8080/tableros/{tableroId}/listas/{listaId}/tarjetas/{tarjetaId}/etiqueta
	@PutMapping("/{tarjetaId}/etiqueta")
	public ResponseEntity<?> modificarEtiqueta(@RequestHeader("Authorization") String token,
											   @PathVariable String tableroId,
											   @PathVariable String listaId,
											   @PathVariable String tarjetaId,
											   @RequestBody ModificarEtiquetaRequest body) {
		try {
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);	
			
			servicioTarjeta.modificarEtiquetaEnTarjeta(tableroId, listaId, tarjetaId, body.nombreOld(), body.colorOld(), body.nombreNuevo(), body.colorNuevo(), usuario.getCorreo());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
}
