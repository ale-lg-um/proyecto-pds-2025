package es.um.pds.tarjetas.infrastructure.adapters;

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
import es.um.pds.tarjetas.domain.model.tarjeta.model.Etiqueta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.ports.output.ModoFiltradoEtiquetas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;

@Repository
public class RepositorioTarjetasMemoria implements RepositorioTarjetas {
	// Atributos
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
		for(Tarjeta t : tarjetas.values()) {
			if(t.getListaActual().equals(id)) {
				lista.add(t);
			}
		}
		return lista;
	}
	
	@Override
	public List<Tarjeta> buscarPorTableroId(TableroId id) {
		List<Tarjeta> lista = new ArrayList<>();
		for(Tarjeta t : tarjetas.values()) {
			if(t.getTablero().equals(id)) {
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
	
	@Override
	public Page<Tarjeta> filtrarPorEtiquetas(TableroId id, List<Etiqueta> etiquetas, ModoFiltradoEtiquetas modo, PageRequest request) {
		List<Tarjeta> lista = new ArrayList<>();
		for(Tarjeta tarjeta : tarjetas.values()) {
			boolean cumple = modo == ModoFiltradoEtiquetas.AND ? tarjeta.getEtiquetas().containsAll(etiquetas) : tarjeta.getEtiquetas().stream().anyMatch(etiquetas::contains);
			
			if(cumple) {
				lista.add(tarjeta);
			}
		}
		
		int nElementos = lista.size();
		int nPags = (int) Math.ceil((double) nElementos / request.getSize());
		
		int inicio = request.getPage() * request.getSize();
		int fin = Math.min(inicio + request.getSize(), nElementos);
		
		List<Tarjeta> contenido = lista.subList(inicio, fin);
		
		return new Page<>(contenido, request.getPage(), request.getSize(), nPags, nElementos);
	}
}
