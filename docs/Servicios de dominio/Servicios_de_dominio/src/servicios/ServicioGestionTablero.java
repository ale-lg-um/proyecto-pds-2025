package servicios;

public class ServicioGestionTablero {
	TableroId crearTablero(CrearTableroCmd cmd)
	void bloquearTablero(TableroId id, EspecBloqueo espec, Actor actor)
	void desbloquearTablero(TableroId id, Actor actor)
	TarjetaId addTarjeta(TableroId id, ListaId listaId, ContenidoTarjeta contenido, Actor actor)
	void moverTarjeta(TableroId id, TarjetaId id, ListId listaDestino, Actor actor)
	void completarTarjeta(TableroId id, TarjetaId id, Actor actor)
	void configurarLimiteLista(TableroId id, ListaId listaId, int limite, Actor actor)
	void configurarPrerrequisitosLista(TableroId id, ListaId listaId, List<ListaId> prerrequisitos, Actor actor)
}
