package es.um.pds.tarjetas.domain.ports.input;

import java.time.LocalDateTime;
import java.util.List;


import es.um.pds.tarjetas.domain.ports.input.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.ResultadoCrearTableroDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;

public interface ServicioGestionTablero {
	// TODO Actualizar DDD si es necesario en este servicio y todos
	// Métodos
	
	// Tablero
	ResultadoCrearTableroDTO crearTablero(CrearTableroCmd cmd);																// Se trata de una operación compleja, por lo que se usa un Command
	void renombrarTablero(String tableroId, String nombreNuevo, String emailUsuario);										// Renombrar el tablero
	void eliminarTablero(String tableroId, String emailUsuario);															// Eliminar el tablero
	void bloquearTablero(String tableroId, LocalDateTime desde, LocalDateTime hasta, String motivo, String emailUsuario);	// Bloquear el tablero
	void desbloquearTablero(String tableroId, String emailUsuario);															// Desbloquear un tablero bloqueado
	
	// Listas
	ListaDTO crearLista(String tableroId, String nombre, String emailUsuario);													// Crear una lista dentro de un tablero
	void renombrarLista(String tableroId, String listaId, String nuevoNombre, String emailUsuario);								// Cambiar el nombre de una lista dentro de un tablero
	void eliminarLista(String tableroId, String listaId, String emailUsuario);													// Eliminar una lista de un tablero
	void definirListaEspecial(String tableroId, String listaId, String emailUsuario);											// Marcamos una lista de un tablero como especial
	void configurarLimiteLista(String tableroId, String listaId, int limite, String emailUsuario);								// Configuramos el número de elementos que puede tener una determinada lista
	void configurarPrerrequisitosLista(String tableroId, String listaId, List<String> prerrequisitoIds, String emailUsuario);	// Configuramos los prerrequisitos de una lista
	
	// Tarjetas
	TarjetaDTO crearTarjeta(String tableroId, String listaId, TarjetaDTO tarjeta, String emailUsuario);				// Crear una tarjeta dentro de una lista
	void editarTarjeta(String tableroId, String tarjetaId, TarjetaDTO tarjetaActualizada, String emailUsuario);		// Modificar el contenido de la tarjeta. No se puede cambiar de Tarea a Checklist o viceversa
	void eliminarTarjeta(String tableroId, String tarjetaId, String emailUsuario);									// Eliminar una tarjeta de una lista
	void moverTarjeta(String tableroId, String tarjetaId, String listaDestinoId, String emailUsuario);				// Mover la tarjeta a una lista especial
	void completarTarjeta(String tableroId, String tarjetaId, String emailUsuario);									// Marcar una tarjeta como completada (en realidad es moverla a la lista especial)
	
	// Etiquetas
	void addEtiquetaATarjeta(String tableroId, String tarjetaId, String nombre, String color, String emailUsuario);															// Añadir una etiqueta (nombre y color) a una tarjeta de un tablero
	void eliminarEtiquetaDeTarjeta(String tableroId, String tarjetaId, String nombre, String color, String emailUsuario);													// Eliminar una etiqueta
	void modificarEtiquetaEnTarjeta(String tableroId, String tarjetaId, String nombreOld, String colorOld, String nombreNuevo, String colorNuevo, String emailUsuario);		// Modificar una etiqueta
}
