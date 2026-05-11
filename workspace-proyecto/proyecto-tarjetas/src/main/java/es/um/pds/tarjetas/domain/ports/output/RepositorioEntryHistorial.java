package es.um.pds.tarjetas.domain.ports.output;

import es.um.pds.tarjetas.common.pagination.Page;
import es.um.pds.tarjetas.common.pagination.PageRequest;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.EntryHistorial;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;

public interface RepositorioEntryHistorial {

	void guardar(EntryHistorial entry);

	Page<EntryHistorial> consultarPorTablero(TableroId tableroId, PageRequest pageRequest);

	void eliminarPorTableroId(TableroId tableroId);
}