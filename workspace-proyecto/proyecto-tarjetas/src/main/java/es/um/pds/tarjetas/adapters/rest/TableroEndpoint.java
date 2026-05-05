package es.um.pds.tarjetas.adapters.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.um.pds.tarjetas.adapters.rest.requests.BloqueoRequest;
import es.um.pds.tarjetas.adapters.rest.requests.FiltradoRequest;
import es.um.pds.tarjetas.adapters.rest.requests.LimiteRequest;
import es.um.pds.tarjetas.adapters.rest.requests.RenombrarRequest;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioFiltradoTarjetas;
import es.um.pds.tarjetas.domain.ports.input.ServicioHistorial;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;
import es.um.pds.tarjetas.domain.ports.input.ServicioTablero;
import es.um.pds.tarjetas.domain.ports.input.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.EntryHistorialDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.PageDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.ResultadoCrearTableroDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TableroDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;

@RestController
@RequestMapping("/tableros")
public class TableroEndpoint {
	
	private final ServicioTablero servicioTablero;
	private final ServicioSesion servicioSesion;
	private final ServicioHistorial servicioHistorial;
	private final ServicioFiltradoTarjetas servicioFiltradoTarjetas;
	private final RepositorioTableros repositorioTableros;
	
	public TableroEndpoint(ServicioTablero servicioTablero, ServicioSesion servicioSesion, ServicioHistorial servicioHistorial, ServicioFiltradoTarjetas servicioFiltradoTarjetas, RepositorioTableros repositorioTableros) {
		this.servicioTablero = servicioTablero;
		this.servicioSesion = servicioSesion;
		this.servicioHistorial = servicioHistorial;
		this.servicioFiltradoTarjetas = servicioFiltradoTarjetas;
		this.repositorioTableros = repositorioTableros;
	}
	
	// GET http://localhost:8080/tableros?url={url}
	@GetMapping
	public ResponseEntity<?> obtener(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token,
									 @RequestParam(required = false) String url) {
		try {	
			String authToken = token != null ? token.replace("Bearer ", "") : "";
			UsuarioId usuarioId = servicioSesion.validarYRenovarToken(authToken);
			
			if(url != null) {
				Optional<Tablero> tableroOptional = repositorioTableros.buscarPorURL(url);
				
				if(tableroOptional.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
				}
				
				TableroDTO tablero = new TableroDTO(tableroOptional.get());
				return ResponseEntity.status(HttpStatus.OK).body(tablero);
			}
			
			List<TableroId> tablerosId = repositorioTableros.listarIdsPorUsuario(usuarioId);
			
			List<TableroDTO> tableros = tablerosId.stream()
												  .map(id -> repositorioTableros.buscarPorId(id))
												  .flatMap(Optional::stream)
												  .map(tablero -> new TableroDTO(tablero))
												  .toList();
			return ResponseEntity.status(HttpStatus.OK)
								 .body(tableros);
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// POST http://localhost:8080/tableros
	@PostMapping
	public ResponseEntity<?> crear(@RequestHeader("Authorization") String token,
								   @RequestBody CrearTableroCmd body) {
		try {	
			String authToken = token.replace("Bearer ", "").trim();
			servicioSesion.validarYRenovarToken(authToken);
			ResultadoCrearTableroDTO tablero = servicioTablero.crearTablero(body);
			return ResponseEntity.status(HttpStatus.CREATED)
								 .body(tablero);
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					 			 .body(e.getMessage());
		}
	}
	
	// PUT http://localhost:8080/tableros/{talberoId}/renombrar
	@PutMapping("/{tableroId}/renombrar")
	public ResponseEntity<?> renombrar(@RequestHeader("Authorization") String token,
			  							  @PathVariable String tableroId,
										  @RequestBody RenombrarRequest body) {
		try {	
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);
			servicioTablero.renombrarTablero(tableroId, body.nuevoNombre(), usuario.getCorreo());
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					             .build();
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// DELETE http://localhost:8080/tableros/{tableroId}
	@DeleteMapping("/{tableroId}")
	public ResponseEntity<?> eliminar(@RequestHeader("Authorization") String token,
										 @PathVariable String tableroId) {
		try {	
			String authToken = token.replace("Bearer ", "").trim();
			servicioSesion.validarYRenovarToken(authToken);
			servicioTablero.eliminarTablero(tableroId);
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PUT http://localhost:8080/tableros/{tableroId}/bloquear
	@PutMapping("/{tableroId}/bloquear")
	public ResponseEntity<?> bloquear(@RequestHeader("Authorization") String token,
										 @PathVariable String tableroId,
										 @RequestBody BloqueoRequest body) {
		try {	
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);
			servicioTablero.bloquearTablero(tableroId, body.inicio(), body.fin(), body.motivo(), usuario.getCorreo());
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// DELETE http://localhost:8080/tableros/{tableroId}/bloquear
	@DeleteMapping("/{tableroId}/bloquear")
	public ResponseEntity<?> desbloquear(@RequestHeader("Authorization") String token,
										 @PathVariable String tableroId) {
		try {	
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);
			servicioTablero.desbloquearTablero(tableroId, usuario.getCorreo());
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// PATCH http://localhost:8080/tableros/{tableroId}/configurar
	@PatchMapping("/{tableroId}/configurar")
	public ResponseEntity<?> configurar(@RequestHeader("Authorization") String token,
										@PathVariable String tableroId,
										@RequestBody LimiteRequest body) {
		try {	
			String authToken = token.replace("Bearer ", "").trim();
			UsuarioId usuario = servicioSesion.validarYRenovarToken(authToken);
			servicioTablero.configurarLimiteTablero(tableroId, body.limite(), usuario.getCorreo());
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
								 .build();
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// GET http://localhost:8080/tableros/{tableroId}/historial?pagina=1&tamano=20
	@GetMapping("/{tableroId}/historial")
	public ResponseEntity<?> mostrarHistorial(@RequestHeader("Authorization") String token,
											  @PathVariable String tableroId,
											  @RequestParam(defaultValue = "1") int page,
											  @RequestParam(defaultValue = "20") int tamano) {
		try {	
			String authToken = token.replace("Bearer ", "").trim();
			servicioSesion.validarYRenovarToken(authToken);
			PageDTO<EntryHistorialDTO> historial = servicioHistorial.consultarPorTablero(tableroId, page - 1, tamano);
			return ResponseEntity.status(HttpStatus.OK)
								 .body(historial);
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
	
	// GET http://localhost:8080/tableros/{tableroId}/tarjetas
	@GetMapping("/{tableroId}/tarjetas")
	public ResponseEntity<?> filtrarTarjetas(@RequestHeader("Authorization") String token,
											 @PathVariable String tableroId,
											 @RequestBody FiltradoRequest body) {
		try {	
			String authToken = token.replace("Bearer ", "").trim();
			servicioSesion.validarYRenovarToken(authToken);
			PageDTO<TarjetaDTO> tarjetas = servicioFiltradoTarjetas.filtrarPorEtiquetas(tableroId, body.nombresEtiquetas(), body.modo(), body.pagina(), body.tamano());
			return ResponseEntity.status(HttpStatus.OK)
								 .body(tarjetas);
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body(e.getMessage());
		}
	}
}
