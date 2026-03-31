package es.um.pds.tarjetas.domain.ports.input;

import java.time.LocalDateTime;

import es.um.pds.tarjetas.domain.ports.input.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.ResultadoCrearTableroDTO;

public interface ServicioTablero {
	// TODO Actualizar DDD si es necesario en este servicio y todos
	
	ResultadoCrearTableroDTO crearTablero(CrearTableroCmd cmd);																// Se trata de una operación compleja, por lo que se usa un Command
	void renombrarTablero(String tableroId, String nombreNuevo, String emailUsuario);										// Renombrar el tablero
	void eliminarTablero(String tableroId);																					// Eliminar el tablero
	void bloquearTablero(String tableroId, LocalDateTime desde, LocalDateTime hasta, String motivo, String emailUsuario);	// Bloquear el tablero
	void desbloquearTablero(String tableroId, String emailUsuario);															// Desbloquear un tablero bloqueado
	void configurarLimiteTablero(String tableroId, Integer limite, String emailUsuario);									// Configurar límite N a nivel de tablero

}
