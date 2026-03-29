package es.um.pds.tarjetas.infrastructure.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;

@Repository
public class RepositorioListasMemoria implements RepositorioListas{
	// Atributos
	private final Map<ListaId, Lista> baseDatos = new HashMap<>();
	
	@Override
	public void guardar(Lista lista) {
		baseDatos.put(lista.getIdentificador(), lista);
	}
	
	@Override
	public Optional<Lista> buscarPorId(ListaId id) {
		return Optional.ofNullable(baseDatos.get(id));
	}
	
	@Override
	public List<Lista> buscarPorTableroId(TableroId id) {
		List<Lista> listas = new ArrayList<>();
		for(Lista lista : baseDatos.values()) {
			if(lista.getTablero().getIdentificador().equals(id)) {
				listas.add(lista);
			}
		}
		return listas;
	}
	
	@Override
	public void eliminarPorId(ListaId id) {
		baseDatos.remove(id);
	}
	
	@Override
	public void eliminarPorTableroId(TableroId id) {
		for(Lista lista : baseDatos.values()) {
			ListaId listaid = lista.getIdentificador();
			if(lista.getTablero().getIdentificador().equals(id)) {
				baseDatos.remove(listaid);
			}
		}
	}
}
