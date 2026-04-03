package es.um.pds.tarjetas.infrastructure.adapters.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.application.common.PageRequest;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.ports.output.ModoFiltradoEtiquetas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;

@Repository
public class RepositorioTarjetasMemoria implements RepositorioTarjetas {
	// Atributos
	// Mapea el ID de la tarjeta con su tarjeta
	private final Map<TarjetaId, Tarjeta> tarjetas = new HashMap<>();

	@Override
	public void guardar(Tarjeta tarjeta) {
		tarjetas.put(tarjeta.getIdentificador(), tarjeta);
	}

	@Override
	public Optional<Tarjeta> buscarPorId(TarjetaId id) {
		return Optional.ofNullable(tarjetas.get(id));
	}

	@Override
	public List<Tarjeta> buscarPorListaId(ListaId id) {
		List<Tarjeta> lista = new ArrayList<>();
		for (Tarjeta t : tarjetas.values()) {
			if (t.getListaActual().equals(id)) {
				lista.add(t);
			}
		}
		return lista;
	}

	@Override
	public List<Tarjeta> buscarPorTableroId(TableroId id) {
		List<Tarjeta> lista = new ArrayList<>();
		for (Tarjeta t : tarjetas.values()) {
			if (t.getTablero().equals(id)) {
				lista.add(t);
			}
		}
		return lista;
	}

	@Override
	public void eliminarPorId(TarjetaId id) {
		tarjetas.remove(id);
	}

	@Override
	public void eliminarPorListaId(ListaId id) {
		tarjetas.values().removeIf(t -> t.getListaActual().equals(id));
	}

	@Override
	public void eliminarPorTableroId(TableroId id) {
		tarjetas.values().removeIf(t -> t.getTablero().equals(id));
	}

	// Coge las tarjetas que cumplan ciertas etiquetas y las devuelve paginadas
	@Override
	public Page<Tarjeta> filtrarPorEtiquetas(TableroId tableroId, List<String> nombresEtiquetas,
			ModoFiltradoEtiquetas modo, PageRequest request) {

		// Normalizar etiquetas de entrada
		List<String> nombresNormalizados = nombresEtiquetas.stream().map(String::trim).toList();

		// Recorrer todas las tarjetas, filtrarlas por tablero y después por etiquetas. Quedan recogidas en una List<Tarjeta>
		List<Tarjeta> lista = tarjetas.values().stream()
				.filter(t -> t.getTablero() != null && t.getTablero().equals(tableroId)).filter(t -> {
					List<String> nombresTarjeta = t.getEtiquetas().stream().map(et -> et.nombre().trim()).toList();

					// En función del modo se devuelven unas tarjetas u otras
					return modo == ModoFiltradoEtiquetas.AND
							? nombresNormalizados.stream().allMatch(nombresTarjeta::contains)
							: nombresNormalizados.stream().anyMatch(nombresTarjeta::contains);
				}).toList();

		// Paginación: total de elementos y número de páginas
		int nElementos = lista.size();
		int nPags = (int) Math.ceil((double) nElementos / request.getSize());
		
		// Calcular el rango de inicio con la petición de página
		int inicio = request.getPage() * request.getSize();
		if (inicio >= nElementos) {
			return new Page<>(List.of(), request.getPage(), request.getSize(), nPags, nElementos);
		}

		// Obtener la sublista de tarjetas
		int fin = Math.min(inicio + request.getSize(), nElementos);
		List<Tarjeta> contenido = lista.subList(inicio, fin);

		// Crear la Page
		return new Page<>(contenido, request.getPage(), request.getSize(), nPags, nElementos);
	}
}
