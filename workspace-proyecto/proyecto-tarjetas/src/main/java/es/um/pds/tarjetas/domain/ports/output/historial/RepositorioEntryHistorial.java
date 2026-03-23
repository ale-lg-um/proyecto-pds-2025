package es.um.pds.tarjetas.domain.ports.output.historial;

import es.um.pds.tarjetas.domain.model.entryHistorial.EntryHistorial;
import es.um.pds.tarjetas.domain.model.tablero.TableroId;
import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.application.common.PageRequest;

public interface RepositorioEntryHistorial {

	void guardar(EntryHistorial entry);

	// TODO
	Page<EntryHistorial> consultarPorTablero(TableroId tableroId, PageRequest pageRequest);

	void eliminarPorTableroId(TableroId tableroId);
}