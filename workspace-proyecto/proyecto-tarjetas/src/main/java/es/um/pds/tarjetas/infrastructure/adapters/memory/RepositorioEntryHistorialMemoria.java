package es.um.pds.tarjetas.infrastructure.adapters.memory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.application.common.PageRequest;
import es.um.pds.tarjetas.domain.model.entryHistorial.id.EntryHistorialId;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.EntryHistorial;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioEntryHistorial;

//@Repository
public class RepositorioEntryHistorialMemoria implements RepositorioEntryHistorial {

	private final Map<EntryHistorialId, EntryHistorial> entradas = new HashMap<>();

	@Override
	public void guardar(EntryHistorial entrada) {
		if (entrada == null) {
			throw new IllegalArgumentException("La entrada del historial no puede ser nula");
		}
		entradas.put(entrada.getIdentificador(), entrada);
	}

	// Hacer la consulta de entries ordenadas por timestamp
	@Override
	public Page<EntryHistorial> consultarPorTablero(TableroId id, PageRequest request) {
		if (id == null) {
			throw new IllegalArgumentException("El id del tablero no puede ser nulo");
		}
		if (request == null) {
			throw new IllegalArgumentException("La petición de página no puede ser nula");
		}

		// Obtener la List<EntryHistorial> ordenada por timestamp
		List<EntryHistorial> listaOrdenada = entradas.values().stream()
				.filter(entrada -> entrada.getTableroId().equals(id))
				.sorted(Comparator.comparing(EntryHistorial::getTimestamp).reversed()).toList();

		int totalElementos = listaOrdenada.size();
		int tamanoPagina = request.getSize();
		int paginaSolicitada = request.getPage();

		// Si no hay elementos totalPaginas = 0, si no, se calcula el total redondeando hacia arriba
		int totalPaginas = totalElementos == 0 ? 0 : (int) Math.ceil((double) totalElementos / tamanoPagina);

		int inicio = paginaSolicitada * tamanoPagina;

		// Si la página está fuera de rango, salta excepción
		if (inicio >= totalElementos) {
			throw new IllegalArgumentException("Página fuera de rango");
		}

		int fin = Math.min(inicio + tamanoPagina, totalElementos);
		List<EntryHistorial> contenido = listaOrdenada.subList(inicio, fin);

		return new Page<>(contenido, paginaSolicitada, tamanoPagina, totalPaginas, totalElementos);
	}

	@Override
	public void eliminarPorTableroId(TableroId id) {
		entradas.values().removeIf(e -> e.getTableroId().equals(id));
	}
}