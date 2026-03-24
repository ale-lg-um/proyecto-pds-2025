package es.um.pds.tarjetas;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;

@Repository
public class InMemoryRepositorioListas implements RepositorioListas {
	private final Map<ListaId, Lista> listas = new HashMap<>();
	
	@Override
	public void guardar(Lista lista) {
		listas.put(lista.getIdentificador(), lista);
	}
	
	@Override
	public Optional<Lista> buscarPorId(ListaId id) {
		return Optional.ofNullable(listas.get(id));
	}
	
	@Override
	public void eliminarPorId(ListaId id) {
		listas.remove(id);
	}
	
	@Override
	public void eliminarPorTableroId(TableroId id) {
		listas.values().removeIf(lista -> lista.getTableroId().equals(id));
	}
}
