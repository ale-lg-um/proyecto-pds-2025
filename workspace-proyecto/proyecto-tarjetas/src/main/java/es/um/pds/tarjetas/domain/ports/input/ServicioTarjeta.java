package es.um.pds.tarjetas.domain.ports.input;

import es.um.pds.tarjetas.domain.ports.input.commands.ContenidoTarjetaCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;

public interface ServicioTarjeta {
	TarjetaDTO crearTarjeta(String tableroId, String listaId, String nombre, ContenidoTarjetaCmd cmd);							// Crear una tarjeta dentro de una lista
	void editarContenidoTarjeta(String tableroId, String listaId, String tarjetaId, ContenidoTarjetaCmd cmd);					// Modificar el contenido de la tarjeta. Se puede cambiar de tarea a checklist o viceversa
	void renombrarTarjeta(String tableroId, String listaId, String tarjetaId, String nuevoNombre, String emailUsuario);			// Renombrar tarjeta
	void eliminarTarjeta(String tableroId, String listaId, String tarjetaId, String emailUsuario);								// Eliminar una tarjeta de una lista
	void moverTarjeta(String tableroId, String tarjetaId, String listaOrigenId, String listaDestinoId, String emailUsuario);	// Mover la tarjeta a una lista especial
	void completarTarjeta(String tableroId, String listaId, String tarjetaId, String emailUsuario);								// Marcar una tarjeta como completada (en realidad es moverla a la lista especial)
	
	// Etiquetas
	void addEtiquetaATarjeta(String tableroId, String listaId, String tarjetaId, String nombre, String color, String emailUsuario);															// Añadir una etiqueta (nombre y color) a una tarjeta de un tablero
	void eliminarEtiquetaDeTarjeta(String tableroId, String listaId, String tarjetaId, String nombre, String color, String emailUsuario);													// Eliminar una etiqueta
	void modificarEtiquetaEnTarjeta(String tableroId, String listaId, String tarjetaId, String nombreOld, String colorOld, String nombreNuevo, String colorNuevo, String emailUsuario);		// Modificar una etiqueta
}
