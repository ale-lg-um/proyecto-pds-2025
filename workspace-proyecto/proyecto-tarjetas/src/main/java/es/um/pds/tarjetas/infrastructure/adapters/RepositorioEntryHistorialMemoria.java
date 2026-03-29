package es.um.pds.tarjetas.infrastructure.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.application.common.PageRequest;
import es.um.pds.tarjetas.domain.model.entryHistorial.id.EntryHistorialId;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.EntryHistorial;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioEntryHistorial;

@Repository
public class RepositorioEntryHistorialMemoria implements RepositorioEntryHistorial {
	// Atributos
	private final Map<EntryHistorialId, EntryHistorial> entradas = new HashMap<>();
	
	@Override
	public void guardar(EntryHistorial entrada) {
		entradas.put(entrada.getIdentificador(), entrada);
	}
	
	@Override
	public Page<EntryHistorial> consultarPorTablero(TableroId id, PageRequest request) {
		List<EntryHistorial> lista = new ArrayList<>();
		for(EntryHistorial entrada : entradas.values()) {
			if(entrada.getTableroId().equals(id)) {
				lista.add(entrada);
			}
		}
		
		int nElementos = lista.size();
		int nPags = (int) Math.ceil((double) nElementos / request.getSize());
		
		int inicio = request.getPage() * request.getSize();
		int fin = Math.min(inicio + request.getSize(), nElementos);
		
		List<EntryHistorial> contenido = lista.subList(inicio, fin);
		
		return new Page<>(contenido, request.getPage(), request.getSize(), nPags, nElementos);
	}
	
	@Override
	public void eliminarPorTableroId(TableroId id) {
		entradas.values().removeIf(e -> e.getTableroId().equals(id));
	}
}
