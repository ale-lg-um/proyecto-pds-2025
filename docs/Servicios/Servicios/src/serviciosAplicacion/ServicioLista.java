package serviciosAplicacion;

public interface ServicioLista {
	ListaDTO crearLista(String tableroId, String nombre, String emailUsuario);
	void renombrarLista(String tableroId, String listaId, String nuevoNombre, String emailUsuario);
	void eliminarLista(String tableroId, String listaId, String emailUsuario);
	void definirListaEspecial(String tableroId, String listaId, String emailUsuario);
	// Configuramos el número de elementos que puede tener una determinada lista
	void configurarLimiteLista(String tableroId, String listaId, Integer limite, String emailUsuario);
	// Configuramos los prerrequisitos de una lista
	void configurarPrerrequisitosLista(String tableroId, String listaId, Set<String> prerrequisitos, String emailUsuario);
}
