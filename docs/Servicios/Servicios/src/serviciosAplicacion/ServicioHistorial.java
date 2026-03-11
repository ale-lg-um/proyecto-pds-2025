package serviciosAplicacion;

public class ServicioHistorial {
	void append(EntryHistorial entry);
	Page<EntryHistorial> consultarPorTablero(TableroId tableroId, PageRequest pageRequest);
}
