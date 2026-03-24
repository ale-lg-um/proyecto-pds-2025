package es.um.pds.tarjetas.domain.ports.input;

import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.application.common.PageRequest;
import es.um.pds.tarjetas.domain.model.entryHistorial.EntryHistorial;
import es.um.pds.tarjetas.domain.model.entryHistorial.EntryHistorialId;
import es.um.pds.tarjetas.domain.model.entryHistorial.TipoEntryHistorial;
import es.um.pds.tarjetas.domain.model.tablero.TableroId;
import es.um.pds.tarjetas.domain.model.usuario.UsuarioId;

public interface ServicioHistorial {
	// Generar la EntryHistorial (quizá innecesario)
	//EntryHistorialId crear(TableroId tableroId, TipoEntryHistorial tipo, UsuarioId usuario, String detalles);
	
	// Registrar en la BD la entry
	void append(EntryHistorial entry);
	
	// Servicio de lectura de las entries por tablero
	Page<EntryHistorial> consultarPorTablero(TableroId tablero, PageRequest pageRequest);
}
