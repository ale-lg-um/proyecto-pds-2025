package serviciosAplicacion;

public interface ServicioGestionTablero {
	
	// TABLERO
	TableroId crearTablero(CrearTableroCmd cmd);
	void renombrarTablero(TableroId id, String nuevoNombre, Actor actor);
	void eliminarTablero(TableroId id, Actor actor);
	void bloquearTablero(TableroId id, EspecBloqueo espec, Actor actor);
	void desbloquearTablero(TableroId id, Actor actor);
	
	// LISTAS
	ListaId crearLista(TableroId id, String nombre, Actor actor);
	void renombrarLista(TableroId id, ListaId listaId, String nuevoNombre, Actor actor);
	void eliminarLista(TableroId id, ListaId listaId, Actor actor);
	void definirListaEspecial(TableroId id, ListaId listaId, Actor actor);
	void configurarLimiteLista(TableroId id, ListaId listaId, int limite, Actor actor);
	void configurarPrerrequisitosLista(TableroId id, ListaId listaId, List<ListaId> prerrequisitos, Actor actor);
	
	// TARJETAS
	TarjetaId crearTarjeta(TableroId id, ListaId listaId, ContenidoTarjeta contenido, Actor actor);
	void editarTarjeta(TableroId id, TarjetaId tarjetaId, ContenidoTarjeta nuevoContenido, Actor actor);
	void eliminarTarjeta(TableroId id, TarjetaId tarjetaId, Actor actor);
	void moverTarjeta(TableroId id, TarjetaId tarjetaId, ListaId listaDestino, Actor actor);
	// Realmente es mover una tarjeta a la lista especial
	void completarTarjeta(TableroId id, TarjetaId id, Actor actor);
	
	
	// ETIQUETAS
	void addEtiquetaATarjeta(TableroId id, TarjetaId tarjetaId, String nombre, String color, Actor actor);
    void eliminarEtiquetaDeTarjeta(TableroId id, TarjetaId tarjetaId, String nombre, String color, Actor actor);
    void modificarEtiquetaEnTarjeta(TableroId id, TarjetaId tarjetaId,
    		String nombreAntiguo, String colorAntiguo, String nombreNuevo, String colorNuevo,
    		Actor actor);
}
