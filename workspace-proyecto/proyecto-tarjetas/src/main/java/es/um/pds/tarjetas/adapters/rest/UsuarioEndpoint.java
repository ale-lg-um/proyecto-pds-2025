package es.um.pds.tarjetas.adapters.rest;

import org.springframework.http.HttpStatus;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioAutenticacion;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;

@RestController
@RequestMapping("/usuarios")
public class UsuarioEndpoint {
	
	private final ServicioAutenticacion servicioAutenticacion;
	private final ServicioSesion servicioSesion;
	
	public UsuarioEndpoint(ServicioAutenticacion servicioAutenticacion, ServicioSesion servicioSesion) {
		this.servicioAutenticacion = servicioAutenticacion;
		this.servicioSesion = servicioSesion;
	}
	
	// POST http://localhost:8080/usuarios/{usuarioId}/login
	@PostMapping("/{usuarioId}/login")
	public ResponseEntity<Void> login(@PathVariable UsuarioId usuarioId) {
		servicioAutenticacion.enviarCodigoLogin(usuarioId.getCorreo());
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	// POST http://localhost:8080/usuarios/{usuarioId}/verificar?codigo={codigo}
	@PostMapping("/{usuarioId}/verificar")
	public ResponseEntity<?> verificarToken(@PathVariable UsuarioId usuarioId, @RequestParam String codigo) {
		try {
			String new_token = servicioAutenticacion.verificarCodigoLogin(usuarioId.getCorreo(), codigo);
			return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.AUTHORIZATION, "Bearer " + new_token).body("Login exitoso");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
	
	// GET http://localhost:8080/usuarios/validar
	@GetMapping("/validar")
	public ResponseEntity<?> validarYRenovar(@RequestHeader("Authorization") String token) {
		try {
			UsuarioId usuarioId = servicioSesion.validarYRenovarToken(token.replace("Bearer ", "").trim());
			return ResponseEntity.status(HttpStatus.OK).header("Access-Control-Expose-Headers", "New-Token").header("New-Token", token).body(usuarioId);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

}
