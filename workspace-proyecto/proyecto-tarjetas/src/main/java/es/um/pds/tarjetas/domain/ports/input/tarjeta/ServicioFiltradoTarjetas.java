package es.um.pds.tarjetas.domain.ports.input.tarjeta;

import java.util.List;

import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.application.common.PageRequest;
import es.um.pds.tarjetas.domain.model.tablero.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.Tarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.Etiqueta;
import es.um.pds.tarjetas.domain.ports.output.tarjeta.ModoFiltradoEtiquetas;

public interface ServicioFiltradoTarjetas {
	Page<Tarjeta> filtrarPorEtiquetas(TableroId tableroId, List<Etiqueta> etiquetas, ModoFiltradoEtiquetas modo, PageRequest page);
}
