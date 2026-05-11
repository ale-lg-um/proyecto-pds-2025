package es.um.pds.tarjetas.adapters.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.um.pds.tarjetas.adapters.rest.requests.CrearPlantillaRequest;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioPlantilla;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;
import es.um.pds.tarjetas.domain.ports.input.dto.PlantillaDTO;

@RestController
@RequestMapping("/plantillas")
public class PlantillaEndpoint {
	
	private final ServicioSesion servicioSesion;
	private final ServicioPlantilla servicioPlantilla;
	
	public PlantillaEndpoint(ServicioSesion servicioSesion, ServicioPlantilla servicioPlantilla) {
		this.servicioSesion = servicioSesion;
		this.servicioPlantilla = servicioPlantilla;
	}
	
	// GET http://localhost:8080/plantillas/{plantillaId}
	@GetMapping("/{plantillaId}")
	public ResponseEntity<?> obtenerPorId(@RequestHeader("Authorization") String token,
										  @PathVariable String plantillaId) {
		try {	
			String authToken = token != null ? token.replace("Bearer ", "") : "";
			servicioSesion.validarYRenovarToken(authToken);
				
			PlantillaDTO plantilla = servicioPlantilla.obtenerPlantilla(plantillaId);
			return ResponseEntity.status(HttpStatus.OK)
								 .body(plantilla);
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// GET http://localhost:8080/plantillas
	@GetMapping
	public ResponseEntity<?> obtenerTodas(@RequestHeader("Authorization") String token) {
		try {	
			String authToken = token != null ? token.replace("Bearer ", "") : "";
			servicioSesion.validarYRenovarToken(authToken);
				
			List<PlantillaDTO> plantillas = servicioPlantilla.listarPlantillas();
			return ResponseEntity.status(HttpStatus.OK)
								 .body(plantillas);
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// POST http://localhost:8080/plantillas
	@PostMapping
	public ResponseEntity<?> crear(@RequestHeader("Authorization") String token,
								   @RequestBody CrearPlantillaRequest body) {
		try {	
			String authToken = token != null ? token.replace("Bearer ", "") : "";
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);
				
			PlantillaDTO plantilla = servicioPlantilla.crearPlantilla(body.yaml(), usuario.getCorreo());
			return ResponseEntity.status(HttpStatus.OK)
								 .body(plantilla);
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
}
