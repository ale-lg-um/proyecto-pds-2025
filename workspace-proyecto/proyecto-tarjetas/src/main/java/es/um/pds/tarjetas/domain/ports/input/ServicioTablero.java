package es.um.pds.tarjetas.domain.ports.input;

import java.time.LocalDateTime;

import es.um.pds.tarjetas.domain.ports.input.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.ResultadoCrearTableroDTO;

public interface ServicioTablero {
	// Se trata de una operación compleja, por lo que se usa un Command
	ResultadoCrearTableroDTO crearTablero(CrearTableroCmd cmd);
	void renombrarTablero(String tableroId, String nombreNuevo, String emailUsuario);
	void eliminarTablero(String tableroId);								
	void bloquearTablero(String tableroId, LocalDateTime desde, LocalDateTime hasta, String motivo, String emailUsuario);
	void desbloquearTablero(String tableroId, String emailUsuario);
	// Configurar límite N a nivel de tablero
	void configurarLimiteTablero(String tableroId, Integer limite, String emailUsuario);									

}
