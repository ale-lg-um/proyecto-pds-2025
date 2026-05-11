package es.um.pds.tarjetas.domain.ports.input;

import java.util.List;

import es.um.pds.tarjetas.domain.ports.input.dto.PageDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
import es.um.pds.tarjetas.domain.ports.output.ModoFiltradoEtiquetas;

public interface ServicioFiltradoTarjetas {

	PageDTO<TarjetaDTO> filtrarPorEtiquetas(String tableroId, List<String> nombresEtiquetas, ModoFiltradoEtiquetas modo,
			int pagina, int tamano);
}