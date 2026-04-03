package es.um.pds.tarjetas.infrastructure.adapters.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;

@Repository
public class RepositorioTablerosMemoria implements RepositorioTableros {
	// Atributos
	private final Map<TableroId, Tablero> baseDatos = new HashMap<>();
	
	@Override
	public void guardar(Tablero tab) {
		baseDatos.put(tab.getIdentificador(), tab);
	}
	
	@Override
	public Optional<Tablero> buscarPorId(TableroId id) {
		return Optional.ofNullable(baseDatos.get(id));
	}
	
	@Override
	public Optional<Tablero> buscarPorURL(String tokenURL) {
		if (tokenURL == null || tokenURL.isBlank()) {
			throw new IllegalArgumentException("El token de URL no puede ser nulo o vacío");
		}

		return baseDatos.values().stream()
				.filter(tab -> tokenURL.equals(tab.getTokenURL()))
				.findFirst();
	}
	
	@Override
	public void eliminarPorId(TableroId id) {
		baseDatos.remove(id);
	}
	
	@Override
	public List<TableroId> listarIdsPorUsuario(UsuarioId usuarioId) {
		List<TableroId> idList = new ArrayList<>();
		for(Tablero tab : baseDatos.values()) {
			if(tab.getCreador().equals(usuarioId)) {
				idList.add(tab.getIdentificador());
			}
		}
		return idList;
	}
}
