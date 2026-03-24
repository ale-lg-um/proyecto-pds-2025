package es.um.pds.tarjetas.application.usecases.historial;

import org.springframework.stereotype.Service;

import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.application.common.PageRequest;
import es.um.pds.tarjetas.domain.model.entryHistorial.EntryHistorial;
import es.um.pds.tarjetas.domain.model.tablero.TableroId;
import es.um.pds.tarjetas.domain.ports.input.historial.ServicioHistorial;
import es.um.pds.tarjetas.domain.ports.output.historial.RepositorioEntryHistorial;

@Service
public class ServicioHistorialImpl implements ServicioHistorial {
	
	private final RepositorioEntryHistorial repoEntriesHistorial;
	
	public ServicioHistorialImpl(RepositorioEntryHistorial repoEntriesHistorial) {
		this.repoEntriesHistorial = repoEntriesHistorial;
	}

	/*
	@Override
	public EntryHistorialId crear(TableroId tableroId, TipoEntryHistorial tipo, UsuarioId usuario, String detalles) {
		
		// Generar Id de la EntryHistorial
		EntryHistorialId nuevoId = EntryHistorialId.of();
		
		String timestamp = LocalDateTime.now().toString();
		
		EntryHistorial nueva = 
		
		this.repoEntriesHistorial.guardar(nueva);
	}
	*/
	
	@Override
	public void append(EntryHistorial entry) {
		this.repoEntriesHistorial.guardar(entry);
	}

	@Override
	public Page<EntryHistorial> consultarPorTablero(TableroId tablero, PageRequest pageRequest) {
		// TODO Auto-generated method stub
	}
	
	
}
