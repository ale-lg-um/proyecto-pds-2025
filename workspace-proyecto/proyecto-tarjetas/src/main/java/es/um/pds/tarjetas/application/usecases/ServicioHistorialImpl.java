package es.um.pds.tarjetas.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.application.common.PageRequest;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.EntryHistorial;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.ports.input.ServicioHistorial;
import es.um.pds.tarjetas.domain.ports.input.dto.EntryHistorialDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.PageDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioEntryHistorial;

@Service
public class ServicioHistorialImpl implements ServicioHistorial {

	// Constantes
	private static final int MAX_TAMANO_PAGINA = 20;

	// Inyectamos dependencias estrictas (patrón fachada)
	private final RepositorioEntryHistorial repoHistorial;

	// Constructor
	public ServicioHistorialImpl(RepositorioEntryHistorial repoHistorial) {
		this.repoHistorial = repoHistorial;
	}

	@Override
	@Transactional(readOnly = true)
	public PageDTO<EntryHistorialDTO> consultarPorTablero(String tableroId, int pagina, int tamano) {

		// 1. Validaciones de frontera
		if (tamano > MAX_TAMANO_PAGINA) {
			throw new IllegalArgumentException("El tamaño máximo de página es " + MAX_TAMANO_PAGINA);
		}

		// 2. Validación y construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		PageRequest pageRequest = new PageRequest(pagina, tamano);

		// 3. Recuperar raíz del agregado
		Page<EntryHistorial> page = repoHistorial.consultarPorTablero(idTablero, pageRequest);

		// 4. Devolver DTO de salida
		// Esta función convierte de Page<EntryHistorial> a PageDTO<EntryHistorialDTO>
		return PageDTO.fromPage(page, EntryHistorialDTO::new);
	}
}