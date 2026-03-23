package serviciosAplicacion;

public interface ServicioHistorial {
	void append(EntryHistorial entry);
	Page<EntryHistorial> consultarPorTablero(TableroId tableroId, PageRequest pageRequest);
}
