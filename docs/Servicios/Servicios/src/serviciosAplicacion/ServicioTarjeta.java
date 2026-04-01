package serviciosAplicacion;

public interface ServicioTarjeta {
	// La tarjeta se crea dentro de una lista
	TarjetaDTO crearTarjeta(String tableroId, String listaId, String nombre, ContenidoTarjetaCmd cmd);
	// Modificar el contenido de la tarjeta. Se puede cambiar de tarea a checklist o viceversa
	void editarContenidoTarjeta(String tableroId, String listaId, String tarjetaId, ContenidoTarjetaCmd cmd);
	void renombrarTarjeta(String tableroId, String listaId, String tarjetaId, String nuevoNombre, String emailUsuario);
	void eliminarTarjeta(String tableroId, String listaId, String tarjetaId, String emailUsuario);
	// Mover la tarjeta a una lista concreta
	void moverTarjeta(String tableroId, String tarjetaId, String listaOrigenId, String listaDestinoId, String emailUsuario);
	// Marcar una tarjeta como completada (en realidad es como moverla a la lista especial)
	void completarTarjeta(String tableroId, String listaId, String tarjetaId, String emailUsuario);
	
	// ETIQUETAS
	// Añadir una etiqueta (nombre y color) a una tarjeta de un tablero
	void addEtiquetaATarjeta(String tableroId, String listaId, String tarjetaId, String nombre, String color, String emailUsuario);
	void eliminarEtiquetaDeTarjeta(String tableroId, String listaId, String tarjetaId, String nombre, String color, String emailUsuario);
	void modificarEtiquetaEnTarjeta(String tableroId, String listaId, String tarjetaId, String nombreOld, String colorOld, String nombreNuevo, String colorNuevo, String emailUsuario);
}
