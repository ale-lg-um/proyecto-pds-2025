package es.um.pds.tarjetas.domain.ports.output;

import java.util.List;
import java.util.Optional;

import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.application.common.PageRequest;

public interface RepositorioTarjetas {

	void guardar(Tarjeta tarjeta);

	Optional<Tarjeta> buscarPorId(TarjetaId tarjetaId);

	List<Tarjeta> buscarPorListaId(ListaId listaId);

	List<Tarjeta> buscarPorTableroId(TableroId tableroId);

	void eliminarPorId(TarjetaId tarjetaId);

	void eliminarPorListaId(ListaId listaId);

	void eliminarPorTableroId(TableroId tableroId);

	// El filtrado se hace solamente por nombre, no por (nombre, color)
	Page<Tarjeta> filtrarPorEtiquetas(
			TableroId tableroId,
			List<String> nombresEtiquetas,
			ModoFiltradoEtiquetas modo,
			PageRequest pageRequest
    );
}