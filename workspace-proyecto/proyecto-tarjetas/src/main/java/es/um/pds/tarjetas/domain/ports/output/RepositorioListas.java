package es.um.pds.tarjetas.domain.ports.output;

import java.util.Optional;
import java.util.Set;

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;

public interface RepositorioListas {

	void guardar(Lista lista);

	Optional<Lista> buscarPorId(ListaId listaId);

	Set<Lista> buscarPorIds(Set<ListaId> ids);
	
	Set<Lista> buscarPorTableroId(TableroId tableroId);

	void eliminarPorId(ListaId listaId);

	void eliminarPorTableroId(TableroId tableroId);
}