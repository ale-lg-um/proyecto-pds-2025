package es.um.pds.tarjetas.domain.ports.input.tablero;

import java.util.List;

import es.um.pds.tarjetas.domain.model.tablero.EspecBloqueo;
import es.um.pds.tarjetas.domain.model.tablero.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.ContenidoTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.TarjetaId;
import es.um.pds.tarjetas.domain.ports.input.tablero.commands.CrearTableroCmd;

public interface ServicioGestionTablero {
	// Métodos
	
	// Tablero
	TableroId crearTablero(CrearTableroCmd cmd) throws Exception;											// Se trata de una operación compleja, por lo que se usa un Command
	void renombrarTablero(TableroId tablero, String nombreNuevo, String emailUsuario) throws Exception;		// Renombrar el tablero
	void eliminarTablero(TableroId tablero, String emailUsuario) throws Exception;							// Eliminar el tablero
	void bloquearTablero(TableroId tablero, EspecBloqueo espec, String emailUsuario) throws Exception;		// Bloquear el tablero
	void desbloquearTablero(TableroId tablero, String emailUsuario) throws Exception;						// Desbloquear un tablero bloqueado
	
	// Listas
	ListaId crearLista(TableroId tablero, String nombre, String correoUsuario) throws Exception;												// Crear una lista dentro de un tablero
	void renombrarLista(TableroId tablero, ListaId lista, String nuevoNombre, String correoUsuario) throws Exception;							// Cambiar el nombre de una lista dentro de un tablero
	void eliminarLista(TableroId tablero, ListaId lista, String correoUsuario) throws Exception;												// Eliminar una lista de un tablero
	void definirListaEspecial(TableroId tablero, ListaId lista, String correoUsuario) throws Exception;											// Marcamos una lista de un tablero como especial
	void configurarLimiteLista(TableroId tablero, ListaId lista, int limite, String correoUsuario) throws Exception;							// Configuramos el número de elementos que puede tener una determinada lista
	void configurarPrerrequisitosLista(TableroId tablero, ListaId lista, List<ListaId> prerrequisitos, String correoUsuario) throws Exception;	// Configuramos los prerrequisitos de una lista
	
	// Tarjetas
	TarjetaId crearTarjeta(TableroId tablero, ListaId lista, ContenidoTarjeta contenido, String correoUsuario) throws Exception;		// Creamos la tarjeta
	void editarTarjeta(TableroId tablero, TarjetaId tarjeta, ContenidoTarjeta contenidoNuevo, String correoUsuario) throws Exception;	// Modificar el contenido de la tarjeta
	void eliminarTarjeta(TableroId tablero, TarjetaId tarjeta, String correoUsuario) throws Exception;									// Eliminar una tarjeta
	void moverTarjeta(TableroId tablero, TarjetaId tarjeta, ListaId lista, String correoUsuario) throws Exception;						// Mover la tarjeta a una lista especial
	void completarTarjeta(TableroId tablero, TarjetaId tarjeta, String correoUsuario) throws Exception;									// Marcar una tarjeta como completada
	
	// Etiquetas
	void addEtiquetaATarjeta(TableroId tablero, TarjetaId tarjeta, String nombre, String color, String correoUsuario) throws Exception;																// Añadir una etiqueta (nombre y color) a una tarjeta de un tablero
	void eliminarEtiquetaDeTarjeta(TableroId tablero, TarjetaId tarjeta, String nombre, String color, String correoUsuario) throws Exception;														// Eliminar una etiqueta
	void modificarEtiquetaEnTarjeta(TableroId tablero, TarjetaId tarjeta, String nombreOld, String colorOld, String nombreNuevo, String colorNuevo, String correoUsuario) throws Exception;			// Modificar una etiqueta
}
