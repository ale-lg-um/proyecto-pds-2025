package es.um.pds.tarjetas.domain.ports.output;

import java.util.List;
import java.util.Optional;

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;

public interface RepositorioListas {

	void guardar(Lista lista);

	Optional<Lista> buscarPorId(ListaId listaId);

	List<Lista> buscarPorTableroId(TableroId tableroId);

	void eliminarPorId(ListaId listaId);

	void eliminarPorTableroId(TableroId tableroId);
}