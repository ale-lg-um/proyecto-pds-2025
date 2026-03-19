package es.um.pds.tarjetas.domain.ports.output.tarjeta;

import java.util.List;
import java.util.Optional;

import es.um.pds.tarjetas.domain.model.tarjeta.Etiqueta;
import es.um.pds.tarjetas.domain.model.tarjeta.Tarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.TarjetaId;
import es.um.pds.tarjetas.domain.model.lista.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.TableroId;
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

	// TODO Modelar clases Page y PageRequest
	Page<Tarjeta> filtrarPorEtiquetas(
			TableroId tableroId,
			List<Etiqueta> etiquetas,
			ModoFiltradoEtiquetas modo,
			PageRequest pageRequest
    );
}