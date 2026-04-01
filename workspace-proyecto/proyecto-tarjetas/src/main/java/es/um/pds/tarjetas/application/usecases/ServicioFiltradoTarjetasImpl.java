package es.um.pds.tarjetas.application.usecases;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.application.common.PageRequest;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.ports.input.ServicioFiltradoTarjetas;
import es.um.pds.tarjetas.domain.ports.input.dto.PageDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
import es.um.pds.tarjetas.domain.ports.output.ModoFiltradoEtiquetas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;

@Service
public class ServicioFiltradoTarjetasImpl implements ServicioFiltradoTarjetas {

    private static final int MAX_TAMANO_PAGINA = 20;

    private final RepositorioTarjetas repoTarjetas;

    public ServicioFiltradoTarjetasImpl(RepositorioTarjetas repoTarjetas) {
        this.repoTarjetas = repoTarjetas;
    }

	@Override
	@Transactional(readOnly = true)
	public PageDTO<TarjetaDTO> filtrarPorEtiquetas(String tableroId, List<String> nombresEtiquetas,
			ModoFiltradoEtiquetas modo, int pagina, int tamano) {

		// 1. Validaciones de frontera
		if (nombresEtiquetas == null || nombresEtiquetas.isEmpty()) {
			throw new IllegalArgumentException("Debe indicarse al menos una etiqueta para filtrar");
		}

		// Construimos una colección con las etiquetas normalizadas
		List<String> etiquetasNormalizadas = nombresEtiquetas.stream().map(nombre -> {
			if (nombre == null || nombre.isBlank()) {
				throw new IllegalArgumentException("Los nombres de etiqueta no pueden ser null o vacíos");
			}
			return nombre.trim();
		}).distinct().toList();

		if (pagina < 0) {
			throw new IllegalArgumentException("El número de página no puede ser negativo");
		}

		if (tamano <= 0) {
			throw new IllegalArgumentException("El tamaño de página debe ser positivo");
		}

		if (tamano > MAX_TAMANO_PAGINA) {
			throw new IllegalArgumentException("El tamaño máximo de página es " + MAX_TAMANO_PAGINA);
		}

		// 2. Validación y construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		ModoFiltradoEtiquetas modoEfectivo = (modo == null) ? ModoFiltradoEtiquetas.OR : modo;
		PageRequest pageRequest = new PageRequest(pagina, tamano);

		// 3. Recuperar la página
		Page<Tarjeta> page = repoTarjetas.filtrarPorEtiquetas(idTablero, etiquetasNormalizadas, modoEfectivo,
				pageRequest);

		// 4. Devolver en formato DTO
		return PageDTO.fromPage(page, TarjetaDTO::new);
	}
}