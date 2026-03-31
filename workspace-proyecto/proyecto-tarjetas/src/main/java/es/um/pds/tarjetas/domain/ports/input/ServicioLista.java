package es.um.pds.tarjetas.domain.ports.input;

import java.util.Set;

import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;

public interface ServicioLista {
	ListaDTO crearLista(String tableroId, String nombre, String emailUsuario);													// Crear una lista dentro de un tablero
	void renombrarLista(String tableroId, String listaId, String nuevoNombre, String emailUsuario);								// Cambiar el nombre de una lista dentro de un tablero
	void eliminarLista(String tableroId, String listaId, String emailUsuario);													// Eliminar una lista de un tablero
	void definirListaEspecial(String tableroId, String listaId, String emailUsuario);											// Marcamos una lista de un tablero como especial
	void configurarLimiteLista(String tableroId, String listaId, Integer limite, String emailUsuario);							// Configuramos el número de elementos que puede tener una determinada lista
	void configurarPrerrequisitosLista(String tableroId, String listaId, Set<String> prerrequisitos, String emailUsuario);		// Configuramos los prerrequisitos de una lista
}
