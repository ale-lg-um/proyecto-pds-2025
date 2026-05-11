package es.um.pds.tarjetas.infrastructure.adapters.memory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

//import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;

//@Repository
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
	public Set<Lista> buscarPorTableroId(TableroId id) {
		Set<Lista> listas = new HashSet<>();
		for(Lista lista : baseDatos.values()) {
			if(lista.getTablero().equals(id)) {
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
		if (id == null) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser nulo");
		}

		baseDatos.values().removeIf(lista -> id.equals(lista.getTablero()));
	}

	@Override
	public Set<Lista> buscarPorIds(Set<ListaId> ids) {

		if (ids == null) {
			throw new IllegalArgumentException("El conjunto de IDs no puede ser nulo");
		}

		Set<Lista> resultado = new HashSet<>();

		for (ListaId id : ids) {
			if (id == null) {
				throw new IllegalArgumentException("No puede haber IDs nulos en el conjunto");
			}

			Lista lista = baseDatos.get(id);
			if (lista != null) {
				resultado.add(lista);
			}
		}

		return resultado;
	}
}
