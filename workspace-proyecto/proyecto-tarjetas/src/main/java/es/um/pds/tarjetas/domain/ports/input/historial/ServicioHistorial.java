package es.um.pds.tarjetas.domain.ports.input.historial;

import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.application.common.PageRequest;
import es.um.pds.tarjetas.domain.model.entryHistorial.EntryHistorial;
import es.um.pds.tarjetas.domain.model.tablero.TableroId;

public interface ServicioHistorial {
	// Generar la EntryHistorial
	void crear(EntryHistorial entry);
	
	// Servicio de lectura de las entries por tablero
	Page<EntryHistorial> consultarPorTablero(TableroId tablero, PageRequest pageRequest);
}
