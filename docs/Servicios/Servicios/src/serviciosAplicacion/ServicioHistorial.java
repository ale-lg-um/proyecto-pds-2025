package serviciosAplicacion;

public interface ServicioHistorial {
	EntryHistorialId crear(TableroId tableroId, TipoEntryHistorial tipo, String detalles, UsuarioId usuario);
	Page<EntryHistorial> consultarPorTablero(TableroId tableroId, PageRequest pageRequest);
}
